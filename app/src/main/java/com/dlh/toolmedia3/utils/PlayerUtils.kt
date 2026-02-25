package com.dlh.toolmedia3.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 播放器相关工具类
 * 
 */
object PlayerUtils {
    /**
     * 获取状态栏高度
     */
    @SuppressLint("InternalInsetResource")
    fun getStatusBarHeight(context: Context): Double {
        var statusBarHeight = 0
        //获取status_bar_height资源的ID
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight.toDouble()
    }

    /**
     * 获取竖屏下状态栏高度
     */
    fun getStatusBarHeightPortrait(context: Context): Double {
        var statusBarHeight = 0
        //获取status_bar_height_portrait资源的ID
        val resourceId =
            context.getResources().getIdentifier("status_bar_height_portrait", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId)
        }
        return statusBarHeight.toDouble()
    }

    /**
     * 获取NavigationBar的高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        if (!hasNavigationBar(context)) {
            return 0
        }
        val resources = context.getResources()
        val resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen", "android"
        )
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 是否存在NavigationBar
     */
    fun hasNavigationBar(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display = getWindowManager(context)!!.getDefaultDisplay()
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.x != size.x || realSize.y != size.y
        } else {
            val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !(menu || back)
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(context: Context, isIncludeNav: Boolean): Int {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().widthPixels + getNavigationBarHeight(
                context
            )
        } else {
            return context.getResources().getDisplayMetrics().widthPixels
        }
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(context: Context, isIncludeNav: Boolean): Int {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight(
                context
            )
        } else {
            return context.getResources().getDisplayMetrics().heightPixels
        }
    }

    /**
     * 获取Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    /**
     * dp转为px
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    /**
     * sp转为px
     */
    fun sp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            dpValue,
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     */
    fun getWindowManager(context: Context): WindowManager? {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    }

    /**
     * 边缘检测
     */
    fun isEdge(context: Context, e: MotionEvent): Boolean {
        val edgeSize = dp2px(context, 40f)
        return e.getRawX() < edgeSize || e.getRawX() > getScreenWidth(
            context,
            true
        ) - edgeSize || e.getRawY() < edgeSize || e.getRawY() > getScreenHeight(
            context,
            true
        ) - edgeSize
    }


    const val NO_NETWORK: Int = 0
    const val NETWORK_CLOSED: Int = 1
    const val NETWORK_ETHERNET: Int = 2
    const val NETWORK_WIFI: Int = 3
    const val NETWORK_MOBILE: Int = 4
    val NETWORK_UNKNOWN: Int = -1

    /**
     * 判断当前网络类型
     */
    fun getNetworkType(context: Context): Int {
        //改为context.getApplicationContext()，防止在Android 6.0上发生内存泄漏
        val connectMgr = context.getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (connectMgr == null) {
            return NO_NETWORK
        }

        val networkInfo = connectMgr.getActiveNetworkInfo()
        if (networkInfo == null) {
            // 没有任何网络
            return NO_NETWORK
        }
        if (!networkInfo.isConnected()) {
            // 网络断开或关闭
            return NETWORK_CLOSED
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            // 以太网网络
            return NETWORK_ETHERNET
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
            return NETWORK_WIFI
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
            when (networkInfo.getSubtype()) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_NR -> return NETWORK_MOBILE
            }
        }
        // 未知网络
        return NETWORK_UNKNOWN
    }

    @get:Deprecated("不在使用，后期谷歌可能封掉该接口")
    @get:SuppressLint("PrivateApi")
    val application: Application?
        /**
         * 通过反射获取Application
         * 
         */
        get() {
            try {
                return Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null) as Application?
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    val currentSystemTime: String
        /**
         * 获取当前系统时间
         */
        get() {
            val simpleDateFormat =
                SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date()
            return simpleDateFormat.format(date)
        }


    /**
     * 格式化时间
     */
    fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
    /**
     * 获取集合的快照
     */
    fun <T> getSnapshot(other: MutableCollection<T?>): MutableList<T?> {
        val result: MutableList<T?> = ArrayList<T?>(other.size)
        for (item in other) {
            if (item != null) {
                result.add(item)
            }
        }
        return result
    }
}
