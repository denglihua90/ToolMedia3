package com.dlh.toolmedia3.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.dlh.toolmedia3.architecture.processor.PlayerProcessor
import com.dlh.toolmedia3.architecture.viewmodel.PlayerViewModel
import com.dlh.toolmedia3.databinding.LayoutBaseVideoViewBinding
import com.dlh.toolmedia3.utils.PlayerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
open class BaseVideoView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr,defStyleRes), LifecycleObserver {
    protected  var  binding: LayoutBaseVideoViewBinding=LayoutBaseVideoViewBinding.inflate(LayoutInflater.from(getContext()), this, true)
    
    // 播放器实例
    protected lateinit var player: ExoPlayer
    
    // PlayerView
    protected lateinit var playerView: PlayerView

    
    // MVI架构组件
    protected lateinit var processor: PlayerProcessor
    protected lateinit var viewModel: PlayerViewModel
    
    // 生命周期管理
    private var lifecycleOwner: LifecycleOwner? = null
    
    // 视频控制器
    protected lateinit var videoController: VideoController
    
    // 屏幕控制器
    lateinit var screenController: ScreenController
    
    // 播放进度管理器
    protected lateinit var progressManager: ProgressManager
    
    // 加载状态视图
    protected lateinit var loadingView: LoadingView
    
    // 处理器
    private val handler = Handler(Looper.getMainLooper())
    
    // 状态更新间隔（毫秒）
    private val STATE_UPDATE_INTERVAL = 100L
    
    // 状态更新任务
    private lateinit var stateUpdateRunnable: Runnable
    
    // 视频源URL
    private var videoUrl: String = ""
    private var videoTitle: String = ""
    
    // 自动保存播放进度的开关
    private var isAutoSaveProgressEnabled: Boolean = true
    
    /**
     * 初始化播放器
     */
    open fun initPlayer(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
        
        // 初始化PlayerView
        playerView = binding.playerView
        
        // 初始化ExoPlayer
        player = ExoPlayer.Builder(context).build()
        playerView.player = player
        
        // 禁用PlayerView的默认控制器和触摸处理
        playerView.useController = false
        
        // 初始化MVI架构组件
        processor = PlayerProcessor(context, player)
        viewModel = PlayerViewModel(processor)
        
        // 初始化视频控制器
        videoController = VideoController(context, lifecycleOwner, viewModel) {
            // 控制器可见性变化回调
        }
        binding.controllerView.addView(videoController.getView())

        // 初始化屏幕控制器
        val activity = PlayerUtils.scanForActivity(context)
        if (activity != null) {
            screenController = ScreenController(activity)
        }

        // 初始化播放进度管理器
        progressManager = ProgressManager(context)
        
        // 初始化加载状态视图
        loadingView = LoadingView(context)
        binding.loadingView.addView(loadingView)
        
        // 设置加载视图回调
        loadingView.onRetryListener = {
            // 重新加载视频
            if (videoUrl.isNotEmpty()) {
                setVideoSource(videoUrl, videoTitle)
                // 重新加载后自动开始播放
                play()
            }
        }
        
        loadingView.onBackListener = {
            // 退出全屏
            if (::screenController.isInitialized && screenController.isFullScreen()) {
                viewModel.sendIntent(com.dlh.toolmedia3.architecture.intent.PlayerIntent.ToggleFullScreen)
            }
        }
        
        // 初始化状态更新任务
        stateUpdateRunnable = Runnable {
            // 更新控制器状态
            updateControllerState()
            
            // 更新播放器状态到ViewModel
            if (::player.isInitialized && ::viewModel.isInitialized) {
                viewModel.updatePlayerState(
                    player.currentPosition,
                    player.bufferedPosition,
                    player.duration,
                    player.isPlaying
                )
            }
            
            // 继续下一次更新
            handler.postDelayed(stateUpdateRunnable, STATE_UPDATE_INTERVAL)
        }
        
        // 观察播放器状态
        observePlayerState()
        
        // 观察播放器事件
        observePlayerEvents()
        
        // 开始状态更新
        startStateUpdate()


    }
    
