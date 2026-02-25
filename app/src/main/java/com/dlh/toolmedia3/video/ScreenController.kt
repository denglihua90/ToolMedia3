package com.dlh.toolmedia3.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dlh.toolmedia3.utils.CutoutUtil.adaptCutoutAboveAndroidP
import com.dlh.toolmedia3.utils.CutoutUtil.addCutoutPaddingToController
import com.dlh.toolmedia3.utils.CutoutUtil.resetControllerPadding
import com.dlh.toolmedia3.utils.PlayerUtils


/**
 * 屏幕控制器
 */
class ScreenController(private val activity: Activity) {
    
    // 是否处于全屏状态
    private var isFullScreen = false
    
    // 播放器容器
    private var playerContainer: View? = null
    
    // 播放器容器的原始父容器
    private var originalParent: ViewGroup? = null
    
    // 播放器容器的原始布局参数
    private var originalLayoutParams: ViewGroup.LayoutParams? = null
    
    // 方向监听器
    private var orientationEventListener: OrientationHelper? = null
    // 记录上一次的方向
    private var mOrientation = -1
    
    /**
     * 切换全屏
     */
    fun toggleFullScreen(playerContainer: View, isCutoutAdapted: Boolean = false) {
        if (isFullScreen) {
            exitFullScreen()
        } else {
            enterFullScreen(playerContainer, isCutoutAdapted)
        }
    }
    
