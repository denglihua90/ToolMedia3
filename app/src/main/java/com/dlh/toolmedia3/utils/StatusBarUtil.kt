package com.dlh.toolmedia3.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


object StatusBarUtil {

    /**
     *  显示状态栏
     *  @param isVisible 是否显示
     *  StatusBarUtil.setStatusBarVisible(this, false)
     */
    fun setStatusBarVisible(activity: Activity, isVisible: Boolean) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, isVisible)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            if (isVisible) {
                controller.show(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            }
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     *  设置状态栏颜色
     *  这里还是直接操作window的statusBarColor
     *  StatusBarUtil.setStatusBarColor(this,Color.BLUE)
     */
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
        activity.window.statusBarColor = color
    }

    /**
     *  设置状态栏字体颜色
     *  此api只能控制字体颜色为 黑/白
     *  @param color 这里的颜色是指背景颜色
     *  val backgroundColor=Color.WHITE
    val backgroundColor=Color.BLUE
    StatusBarUtil.setStatusBarColor(this,backgroundColor)     //设置状态栏颜色
    StatusBarUtil.setStatusBarTextColor(this,backgroundColor) // 字体颜色
     */
    fun setStatusBarTextColor(activity: Activity, @ColorInt backgroundColor: Int) {
        // 计算颜色亮度
        val luminanceValue = ColorUtils.calculateLuminance(backgroundColor)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).let { controller ->
            if (backgroundColor == Color.TRANSPARENT) {
                // 如果是透明颜色就默认设置成黑色
                controller.isAppearanceLightStatusBars = true
            } else {
                // 通过亮度来决定字体颜色是黑还是白
                controller.isAppearanceLightStatusBars = luminanceValue >= 0.5
            }
        }
    }

    /**
     * 状态栏字颜色
     * isLightMode true  白色
     * isLightMode false  黑色
     */
    fun setStatusBarTextColor(activity: Activity, isLightMode: Boolean) {
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).let { controller ->
            controller.isAppearanceLightStatusBars = !isLightMode
        }
    }


    /**
     *  沉浸式状态栏
     *  @param contentColor 内容颜色:获取内容的颜色，传入系统，它自动修改字体颜色(黑/白)
     *  val contentColor = Color.parseColor("#0000FF")
    StatusBarUtil.immersiveStatusBar(this, contentColor)
     */
    fun immersiveStatusBar(activity: Activity, @ColorInt contentColor: Int) {
        val window = activity.window.apply {
            statusBarColor = Color.TRANSPARENT
        }
        // 设置状态栏字体颜色
        setStatusBarTextColor(activity, contentColor)
        // 把内容放到系统窗口里面 可以去了解一下Window和decorView的关系
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

//    // TODO: StatusBarHeight  获取状态栏高度
//    fun getStatusBarHeight(context: Context): Int {
//
//        var statusBarHeight = -1
//        //获取status_bar_height_portrait资源的ID
//        var resourceId=
//            context.resources.getIdentifier("status_bar_height_portrait", "dimen", "android")
//        if (resourceId > 0) {
//            //根据资源ID获取响应的尺寸值
//            statusBarHeight = try {
//                context.resources.getDimensionPixelSize(statusBarHeight)
//            }catch (e:Exception){
//                context.resources.getDimensionPixelSize(R.dimen.dp_24);
//            }
//        }else{
//            statusBarHeight= context.resources.getDimensionPixelSize(R.dimen.dp_24);
//        }
//
//        return statusBarHeight
//    }


}