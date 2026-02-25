package com.dlh.toolmedia3.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * 刘海屏工具
 */
object CutoutUtil {
    /**
     * 是否为允许全屏界面显示内容到刘海区域的刘海屏机型（与AndroidManifest中配置对应）
     */
    fun allowDisplayToCutout(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 9.0系统全屏界面默认会保留黑边，不允许显示内容到刘海区域
            val window = activity.window
            val windowInsets = window.decorView.rootWindowInsets ?: return false
            val displayCutout = windowInsets.displayCutout ?: return false
            val boundingRects = displayCutout.boundingRects
            return boundingRects.isNotEmpty()
        } else {
            return hasCutoutHuawei(activity)
                    || hasCutoutOPPO(activity)
                    || hasCutoutVIVO(activity)
                    || hasCutoutXIAOMI(activity)
        }
    }

    /**
     * 是否是华为刘海屏机型
     */
    private fun hasCutoutHuawei(activity: Activity): Boolean {
        if (!Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)) {
            return false
        }
        try {
            val cl = activity.classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            if (HwNotchSizeUtil != null) {
                val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
                return get.invoke(HwNotchSizeUtil) as Boolean
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 是否是oppo刘海屏机型
     */
    private fun hasCutoutOPPO(activity: Activity): Boolean {
        if (!Build.MANUFACTURER.equals("oppo", ignoreCase = true)) {
            return false
        }
        return activity.packageManager
            .hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    /**
     * 是否是vivo刘海屏机型
     */
    @SuppressLint("PrivateApi")
    private fun hasCutoutVIVO(activity: Activity): Boolean {
        if (!Build.MANUFACTURER.equals("vivo", ignoreCase = true)) {
            return false
        }
        try {
            val cl = activity.classLoader
            val ftFeatureUtil = cl.loadClass("android.util.FtFeature")
            if (ftFeatureUtil != null) {
                val get = ftFeatureUtil.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                return get.invoke(ftFeatureUtil, 0x00000020) as Boolean
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 是否是小米刘海屏机型
     */
    @SuppressLint("PrivateApi")
    private fun hasCutoutXIAOMI(activity: Activity): Boolean {
        if (!Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)) {
            return false
        }
        try {
            val cl = activity.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Int::class.javaPrimitiveType
            val getInt = SystemProperties.getMethod("getInt", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = "ro.miui.notch"
            params[1] = 0
            val hasCutout = getInt.invoke(SystemProperties, *params) as Int
            return hasCutout == 1
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 适配刘海屏，针对Android P以上系统
     */
    fun adaptCutoutAboveAndroidP(context: Context?, isAdapt: Boolean) {
        val activity = PlayerUtils.scanForActivity(context) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = activity.window.attributes
            if (isAdapt) {
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            } else {
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
            activity.window.setAttributes(lp)
        }
    }

    /**
     * 为控制器添加刘海屏安全区域padding
     */
    fun addCutoutPaddingToController(activity: Activity, controller: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = activity.window.decorView.rootWindowInsets ?: return
            val displayCutout = windowInsets.displayCutout ?: return
            
            val safeInsetLeft = displayCutout.safeInsetLeft
            val safeInsetTop = displayCutout.safeInsetTop
            val safeInsetRight = displayCutout.safeInsetRight
            val safeInsetBottom = displayCutout.safeInsetBottom
            
            if (safeInsetTop > 0 || safeInsetLeft > 0 || safeInsetRight > 0) {
                controller.setPadding(
                    safeInsetLeft,
                    safeInsetTop,
                    safeInsetRight,
                    safeInsetBottom
                )
            }
        }
    }

    /**
     * 重置控制器padding
     */
    fun resetControllerPadding(controller: View) {
        controller.setPadding(
            0,
            0,
            0,
            0
        )
    }


}