    /**
     * 进入全屏
     */
    fun enterFullScreen(playerContainer: View, isCutoutAdapted: Boolean = false) {
        if (isFullScreen) return
        
        this.playerContainer = playerContainer
        
        // 保存原始状态
        originalParent = playerContainer.parent as? ViewGroup
        originalLayoutParams = playerContainer.layoutParams

        val decorView = getDecorView() ?: return

        isFullScreen = true
        adaptCutoutAboveAndroidP(activity, isCutoutAdapted)

        // 隐藏系统UI
        hideSysBar(decorView)
        
        // 隐藏其他UI元素
        hideOtherUIElements()
        
        // 从当前容器中移除播放器视图
        originalParent?.removeView(playerContainer)
        
        // 将播放器视图添加到DecorView中
        decorView.addView(playerContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        
        // 设置为传感器方向，允许根据设备方向自动旋转
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        
        // 初始化并启动方向监听器
        initOrientationEventListener()
        
        // 为控制器添加刘海屏安全区域padding
        if (isCutoutAdapted) {
            // 延迟一点时间，确保控制器已经完全初始化
            android.os.Handler().postDelayed({
                findAndAdaptController(playerContainer)
            }, 100)
        }
    }
    
    /**
     * 查找并适配控制器
     */
    private fun findAndAdaptController(playerContainer: View) {
        // 直接为controller_view添加刘海屏安全区域padding
        val controllerView = playerContainer.findViewById<ViewGroup>(com.dlh.toolmedia3.R.id.controller_view)
        if (controllerView != null) {
            // 使用CutoutUtil的方法为控制器添加安全区域padding
            addCutoutPaddingToController(activity, controllerView)
        }
    }
    
    /**
     * 重置控制器padding
     */
    private fun resetControllerPadding(playerContainer: View) {
        // 直接重置controller_view的padding
        val controllerView = playerContainer.findViewById<ViewGroup>(com.dlh.toolmedia3.R.id.controller_view)
        if (controllerView != null) {
            com.dlh.toolmedia3.utils.CutoutUtil.resetControllerPadding(controllerView)
        }
    }
    
    /**
     * 退出全屏
     */
    fun exitFullScreen() {
        if (!isFullScreen) return
        
        val decorView = getDecorView()
        val currentPlayerContainer = playerContainer
        val currentOriginalParent = originalParent
        if (decorView == null || currentPlayerContainer == null || currentOriginalParent == null) return
        
        isFullScreen = false

        adaptCutoutAboveAndroidP(activity,false)
        // 显示系统UI
        showSysBar(decorView)
        
        // 显示其他UI元素
        showOtherUIElements()
        
        // 从DecorView中移除播放器视图
        decorView.removeView(currentPlayerContainer)
        
        // 将播放器视图添加回原始容器
        currentOriginalParent.addView(currentPlayerContainer, originalLayoutParams)
        
        // 重置控制器padding
        resetControllerPadding(currentPlayerContainer)
        
        // 设置竖屏
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // 停止并释放方向监听器
        stopOrientationEventListener()
        
        // 重置状态
        playerContainer = null
        originalParent = null
        originalLayoutParams = null
    }
    
    /**
     * 是否全屏
     */
    fun isFullScreen(): Boolean {
        return isFullScreen
    }
    
    /**
     * 是否为横屏模式
     */
    fun isLandscape(): Boolean {
        // 检查当前屏幕方向
        val orientation = activity.resources.configuration.orientation
        return orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE || isFullScreen()
    }
    
    /**
     * 进入画中画模式
     */
    fun enterPictureInPictureMode(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // 使用 PictureInPictureParams (Android 9+)
                val params = android.app.PictureInPictureParams.Builder().build()
                activity.enterPictureInPictureMode(params)
            } else {
                // 兼容 Android 8
                activity.enterPictureInPictureMode()
            }
            return true
        }
        return false
    }
    
    /**
     * 退出画中画模式
     */
    fun exitPictureInPictureMode() {
        // 画中画模式下，系统会自动处理退出逻辑
        // 这里可以添加一些额外的处理
    }
    
    /**
     * 是否在画中画模式
     */
    fun isInPictureInPictureMode(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return activity.isInPictureInPictureMode
        }
        return false
    }
    
    /**
     * 隐藏系统UI
     */
    private fun hideSysBar(decorView: ViewGroup) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // 使用 WindowInsetsController (Android 11+)
            val windowInsetsController = decorView.windowInsetsController
            if (windowInsetsController != null) {
                windowInsetsController.hide(android.view.WindowInsets.Type.systemBars())
                windowInsetsController.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // 兼容 Android 11 以下版本
            var uiOptions = decorView.systemUiVisibility
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            
            decorView.systemUiVisibility = uiOptions
        }
        
        // 已经通过 WindowInsetsController 处理了系统栏的隐藏，不再需要 FLAG_FULLSCREEN
    }
    
    /**
     * 显示系统UI
     */
    private fun showSysBar(decorView: ViewGroup) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // 使用 WindowInsetsController (Android 11+)
            val windowInsetsController = decorView.windowInsetsController
            if (windowInsetsController != null) {
                windowInsetsController.show(android.view.WindowInsets.Type.systemBars())
            }
        } else {
            // 兼容 Android 11 以下版本
            var uiOptions = decorView.systemUiVisibility
            uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }
            
            decorView.systemUiVisibility = uiOptions
        }
        
        // 已经通过 WindowInsetsController 处理了系统栏的显示，不再需要 clearFlags
    }
    
    /**
     * 获取DecorView
     */
    private fun getDecorView(): ViewGroup? {
        return activity.window.decorView as? ViewGroup
    }
    
    /**
     * 处理窗口焦点变化
     */
    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus && isFullScreen) {
            val decorView = getDecorView()
            decorView?.let { hideSysBar(it) }
        }
    }
    
    /**
     * 隐藏其他UI元素
     */
    private fun hideOtherUIElements() {
        // 隐藏Activity中的其他UI元素
        val contentView = activity.findViewById<android.view.View>(android.R.id.content) as? android.view.ViewGroup
        contentView?.let {
            // 遍历所有子视图，隐藏除了播放器容器之外的所有视图
            for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)
                if (child != playerContainer) {
                    child.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    /**
     * 显示其他UI元素
     */
    private fun showOtherUIElements() {
        // 显示Activity中的其他UI元素
        val contentView = activity.findViewById<android.view.View>(android.R.id.content) as? android.view.ViewGroup
        contentView?.let {
            // 遍历所有子视图，显示所有视图
            for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)
                child.visibility = android.view.View.VISIBLE
            }
        }
    }
    
    /**
     * 初始化方向监听器
     */
    private fun initOrientationEventListener() {
        orientationEventListener = object : OrientationHelper(activity) {
            override fun onOrientationChanged(orientation: Int) {
                // 只在全屏模式下处理方向变化
                if (!isFullScreen) return
                
                // 检查Activity是否正在结束
                if (activity.isFinishing) return

                // 记录用户手机上一次放置的位置
                val lastOrientation = mOrientation

                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    // 手机平放时，检测不到有效的角度
                    // 重置为原始位置 -1
                    mOrientation = -1
                    return
                }

                // 只处理横屏向左、横屏向右两个方向
                if (orientation in 81..<100) {
                    val o = activity.requestedOrientation
                    // 手动切换横竖屏
                    if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) return
                    if (mOrientation == 90) return
                    // 90度，用户右侧横屏拿着手机
                    mOrientation = 90
                    onOrientationReverseLandscape(activity)
                } else if (orientation in 261..<280) {
                    val o = activity.requestedOrientation
                    // 手动切换横竖屏
                    if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) return
                    if (mOrientation == 270) return
                    // 270度，用户左侧横屏拿着手机
                    mOrientation = 270
                    onOrientationLandscape(activity)
                }

            }
        }
        
        // 启动方向监听器
        orientationEventListener?.enable()
    }
    
    /**
     * 处理横屏向左方向
     */
    private fun onOrientationLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // 方向变化后重新适配刘海屏
        reAdaptCutoutAfterOrientationChange()
    }
    
    /**
     * 处理横屏向右方向
     */
    private fun onOrientationReverseLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        // 方向变化后重新适配刘海屏
        reAdaptCutoutAfterOrientationChange()
    }
    
    /**
     * 方向变化后重新适配刘海屏
     */
    private fun reAdaptCutoutAfterOrientationChange() {
        // 延迟一点时间，确保方向变化完成
        android.os.Handler().postDelayed({ 
            val currentPlayerContainer = playerContainer
            if (isFullScreen && currentPlayerContainer != null) {
                // 重置控制器padding
                val controllerView = currentPlayerContainer.findViewById<ViewGroup>(com.dlh.toolmedia3.R.id.controller_view)
                if (controllerView != null) {
                    com.dlh.toolmedia3.utils.CutoutUtil.resetControllerPadding(controllerView)
                }
                // 重新适配刘海屏
                findAndAdaptController(currentPlayerContainer)
            }
        }, 200)
    }
    
    /**
     * 停止方向监听器
     */
    private fun stopOrientationEventListener() {
        orientationEventListener?.disable()
        orientationEventListener = null
    }
}