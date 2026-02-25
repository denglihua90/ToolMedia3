package com.dlh.toolmedia3.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LifecycleOwner
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.architecture.viewmodel.PlayerViewModel
import com.dlh.toolmedia3.databinding.LayoutVideoControllerBinding
import com.dlh.toolmedia3.utils.CutoutUtil.allowDisplayToCutout
import com.dlh.toolmedia3.utils.PlayerUtils

@androidx.media3.common.util.UnstableApi

/**
 * 视频控制器
 */
class VideoController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: PlayerViewModel,
    private val gestureConfig: GestureConfig = GestureConfig(),
    private val onControllerVisibilityChanged: ((Boolean) -> Unit)? = null
) {

    private val binding: LayoutVideoControllerBinding =
        LayoutVideoControllerBinding.inflate(LayoutInflater.from(context))
    private val handler = Handler(Looper.getMainLooper())
    private var isVisible = true
    private var isDragging = false
    private var hideControllerRunnable = Runnable { hideController() }

    // 手势相关变量
    private var lastX = 0f
    private var lastY = 0f
    private var startX = 0f
    private var startY = 0f
    private var currentGesture = GestureType.NONE
    private var brightness = 0f
    private var volume = 0f
    private var progress = 0L
    private var hideGestureFeedbackRunnable = Runnable { hideGestureFeedback() }

    // 手势检测器
    private val gestureDetector: GestureDetector
    private var isLandscape = false // 是否为横屏模式

    // 手势类型枚举
    enum class GestureType {
        NONE,
        BRIGHTNESS,
        VOLUME,
        PROGRESS
    }

    companion object {
        // 手势反馈显示时长
        const val GESTURE_FEEDBACK_DURATION = 1500L
    }

    /**
     * 手势配置参数
     */
    data class GestureConfig(
        // 手势阈值
        val gestureThreshold: Int = 10,
        // 灵敏度系数
        val gestureSensitivity: Float = 0.8f,
        val progressGestureSensitivity: Float = 0.6f,
        val volumeGestureSensitivity: Float = 1.5f,
        val brightnessGestureSensitivity: Float = 0.8f,
        // 启用/禁用手势
        val enableDoubleTap: Boolean = true,
        val enableHorizontalSwipe: Boolean = true,
        val enableVerticalSwipe: Boolean = true,
        // 音量范围
        val volumeMin: Float = 0.0f,
        val volumeMax: Float = 1.0f,
        // 亮度范围
        val brightnessMin: Float = 0.0f,
        val brightnessMax: Float = 1.0f
    )

    init {
        // 初始化手势检测器
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    startX = e.x
                    startY = e.y
                    lastX = e.x
                    lastY = e.y
                    currentGesture = GestureType.NONE

                    // 记录当前值
                    brightness = viewModel.state.value.brightness
                    volume = viewModel.state.value.volume
                    progress = viewModel.state.value.currentPosition

                    return true
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    // 只在横屏模式下处理手势操作
                    if (!isLandscape) return false

                    if (e1 == null) return false

                    val x = e2.x
                    val y = e2.y
                    val deltaX = x - lastX
                    val deltaY = y - lastY

                    // 计算滑动距离
                    val swipeDistance =
                        Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

                    // 如果滑动距离超过阈值，才认为是手势操作
                if (swipeDistance > gestureConfig.gestureThreshold && currentGesture == GestureType.NONE) {
                    // 根据滑动方向和位置确定手势类型
                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        // 上下滑动
                        if (gestureConfig.enableVerticalSwipe) {
                            currentGesture = getGestureType(x, y)
                        }
                    } else {
                        // 左右滑动
                        if (gestureConfig.enableHorizontalSwipe) {
                            currentGesture = GestureType.PROGRESS
                        }
                    }
                }

                when (currentGesture) {
                    GestureType.BRIGHTNESS -> {
                        // 亮度调节（上下滑动）
                        val brightnessDelta = 
                            -deltaY / getScreenHeight() * 2 * gestureConfig.brightnessGestureSensitivity
                        val newBrightness = (brightness + brightnessDelta).coerceIn(
                            gestureConfig.brightnessMin,
                            gestureConfig.brightnessMax
                        )
                        brightness = newBrightness // 更新本地亮度变量，确保后续计算基于最新值
                        setBrightness(newBrightness)
                        showBrightnessFeedback(newBrightness)
                    }

                    GestureType.VOLUME -> {
                        // 音量调节（上下滑动）
                        // 使用增量deltaY进行音量调节，这样可以更精确地控制音量变化
                        val volumeDelta = 
                            -deltaY / getScreenHeight() * 2 * gestureConfig.volumeGestureSensitivity
                        val newVolume = (volume + volumeDelta).coerceIn(
                            gestureConfig.volumeMin,
                            gestureConfig.volumeMax
                        )
                        volume = newVolume // 更新本地音量变量，确保后续计算基于最新值
                        viewModel.setVolume(newVolume)
                        showVolumeFeedback(newVolume)
                    }

                    GestureType.PROGRESS -> {
                        // 进度调节（左右滑动）
                        val duration = viewModel.state.value.duration

                        // 检查持续时间是否有效
                        if (duration > 0) {
                            // 计算总滑动距离（从初始位置到当前位置）
                            val totalDeltaX = x - startX
                            val progressDelta = 
                                (totalDeltaX / getScreenWidth() * duration * gestureConfig.progressGestureSensitivity).toLong()
                            val newProgress = (progress + progressDelta).coerceIn(0L, duration)

                            // 设置isDragging为true，避免updateControllerState覆盖我们的修改
                            isDragging = true

                            // 实时更新控制器进度条
                            updateSeekBarProgress(newProgress, duration)
                            showProgressFeedback(newProgress, duration)

                            // 暂停自动隐藏控制器
                            removeHideControllerTimer()
                        }
                    }
                    else -> {}
                }

                    lastX = x
                    lastY = y
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    // 单击事件：切换控制器可见性
                    toggleControllerVisibility()
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    // 双击事件：播放/暂停
                    if (gestureConfig.enableDoubleTap) {
                        if (viewModel.state.value.isPlaying) {
                            viewModel.pause()
                        } else {
                            viewModel.play()
                        }
                        resetHideControllerTimer()
                    }
                    return true
                }
            })

        initViews()
        initListeners()
        updateControllerVisibility(true)
    }

    /**
     * 获取控制器视图
     */
    fun getView(): View {
        return binding.root
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        // 初始化进度条
        binding.seekBar.max = 100
        
        // 设置返回按钮初始可见性为显示
        binding.btnBack.visibility = View.VISIBLE

    }
    


    /**
     * 初始化监听器
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        // 播放/暂停按钮
        binding.btnPlayPause.setOnClickListener {
            if (viewModel.state.value.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
            resetHideControllerTimer()
        }

        // 进度条
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.state.value.duration
                    val position = (progress * duration / 100).coerceAtMost(duration)
                    binding.tvCurrentTime.text = PlayerUtils.formatTime(position)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isDragging = true
                removeHideControllerTimer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isDragging = false
                val duration = viewModel.state.value.duration
                val position = (binding.seekBar.progress * duration / 100).coerceAtMost(duration)
                viewModel.seekTo(position)
                resetHideControllerTimer()
            }
        })


        // 全屏按钮
        binding.btnFullscreen.setOnClickListener {
            viewModel.toggleFullScreen()

            resetHideControllerTimer()
        }

        // 清晰度按钮
        binding.btnQuality.setOnClickListener {
            viewModel.loadQualities()
            // 显示清晰度选择对话框
            val qualities = viewModel.state.value.availableQualities
            val currentQuality = viewModel.state.value.currentQuality
            if (qualities.isNotEmpty()) {
                val dialog = QualityDialog(context, qualities, currentQuality) {
                    viewModel.changeQuality(it)
                }
                dialog.show()
            }
            resetHideControllerTimer()
        }

        // 播放速度按钮
        binding.btnSpeed.setOnClickListener {
            // 显示播放速度选择对话框
            showSpeedDialog()
            resetHideControllerTimer()
        }

        // 底部播放速度按钮
        binding.btnSpeedIv.setOnClickListener {
            // 显示播放速度选择对话框
            showSpeedDialog()
            resetHideControllerTimer()
        }

        // 返回按钮
        binding.btnBack.setOnClickListener {
            val isFullScreen = viewModel.state.value.isFullScreen
            val activity = PlayerUtils.scanForActivity(context)
            
            if (isFullScreen) {
                // 全屏状态：退出全屏
                viewModel.toggleFullScreen()
            } else if (isLandscape) {
                // 横屏状态：切换到竖屏
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }else {
                // 非全屏/横屏状态：退出应用
                activity?.finish()
            }
            resetHideControllerTimer()
        }

        // 触摸事件监听器（用于手势操作）
        binding.root.setOnTouchListener { v, event ->
            // 使用手势检测器处理触摸事件
            val handled = gestureDetector.onTouchEvent(event)

            // 处理手势结束事件
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                handleGestureEnd(event)
            }

            return@setOnTouchListener handled
        }
    }

    /**
     * 处理手势结束事件
     */
    private fun handleGestureEnd(event: MotionEvent) {
        if (!isLandscape) return
        
        // 处理进度调节手势的结束
        if (currentGesture == GestureType.PROGRESS) {
            // 使用初始位置和最终位置计算总滑动距离
            val totalDeltaX = event.x - startX
            val progressDelta = 
                (totalDeltaX / getScreenWidth() * viewModel.state.value.duration * gestureConfig.progressGestureSensitivity).toLong()
            val finalProgress = 
                (progress + progressDelta).coerceIn(0L, viewModel.state.value.duration)
            viewModel.seekTo(finalProgress)
            showProgressFeedback(finalProgress, viewModel.state.value.duration)
            updateSeekBarProgress(finalProgress, viewModel.state.value.duration)
        }

        // 重置手势状态
        resetGestureState()

        // 操作完成后，重启控制器自动隐藏
        if (isVisible && viewModel.state.value.isPlaying) {
            resetHideControllerTimer()
        }
    }

    /**
     * 重置手势状态
     */
    private fun resetGestureState() {
        currentGesture = GestureType.NONE
        isDragging = false
        hideGestureFeedback()
    }

    /**
     * 更新控制器状态
     */
    fun updateControllerState() {
        val state = viewModel.state.value

        // 更新播放/暂停按钮
        if (state.isPlaying) {
            binding.btnPlayPause.setImageResource(R.drawable.baseline_pause_24)
        } else {
            binding.btnPlayPause.setImageResource(R.drawable.baseline_play_arrow_24)
        }

        // 更新进度条
        if (!isDragging) {
            val duration = state.duration
            val position = state.currentPosition
            val bufferedPosition = state.bufferedPosition

            if (duration > 0) {
                binding.seekBar.progress = (position * 100 / duration).toInt()
                binding.seekBar.secondaryProgress = (bufferedPosition * 100 / duration).toInt()
                binding.tvCurrentTime.text = PlayerUtils.formatTime(position)
                binding.tvDuration.text = PlayerUtils.formatTime(duration)
            }
        }


        // 更新全屏按钮
        if (state.isFullScreen) {
            binding.btnFullscreen.setImageResource(R.drawable.baseline_fullscreen_exit_24)
        } else {
            binding.btnFullscreen.setImageResource(R.drawable.baseline_fullscreen_24)
        }
    }

    /**
     * 显示控制器
     */
    fun showController() {
        if (!isVisible) {
            updateControllerVisibility(true)
        }
        resetHideControllerTimer()
    }

    /**
     * 隐藏控制器
     */
    fun hideController() {
        if (isVisible) {
            updateControllerVisibility(false)
        }
    }

    /**
     * 切换控制器可见性
     */
    fun toggleControllerVisibility() {
        updateControllerVisibility(!isVisible)
        if (isVisible) {
            resetHideControllerTimer()
        } else {
            removeHideControllerTimer()
        }
    }

    /**
     * 更新控制器可见性
     */
    private fun updateControllerVisibility(visible: Boolean) {
        isVisible = visible
        val visibility = if (visible) View.VISIBLE else View.GONE

        binding.topController.visibility = visibility
//        binding.centerController.visibility = visibility
        binding.bottomController.visibility = visibility

        onControllerVisibilityChanged?.invoke(visible)

    }

    /**
     * 重置隐藏控制器计时器
     */
    fun resetHideControllerTimer() {
        removeHideControllerTimer()
        if (isVisible && viewModel.state.value.isPlaying) {
            handler.postDelayed(hideControllerRunnable, 3000) // 3秒后自动隐藏
        }
    }

    /**
     * 移除隐藏控制器计时器
     */
    fun removeHideControllerTimer() {
        handler.removeCallbacks(hideControllerRunnable)
    }

    /**
     * 设置视频标题
     */
    fun setVideoTitle(title: String) {
        binding.tvTitle.text = title
    }

    /**
     * 更新屏幕方向状态
     */
    fun updateOrientation(isLandscape: Boolean) {
        this.isLandscape = isLandscape
        // 控制返回按钮的显示/隐藏
        binding.btnBack.visibility = View.VISIBLE
    }

    /**
     * 销毁
     */
    fun destroy() {
        removeHideControllerTimer()
        removeHideGestureFeedbackTimer()
    }

    /**
     * 显示播放速度选择对话框
     */
    private fun showSpeedDialog() {
        val speeds = arrayOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
        val speedValues = arrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("选择播放速度")
        builder.setItems(speeds) { dialog, which ->
            val selectedSpeed = speedValues[which]
            viewModel.setPlaybackSpeed(selectedSpeed)
            dialog.dismiss()
        }
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


    /**
     * 获取手势类型
     */
    private fun getGestureType(x: Float, y: Float): GestureType {
        val width = binding.root.width

        // 计算点击区域
        val areaWidth = width / 3

        return when {
            x < areaWidth -> {
                GestureType.BRIGHTNESS
            }

            x > width - areaWidth -> {
                GestureType.VOLUME
            }

            else -> {
                GestureType.PROGRESS
            }
        }
    }

    /**
     * 获取屏幕宽度
     */
    private fun getScreenWidth(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    private fun getScreenHeight(): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 设置亮度
     */
    private fun setBrightness(brightness: Float) {
        // 注意：设置系统亮度需要权限
        // 实际实现中，我们可以通过以下方式设置当前窗口的亮度
        val activity = PlayerUtils.scanForActivity(context)
        activity?.window?.attributes?.screenBrightness = brightness
        activity?.window?.attributes = activity.window.attributes
        showBrightnessFeedback(brightness)
    }

    /**
     * 显示音量调节反馈
     */
    private fun showVolumeFeedback(volume: Float) {
        val percent = (volume * 100).toInt()

        // 设置图标
        binding.ivIcon.setImageResource(
            if (percent == 0) {
                R.drawable.baseline_volume_off_36
            } else {
                R.drawable.baseline_volume_up_36
            }
        )

        // 设置百分比
        binding.tvPercent.text = "$percent%"

        // 设置进度条
        binding.proPercent.progress = percent

        // 显示反馈
        showGestureFeedback()
    }

    /**
     * 显示亮度调节反馈
     */
    private fun showBrightnessFeedback(brightness: Float) {
        val percent = (brightness * 100).toInt()

        // 设置图标
        binding.ivIcon.setImageResource(R.drawable.baseline_brightness_36)

        // 设置百分比
        binding.tvPercent.text = "$percent%"

        // 设置进度条
        binding.proPercent.progress = percent

        // 显示反馈
        showGestureFeedback()
    }

    /**
     * 显示进度调节反馈
     */
    private fun showProgressFeedback(position: Long, duration: Long) {
        val percent = if (duration > 0) ((position * 100) / duration).toInt() else 0

        // 设置图标
        binding.ivIcon.setImageResource(R.drawable.baseline_fast_forward_36)

        // 设置时间
        val positionStr = PlayerUtils.formatTime(position)
        val durationStr = PlayerUtils.formatTime(duration)
        binding.tvPercent.text = "$positionStr / $durationStr"

        // 设置进度条
        binding.proPercent.progress = percent

        // 显示反馈
        showGestureFeedback()
    }

    /**
     * 显示手势反馈
     */
    private fun showGestureFeedback() {
        binding.gestureController.visibility = View.VISIBLE
        resetHideGestureFeedbackTimer()
    }

    /**
     * 隐藏手势反馈
     */
    private fun hideGestureFeedback() {
        binding.gestureController.visibility = View.GONE
    }

    /**
     * 重置隐藏手势反馈计时器
     */
    private fun resetHideGestureFeedbackTimer() {
        removeHideGestureFeedbackTimer()
        handler.postDelayed(hideGestureFeedbackRunnable, GESTURE_FEEDBACK_DURATION)
    }

    /**
     * 移除隐藏手势反馈计时器
     */
    private fun removeHideGestureFeedbackTimer() {
        handler.removeCallbacks(hideGestureFeedbackRunnable)
    }

    /**
     * 更新进度条进度
     */
    private fun updateSeekBarProgress(position: Long, duration: Long) {
        if (duration > 0) {
            binding.seekBar.progress = (position * 100 / duration).toInt()
            binding.tvCurrentTime.text = PlayerUtils.formatTime(position)
            binding.tvDuration.text = PlayerUtils.formatTime(duration)
        }
    }
}