    /**
     * 观察播放器状态
     */
    protected open fun observePlayerState() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.state.collect {
                // 处理加载状态
                val isLandscape = if (::screenController.isInitialized) screenController.isLandscape() else false
                val bufferPercentage = if (it.duration > 0) ((it.bufferedPosition * 100) / it.duration).toInt() else 0
                
                // 更新加载视图状态
                if (::loadingView.isInitialized) {
                    loadingView.updateState(
                        it.playState,
                        bufferPercentage,
                        it.errorMessage,
                        isLandscape
                    )
                }
                
                // 处理屏幕控制状态变化
                if (::screenController.isInitialized) {
                    // 全屏状态变化
                    val isFullScreen = it.isFullScreen
                    if (isFullScreen != screenController.isFullScreen()) {
                        // 传递当前BaseVideoView作为播放器容器
                        screenController.toggleFullScreen(this@BaseVideoView, it.isCutoutAdapted)
                        
                        // 退出全屏时解锁屏幕
                        if (!isFullScreen) {
                            videoController.unlockScreen()
                        }
                    }
                    
                    // 画中画状态变化
                    val isPictureInPicture = it.isPictureInPicture
                    if (isPictureInPicture && !screenController.isInPictureInPictureMode()) {
                        screenController.enterPictureInPictureMode()
                    }
                    
                    // 更新屏幕方向状态
                    videoController.updateOrientation(isLandscape)

                }
                
//                // 处理缩放类型变化
//                // 暂时注释掉，需要确认Media3的正确常量名称
//                 when (it.scaleType) {
//                     0 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_FIT)
//                     1 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_FILL)
//                     2 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_ZOOM)
//                 }
                
                // 处理视频旋转和镜像
                updateVideoTransform(it.videoRotation, it.isVideoMirrored)
                
                // 处理音频模式
                if (it.isAudioOnlyMode) {
                    // 纯音频模式：隐藏视频画面
                    playerView.visibility = View.GONE
                } else {
                    // 正常模式：显示视频画面
                    playerView.visibility = View.VISIBLE
                }
            }
        }
    }
    
    /**
     * 观察播放器事件
     */
    protected open fun observePlayerEvents() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.events.collect {
                // 这里可以处理事件
            }
        }
    }
    
    /**
     * 设置视频源
     */
    open fun setVideoSource(url: String, title: String = "") {
        // 保存视频URL和标题
        this.videoUrl = url
        this.videoTitle = title
        
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        
        // 设置视频标题
        videoController.setVideoTitle(title)
        
        // 加载播放进度
        loadPlaybackProgress(url)
        
        // 自动保存进度（每30秒）
        if (isAutoSaveProgressEnabled) {
            startAutoSaveProgress(url)
        }

    }
    
    /**
     * 加载播放进度
     */
    private fun loadPlaybackProgress(videoUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val progress = progressManager.loadProgress(videoUrl)
            if (progress != null) {
                player.seekTo(progress)
            }
        }
    }
    
    /**
     * 开始自动保存进度
     */
    private fun startAutoSaveProgress(videoUrl: String) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (player.isPlaying) {
                    val position = player.currentPosition
                    val duration = player.duration
                    if (duration > 0) {
                        CoroutineScope(Dispatchers.IO).launch {
                            progressManager.saveProgress(videoUrl, position, duration)
                        }
                    }
                }
                handler.postDelayed(this, 30000) // 每30秒保存一次
            }
        }, 30000)
    }
    
    /**
     * 预加载视频
     */
    open fun preloadVideo(url: String, title: String = "") {
        viewModel.preloadVideo(url)
        // 使用PreloadManager进行预加载
        PreloadManager.getInstance(context).preloadVideo(url, title)
    }
    
    /**
     * 播放
     */
    open fun play() {
        viewModel.play()
    }
    
    /**
     * 暂停
     */
    open fun pause() {
        viewModel.pause()
    }
    
    /**
     * 停止
     */
    open fun stop() {
        viewModel.stop()
    }
    
    /**
     * 跳转到指定位置
     */
    open fun seekTo(position: Long) {
        viewModel.seekTo(position)
    }
    
    /**
     * 设置音量
     */
    open fun setVolume(volume: Float) {
        viewModel.setVolume(volume)
    }
    
    /**
     * 设置播放速度
     */
    open fun setPlaybackSpeed(speed: Float) {
        viewModel.setPlaybackSpeed(speed)
    }
    
    /**
     * 获取播放状态
     */
    open fun isPlaying(): Boolean {
        return player.isPlaying
    }
    
    /**
     * 获取当前播放位置
     */
    open fun getCurrentPosition(): Long {
        return player.currentPosition
    }
    
    /**
     * 获取视频总时长
     */
    open fun getDuration(): Long {
        return player.duration
    }
    
    /**
     * 获取缓冲位置
     */
    open fun getBufferedPosition(): Long {
        return player.bufferedPosition
    }
    
    /**
     * 更新控制器状态
     */
    protected open fun updateControllerState() {
        videoController.updateControllerState()
    }
    
    /**
     * 开始状态更新
     */
    protected open fun startStateUpdate() {
        handler.post(stateUpdateRunnable)
    }
    
    /**
     * 停止状态更新
     */
    protected open fun stopStateUpdate() {
        handler.removeCallbacks(stateUpdateRunnable)
    }
    
    // 移除handleTouchEvent方法，因为不再需要手势操作
    
    /**
     * 更新视频变换（旋转和镜像）
     */
    protected open fun updateVideoTransform(rotation: Int, mirrored: Boolean) {
        // 处理视频旋转
        // Media3的PlayerView会自动处理视频的旋转，基于MediaItem中的旋转信息
        // 我们可以通过修改当前播放的MediaItem来设置旋转
        
        // 处理镜像效果
        // 注意：Media3的PlayerView本身不直接支持镜像效果
        // 实际实现中，我们有几种方案：
        // 1. 使用自定义的TextureView并操作其矩阵
        // 2. 使用GLSurfaceView并通过OpenGL实现镜像
        // 3. 通过修改视频渲染管道实现
        
        // 这里我们提供一个占位实现，实际项目中需要根据具体需求选择合适的方案
        if (mirrored) {
            // 实现镜像效果
        } else {
            // 恢复正常效果
        }
    }
    
    /**
     * 释放播放器资源
     */
    open fun release() {
        stopStateUpdate()
        videoController.destroy()
        player.release()
        lifecycleOwner?.lifecycle?.removeObserver(this)
    }
    
    /**
     * 设置刘海屏适配
     */
    fun setCutoutAdapted(adapted: Boolean) {
        if (::viewModel.isInitialized) {
            viewModel.setCutoutAdapted(adapted)
        }
    }
    
    /**
     * 设置是否启用自动保存播放进度的功能
     */
    fun setAutoSaveProgressEnabled(enabled: Boolean) {
        isAutoSaveProgressEnabled = enabled
        // 如果启用自动保存进度，并且已经设置了视频源，则开始自动保存
        if (enabled && videoUrl.isNotEmpty()) {
            startAutoSaveProgress(videoUrl)
        }
    }
    
    /**
     * 获取是否启用自动保存播放进度的功能
     */
    fun isAutoSaveProgressEnabled(): Boolean {
        return isAutoSaveProgressEnabled
    }
    
    // 生命周期回调
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        viewModel.onResume()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
        viewModel.onPause()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        viewModel.onDestroy()
        release()
    }

}