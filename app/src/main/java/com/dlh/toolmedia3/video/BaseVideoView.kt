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
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.dlh.toolmedia3.ToolMediaApplication
import com.dlh.toolmedia3.architecture.event.PlayerEvent
import com.dlh.toolmedia3.architecture.processor.PlayerProcessor
import com.dlh.toolmedia3.architecture.state.PlayerState
import com.dlh.toolmedia3.architecture.viewmodel.PlayerViewModel
import com.dlh.toolmedia3.databinding.LayoutBaseVideoViewBinding
import com.dlh.toolmedia3.network.state.NetworkState
import com.dlh.toolmedia3.network.state.NetworkStateManager
import com.dlh.toolmedia3.util.PerformanceMonitor
import com.dlh.toolmedia3.util.PlayerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.dlh.toolmedia3.network.state.NetworkType as NetworkStateManagerNetworkType

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
    private var STATE_UPDATE_INTERVAL = 300L // 优化：增加更新间隔，减少UI线程负担
    
    // 上次状态更新时间
    private var lastUpdateTime = 0L
    
    // 状态更新任务
    private lateinit var stateUpdateRunnable: Runnable
    
    // 视频源URL
    private var videoUrl: String = ""
    private var videoTitle: String = ""
    
    // 自动保存播放进度的开关
    private var isAutoSaveProgressEnabled: Boolean = false
    
    // 网络状态管理器
    private lateinit var networkStateManager: NetworkStateManager
    private var networkStateObserver: androidx.lifecycle.Observer<NetworkState>? = null
    
    // 性能监控器
    private lateinit var performanceMonitor: PerformanceMonitor
    
    /**
     * 根据播放状态调整状态更新间隔
     */
    private fun adjustStateUpdateInterval() {
        STATE_UPDATE_INTERVAL = if (::player.isInitialized && (player.isPlaying || player.playbackState == Player.STATE_BUFFERING)) {
            300L // 播放时：300ms更新一次
        } else {
            1000L // 暂停时：1000ms更新一次
        }
    }
    
    /**
     * 初始化播放器
     */
    open fun initPlayer(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
        
        // 初始化PlayerView
        playerView = binding.playerView
        
        // 初始化网络状态管理器，用于检测网络类型
        networkStateManager = NetworkStateManager.getInstance(context)
        
        // 从播放器池获取播放器实例
        player = ToolMediaApplication.playerPool.acquire()
        playerView.player = player
        playerView.player?.playWhenReady = true
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
            // 根据播放状态调整更新间隔
            adjustStateUpdateInterval()
            
            // 更新控制器状态
            updateControllerState()
            
            // 更新播放器状态到ViewModel
            if (::player.isInitialized && ::viewModel.isInitialized) {
                // 优化：只在播放器真正播放时才更新状态，并且每600ms更新一次
                if ((player.isPlaying || player.playbackState == Player.STATE_BUFFERING) && 
                    System.currentTimeMillis() - lastUpdateTime > 600) {
                    viewModel.updatePlayerState(
                        player.currentPosition,
                        player.bufferedPosition,
                        player.duration,
                        player.isPlaying
                    )
                    lastUpdateTime = System.currentTimeMillis()
                }
            }
            
            // 继续下一次更新
            handler.postDelayed(stateUpdateRunnable, STATE_UPDATE_INTERVAL)
        }
        
        // 观察播放器状态
        observePlayerState()
        
        // 观察播放器事件
        observePlayerEvents()
        
        // 初始化网络状态观察
        initNetworkStateObserver()
        
        // 初始化性能监控器
        performanceMonitor = PerformanceMonitor(player)
        
        // 开始状态更新
        startStateUpdate()
        
        // 开始性能监控
        performanceMonitor.startMonitoring()


    }
    
    /**
     * 观察播放器状态
     */
    // 上一次的状态，用于状态过滤
    private var lastState: PlayerState? = null
    
    protected open fun observePlayerState() {
        lifecycleOwner?.lifecycleScope?.launch {
            viewModel.state.collect {
                // 状态过滤：只有当状态真正变化时才更新UI
                val isStateChanged = lastState != it
                lastState = it
                
                if (isStateChanged) {
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
                    
//                    // 处理缩放类型变化
//                    // 暂时注释掉，需要确认Media3的正确常量名称
//                     when (it.scaleType) {
//                         0 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_FIT)
//                         1 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_FILL)
//                         2 -> playerView.setResizeMode(androidx.media3.common.Player.RESIZE_MODE_ZOOM)
//                     }
                    
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
    }
    
    /**
     * 观察播放器事件
     */
    protected open fun observePlayerEvents() {
        lifecycleOwner?.lifecycleScope?.launch {
            viewModel.events.collect {
                when (it) {
                    // 播放状态变化事件
                    is PlayerEvent.PlaybackStateChanged -> {
                        // 处理播放状态变化
                        when (it.state) {
                            Player.STATE_IDLE -> {
                                // 空闲状态
                                if (::loadingView.isInitialized) {
                                    loadingView.updateState(it.state, 0, null, false)
                                }
                            }
                            Player.STATE_BUFFERING -> {
                                // 缓冲状态
                                if (::loadingView.isInitialized) {
                                    loadingView.updateState(it.state, 0, null, false)
                                }
                            }
                            Player.STATE_READY -> {
                                // 就绪状态
                                if (::loadingView.isInitialized) {
                                    loadingView.updateState(it.state, 100, null, false)
                                }
                            }
                            Player.STATE_ENDED -> {
                                // 播放结束
                                if (::loadingView.isInitialized) {
                                    loadingView.updateState(it.state, 100, null, false)
                                }
                            }
                        }
                    }
                    is PlayerEvent.IsPlayingChanged -> {
                        // 处理播放状态变化
                        // 播放状态变化会通过updateControllerState自动更新
                        videoController.updateControllerState()
                    }
                    is PlayerEvent.PositionDiscontinuity -> {
                        // 处理位置不连续事件
                        // 可以在这里处理跳转后的逻辑
                    }
                    is PlayerEvent.SeekProcessed -> {
                        // 处理跳转完成事件
                        // 可以在这里处理跳转完成后的逻辑
                    }
                    // 轨道变化事件
                    is PlayerEvent.TracksChanged -> {
                        // 处理轨道变化
                        // 可以在这里更新可用的轨道信息
                    }
                    // 错误事件
                    is PlayerEvent.PlayerError -> {
                        // 处理播放器错误
                        if (::loadingView.isInitialized) {
                            loadingView.updateState(Player.STATE_IDLE, 0, it.error.message, false)
                        }
                    }
                    // 下载事件
                    is PlayerEvent.DownloadStarted -> {
                        // 处理下载开始
                        // 可以显示下载开始的提示
                    }
                    is PlayerEvent.DownloadProgress -> {
                        // 处理下载进度
                        // 可以更新下载进度条
                    }
                    is PlayerEvent.DownloadCompleted -> {
                        // 处理下载完成
                        // 可以显示下载完成的提示
                    }
                    is PlayerEvent.DownloadFailed -> {
                        // 处理下载失败
                        // 可以显示下载失败的提示
                    }
                    is PlayerEvent.DownloadPaused -> {
                        // 处理下载暂停
                        // 可以显示下载暂停的提示
                    }
                    is PlayerEvent.DownloadCanceled -> {
                        // 处理下载取消
                        // 可以显示下载取消的提示
                    }
                    // 缓存事件
                    is PlayerEvent.CacheSizeChanged -> {
                        // 处理缓存大小变化
                        // 可以更新缓存大小显示
                    }
                    is PlayerEvent.CacheCleared -> {
                        // 处理缓存清除
                        // 可以显示缓存清除成功的提示
                    }
                    // 其他事件
                    is PlayerEvent.ScreenshotTaken -> {
                        // 处理截图完成
                        // 可以显示截图成功的提示
                    }
                    is PlayerEvent.PlaybackProgressSaved -> {
                        // 处理播放进度保存
                        // 可以记录保存的进度
                    }
                    is PlayerEvent.PlaybackProgressLoaded -> {
                        // 处理播放进度加载
                        // 可以跳转到加载的进度
                        seekTo(it.position)
                    }
                    is PlayerEvent.NetworkStateChanged -> {
                        // 处理网络状态变化
                        // 可以根据网络状态调整播放策略
                    }
                }
            }
        }
    }
    
    /**
     * 初始化网络状态观察
     */
    private fun initNetworkStateObserver() {
        networkStateObserver = androidx.lifecycle.Observer { networkState ->
            handleNetworkStateChange(networkState)
        }
        networkStateManager.networkState.observe(lifecycleOwner!!, networkStateObserver!!)
    }
    
    /**
     * 处理网络状态变化
     */
    private fun handleNetworkStateChange(networkState: NetworkState) {
        lifecycleOwner?.lifecycleScope?.launch(Dispatchers.Main) {
            // 记录网络状态变化
            println("[Network] State changed to: $networkState")
            
            // 根据网络类型调整播放策略和缓冲策略
            when (networkState) {
                is NetworkState.Connected -> {
                    val networkType = networkState.type
                    when (networkType) {
                        NetworkStateManagerNetworkType.WIFI, NetworkStateManagerNetworkType.CELLULAR, NetworkStateManagerNetworkType.ETHERNET -> {
                            // 网络恢复，自动恢复播放
                            if (!player.isPlaying && player.playbackState == Player.STATE_READY) {
                                // 延迟1秒再恢复播放，确保有足够的缓冲
                                handler.postDelayed({ 
                                    if (player.playbackState == Player.STATE_READY) {
                                        player.playWhenReady = true
                                    }
                                }, 1000)
                            }
                        }
                        else -> {
                            // 其他网络类型不做处理
                        }
                    }
                    // 发送网络状态变化事件
                    viewModel.networkStateChanged(getNetworkTypeOrdinal(networkType))
                }
                is NetworkState.Disconnected -> {
                    // 无网络，暂停播放
                    if (player.isPlaying) {
                        player.pause()
                    }
                    // 发送网络状态变化事件
                    viewModel.networkStateChanged(3) // NONE 对应的 ordinal
                }
            }
        }
    }
    
    /**
     * 将 NetworkStateManager 的网络类型转换为 BaseVideoView 中使用的 ordinal
     */
    private fun getNetworkTypeOrdinal(networkType: NetworkStateManagerNetworkType): Int {
        return when (networkType) {
            NetworkStateManagerNetworkType.WIFI -> 1 // WIFI
            NetworkStateManagerNetworkType.CELLULAR -> 2 // MOBILE
            NetworkStateManagerNetworkType.ETHERNET -> 0 // UNKNOWN
            NetworkStateManagerNetworkType.OTHER -> 0 // UNKNOWN
            NetworkStateManagerNetworkType.NONE -> 3 // NONE
        }
    }
    
    /**
     * 设置视频源
     */
    open fun setVideoSource(url: String, title: String = "") {
        // 保存视频URL和标题
        this.videoUrl = url
        this.videoTitle = title
        
        // 检查视频是否已经被预加载
        val preloadStatus = PreloadManager.getInstance(context).getPreloadStatus(url)
        
        // 显示预加载状态反馈
        if (preloadStatus == PreloadManager.PreloadStatus.PRELOADING) {
            // 显示预加载中状态
            loadingView.updateState(
                androidx.media3.common.Player.STATE_BUFFERING,
                0, "正在使用预加载内容...",
                if (::screenController.isInitialized) screenController.isLandscape() else false
            )
        } else if (preloadStatus == PreloadManager.PreloadStatus.COMPLETED) {
            // 显示预加载完成状态
            loadingView.updateState(
                androidx.media3.common.Player.STATE_BUFFERING,
                100, "使用预加载内容...",
                if (::screenController.isInitialized) screenController.isLandscape() else false
            )
        }
        
        // 清除错误状态
        if (::processor.isInitialized) {
            // 清除错误信息
            processor.clearErrorState()
        }
        
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
        lifecycleOwner?.lifecycleScope?.launch {
            val progress = progressManager.loadProgress(videoUrl)
            if (progress != null) {
//                player.seekTo(progress)
                seekTo(progress)

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
                        lifecycleOwner?.lifecycleScope?.launch(Dispatchers.IO) {
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
        // 保存视频URL和标题，与setVideoSource保持一致
        this.videoUrl = url
        this.videoTitle = title
        
        viewModel.preloadVideo(url)
        // 使用PreloadManager进行预加载
        PreloadManager.getInstance(context).preloadVideo(url, title)
    }
    
    /**
     * 加载并播放视频
     * 先预加载视频，然后设置视频源并开始播放
     */
    open fun loadAndPlayVideo(url: String, title: String = "") {
        // 预加载视频
        preloadVideo(url, title)
        // 设置视频源
        setVideoSource(url, title)
        // 开始播放
        play()
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
        // 停止性能监控
        if (::performanceMonitor.isInitialized) {
            performanceMonitor.stopMonitoring()
        }
        videoController.destroy()
        // 将播放器实例释放回池
        ToolMediaApplication.playerPool.release(player)
        // 移除网络状态观察
        networkStateObserver?.let {
            networkStateManager.networkState.removeObserver(it)
        }
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
     * 设置是否开启缓存
     */
    fun setCacheEnabled(enabled: Boolean) {
        if (::viewModel.isInitialized) {
            viewModel.setCacheEnabled(enabled)
            ToolMediaApplication.playerPool.setCacheEnabled(enabled)
        }
    }
    
    /**
     * 获取是否开启缓存
     */
    fun isCacheEnabled(): Boolean {
        return MediaCacheManager.getInstance(context).isCacheEnabled()
    }
    
    /**
     * 清空缓存
     */
    fun clearCache() {
        ToolMediaApplication.playerPool.clearCache()
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Long {
        return ToolMediaApplication.playerPool.getCacheSize()
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
    
    /**
     * 设置是否在横屏模式下显示选集按钮
     */
    fun setShowSelectionsInLandscape(enabled: Boolean) {
        if (::videoController.isInitialized) {
            videoController.setShowSelectionsInLandscape(enabled)
        }
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