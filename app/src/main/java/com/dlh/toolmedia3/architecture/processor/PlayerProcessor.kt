package com.dlh.toolmedia3.architecture.processor

import android.content.Context
import android.media.AudioManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.Tracks
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
import com.dlh.toolmedia3.architecture.event.PlayerEvent
import com.dlh.toolmedia3.architecture.intent.PlayerIntent
import com.dlh.toolmedia3.architecture.state.PlayerState
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.util.ErrorCodeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@androidx.media3.common.util.UnstableApi

/**
 * 播放器处理器
 * 负责处理播放器状态管理和事件分发
 */
class PlayerProcessor(
    private val context: Context,
    private val player: Player? = null
) {
    /**
     * 播放器状态流
     */
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()
    
    /**
     * 播放器事件流
     */
    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    val events: Flow<PlayerEvent> = _events.asSharedFlow()
    
    // 音频管理器
    private val audioManager: AudioManager = context.getSystemService(AudioManager::class.java)
    
    // 协程作用域
    private val scope = CoroutineScope(Dispatchers.Main)
    
    init {
        // 监听ExoPlayer状态变化
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val currentState = _state.value
                
                // 只有当状态真正改变时才更新状态和发送事件
                if (currentState.playState != playbackState) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            _state.update { it.copy(playState = playbackState, isPlaying = false) }
                        }
                        Player.STATE_BUFFERING -> {
                            _state.update { it.copy(playState = playbackState, isPlaying = false) }
                        }
                        Player.STATE_READY -> {
                            _state.update { it.copy(playState = playbackState, isPlaying = player.isPlaying) }
                        }
                        Player.STATE_ENDED -> {
                            _state.update { it.copy(playState = playbackState, isPlaying = false) }
                        }
                    }
                    
                    // 发送播放状态变化事件
                    scope.launch {
                        _events.emit(PlayerEvent.PlaybackStateChanged(playbackState))
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val currentState = _state.value
                
                // 只有当播放状态真正改变时才更新状态和发送事件
                if (currentState.isPlaying != isPlaying) {
                    _state.update { it.copy(isPlaying = isPlaying) }
                    
                    // 发送播放状态变化事件
                    scope.launch {
                        _events.emit(PlayerEvent.IsPlayingChanged(isPlaying))
                    }
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {
                val currentState = _state.value
                val newPosition = player.currentPosition
                
                // 只有当位置真正改变时才更新状态
                if (newPosition != currentState.currentPosition) {
                    _state.update { it.copy(currentPosition = newPosition) }
                }
                
                // 发送位置不连续事件
                scope.launch {
                    _events.emit(PlayerEvent.PositionDiscontinuity(reason))
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    val currentState = _state.value
                    val newDuration = player.duration
                    
                    // 只有当时长真正改变时才更新状态
                    if (newDuration != currentState.duration) {
                        _state.update { it.copy(duration = newDuration) }
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                // 构建错误信息
                val errorMessage = buildErrorMessage(error)
                val currentState = _state.value
                
                // 只有当错误信息真正改变时才更新状态
                if (errorMessage != currentState.errorMessage) {
                    _state.update { it.copy(errorMessage = errorMessage) }
                    
                    // 发送播放器错误事件
                    scope.launch {
                        _events.emit(PlayerEvent.PlayerError(error))
                    }
                }
            }

            // onSeekProcessed 方法在某些版本的 ExoPlayer 中可能不存在
            // 暂时注释掉，避免编译错误
            // override fun onSeekProcessed() {
            //     // 发送跳转处理完成事件
            //     scope.launch {
            //         _events.emit(PlayerEvent.SeekProcessed(player.currentPosition))
            //     }
            // }

            override fun onTracksChanged(tracks: Tracks) {
                // 发送轨道变化事件
                scope.launch {
                    _events.emit(PlayerEvent.TracksChanged(tracks))
                }
            }
            
            /**
             * 构建错误信息
             * @param error 播放异常
             * @return 格式化的错误信息
             */
            private fun buildErrorMessage(error: PlaybackException): String {
                // 首先检查错误原因
                val cause = error.cause
                if (cause is HttpDataSource.HttpDataSourceException) {
                    // HTTP 数据源错误
                    return handleHttpError(cause)
                }
                
                // 记录详细错误信息
                println("[PlayerError] Type: ${error.errorCode}, Message: ${error.message}")
                
                // 使用错误代码管理工具类进行错误信息生成
                return ErrorCodeManager.getErrorMessage(context, error)
            }
            
            /**
             * 处理 HTTP 错误
             * @param httpError HTTP 数据源异常
             * @return 格式化的 HTTP 错误信息
             */
            private fun handleHttpError(httpError: HttpDataSource.HttpDataSourceException): String {
                return when (httpError) {
                    is InvalidResponseCodeException -> {
                        // HTTP 响应码错误
                        val responseCode = httpError.responseCode
                        val responseMessage = httpError.responseMessage ?: ""
                        val errorUrl = httpError.dataSpec.uri.toString()
                        // 记录详细的 HTTP 错误信息
                        println("[HttpError] Code: $responseCode, Message: $responseMessage, URL: $errorUrl")
                        ErrorCodeManager.getHttpErrorMessage(context, responseCode, responseMessage)
                    }
                    else -> {
                        // 其他 HTTP 错误
                        val rootCause = httpError.cause
                        val errorUrl = httpError.dataSpec.uri.toString()
                        // 记录详细的连接错误信息
                        println("[HttpError] Connection error: ${httpError.message}, URL: $errorUrl, Cause: $rootCause")
                        ErrorCodeManager.getHttpConnectionErrorMessage(context, rootCause)
                    }
                }
            }
        })
    }
    
    /**
     * 处理播放器意图
     * @param intent 播放器意图
     * @param scope 协程作用域
     */
    fun processIntent(intent: PlayerIntent, scope: CoroutineScope) {
        when (intent) {
            is PlayerIntent.Play -> handlePlay(intent, scope)
            is PlayerIntent.Pause -> handlePause()
            is PlayerIntent.Stop -> handleStop()
            is PlayerIntent.SeekTo -> handleSeekTo(intent)
            is PlayerIntent.SetVolume -> handleSetVolume(intent)
            is PlayerIntent.SetPlaybackSpeed -> handleSetPlaybackSpeed(intent)
            is PlayerIntent.ChangeQuality -> handleChangeQuality(intent, scope)
            is PlayerIntent.LoadQualities -> handleLoadQualities(scope)
            is PlayerIntent.ToggleFullScreen -> handleToggleFullScreen()
            is PlayerIntent.EnterPictureInPicture -> handleEnterPictureInPicture()
            is PlayerIntent.ExitPictureInPicture -> handleExitPictureInPicture()
            is PlayerIntent.SetScreenMode -> handleSetScreenMode(intent)
            is PlayerIntent.SetScaleType -> handleSetScaleType(intent)
            is PlayerIntent.SetVideoRotation -> handleSetVideoRotation(intent)
            is PlayerIntent.SetVideoMirror -> handleSetVideoMirror(intent)
            is PlayerIntent.AdjustVolume -> handleAdjustVolume(intent)
            is PlayerIntent.AdjustBrightness -> handleAdjustBrightness(intent, scope)
            is PlayerIntent.AdjustProgress -> handleAdjustProgress(intent)
            is PlayerIntent.SetAudioOnlyMode -> handleSetAudioOnlyMode(intent)
            is PlayerIntent.SavePlaybackProgress -> handleSavePlaybackProgress(scope)
            is PlayerIntent.LoadPlaybackProgress -> handleLoadPlaybackProgress(scope)
            is PlayerIntent.PreloadVideo -> handlePreloadVideo(intent, scope)
            is PlayerIntent.DownloadVideo -> handleDownloadVideo(intent, scope)
            is PlayerIntent.PauseDownload -> handlePauseDownload(intent, scope)
            is PlayerIntent.CancelDownload -> handleCancelDownload(intent, scope)
            is PlayerIntent.GetDownloadStatus -> handleGetDownloadStatus(scope)
            is PlayerIntent.GetDownloadedList -> handleGetDownloadedList(scope)
            is PlayerIntent.DeleteDownloadedVideo -> handleDeleteDownloadedVideo(intent, scope)
            is PlayerIntent.ClearCache -> handleClearCache(scope)
            is PlayerIntent.GetCacheSize -> handleGetCacheSize(scope)
            is PlayerIntent.SetMaxCacheSize -> handleSetMaxCacheSize(intent)
            is PlayerIntent.TakeScreenshot -> handleTakeScreenshot(scope)
            is PlayerIntent.OnResume -> handleOnResume()
            is PlayerIntent.OnPause -> handleOnPause()
            is PlayerIntent.OnDestroy -> handleOnDestroy()
            is PlayerIntent.NetworkStateChanged -> handleNetworkStateChanged(intent)
            is PlayerIntent.SetCutoutAdapted -> handleSetCutoutAdapted(intent)
        }
    }
    
    /**
     * 处理播放逻辑
     * @param intent 播放意图
     * @param scope 协程作用域
     */
    private fun handlePlay(intent: PlayerIntent.Play, scope: CoroutineScope) {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (!currentState.isPlaying || currentState.errorMessage != null) {
            _state.update { it.copy(isPlaying = true, errorMessage = null) }
            player?.play()
        }
    }
    
    /**
     * 处理暂停逻辑
     */
    private fun handlePause() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (currentState.isPlaying) {
            _state.update { it.copy(isPlaying = false) }
            player?.pause()
        }
    }
    
    /**
     * 处理停止逻辑
     */
    private fun handleStop() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (currentState.isPlaying) {
            _state.update { it.copy(isPlaying = false) }
            player?.stop()
        }
    }
    
    /**
     * 处理跳转逻辑
     * @param intent 跳转意图
     */
    private fun handleSeekTo(intent: PlayerIntent.SeekTo) {
        player?.seekTo(intent.position)
    }
    
    /**
     * 处理设置音量逻辑
     * @param intent 设置音量意图
     */
    private fun handleSetVolume(intent: PlayerIntent.SetVolume) {
        val currentState = _state.value
        
        // 只有当音量真正改变时才更新状态
        if (currentState.volume != intent.volume) {
            _state.update { it.copy(volume = intent.volume) }
            
            // 使用AudioManager设置系统音量
            audioManager?.let {
                val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = (intent.volume * maxVolume).toInt()
                it.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            }
        }
    }
    
    /**
     * 处理设置播放速度逻辑
     * @param intent 设置播放速度意图
     */
    private fun handleSetPlaybackSpeed(intent: PlayerIntent.SetPlaybackSpeed) {
        val currentState = _state.value
        
        // 只有当播放速度真正改变时才更新状态
        if (currentState.playbackSpeed != intent.speed) {
            _state.update { it.copy(playbackSpeed = intent.speed) }
            player?.playbackParameters = player?.playbackParameters?.withSpeed(intent.speed) ?: androidx.media3.common.PlaybackParameters.DEFAULT
        }
    }
    
    /**
     * 处理清晰度切换逻辑
     * @param intent 切换清晰度意图
     * @param scope 协程作用域
     */
    private fun handleChangeQuality(intent: PlayerIntent.ChangeQuality, scope: CoroutineScope) {
        val currentState = _state.value
        
        // 只有当清晰度真正改变时才更新状态
        if (currentState.currentQuality != intent.quality) {
            _state.update { it.copy(currentQuality = intent.quality) }
            // 这里需要根据清晰度切换逻辑更新媒体源
            // 实际实现中需要根据不同清晰度的URL重新设置媒体源
            // 例如: val newUrl = getUrlByQuality(intent.quality)
            // player.setMediaItem(MediaItem.fromUri(newUrl))
            // player.prepare()
            // player.play()
        }
    }
    
    /**
     * 处理加载清晰度列表逻辑
     * @param scope 协程作用域
     */
    private fun handleLoadQualities(scope: CoroutineScope) {
        val qualities = listOf(
            context.getString(R.string.quality_smooth),
            context.getString(R.string.quality_standard),
            context.getString(R.string.quality_high),
            context.getString(R.string.quality_super),
            context.getString(R.string.quality_blue)
        )
        _state.update { it.copy(availableQualities = qualities) }
    }
    
    /**
     * 处理全屏切换逻辑
     */
    private fun handleToggleFullScreen() {
        val currentState = _state.value
        val newState = !currentState.isFullScreen
        
        // 只有当状态真正改变时才更新状态
        if (currentState.isFullScreen != newState) {
            _state.update { it.copy(isFullScreen = newState) }
            // 这里需要通过回调或其他方式通知UI执行屏幕切换
        }
    }
    
    /**
     * 处理进入画中画逻辑
     */
    private fun handleEnterPictureInPicture() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (!currentState.isPictureInPicture) {
            _state.update { it.copy(isPictureInPicture = true) }
            // 这里需要通过回调或其他方式通知UI执行画中画切换
        }
    }
    
    /**
     * 处理退出画中画逻辑
     */
    private fun handleExitPictureInPicture() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (currentState.isPictureInPicture) {
            _state.update { it.copy(isPictureInPicture = false) }
            // 这里需要通过回调或其他方式通知UI执行画中画切换
        }
    }
    
    /**
     * 处理设置屏幕模式逻辑
     * @param intent 设置屏幕模式意图
     */
    private fun handleSetScreenMode(intent: PlayerIntent.SetScreenMode) {
        val currentState = _state.value
        
        // 只有当屏幕模式真正改变时才更新状态
        if (currentState.screenMode != intent.mode) {
            _state.update { it.copy(screenMode = intent.mode) }
            // 这里需要根据模式设置屏幕状态
        }
    }
    
    /**
     * 处理设置缩放类型逻辑
     * @param intent 设置缩放类型意图
     */
    private fun handleSetScaleType(intent: PlayerIntent.SetScaleType) {
        val currentState = _state.value
        
        // 只有当缩放类型真正改变时才更新状态
        if (currentState.scaleType != intent.scaleType) {
            _state.update { it.copy(scaleType = intent.scaleType) }
            // 这里需要根据缩放类型设置播放器的缩放模式
        }
    }
    
    /**
     * 处理设置视频旋转逻辑
     * @param intent 设置视频旋转意图
     */
    private fun handleSetVideoRotation(intent: PlayerIntent.SetVideoRotation) {
        val currentState = _state.value
        
        // 只有当旋转角度真正改变时才更新状态
        if (currentState.videoRotation != intent.rotation) {
            _state.update { it.copy(videoRotation = intent.rotation) }
        }
    }
    
    /**
     * 处理设置视频镜像逻辑
     * @param intent 设置视频镜像意图
     */
    private fun handleSetVideoMirror(intent: PlayerIntent.SetVideoMirror) {
        val currentState = _state.value
        
        // 只有当镜像状态真正改变时才更新状态
        if (currentState.isVideoMirrored != intent.mirrored) {
            _state.update { it.copy(isVideoMirrored = intent.mirrored) }
        }
    }
    
    /**
     * 处理音量调节逻辑
     * @param intent 音量调节意图
     */
    private fun handleAdjustVolume(intent: PlayerIntent.AdjustVolume) {
        val currentState = _state.value
        val newVolume = (currentState.volume + intent.delta).coerceIn(0f, 1f)
        
        // 只有当音量真正改变时才更新状态
        if (currentState.volume != newVolume) {
            _state.update { it.copy(volume = newVolume) }
            
            // 使用AudioManager设置系统音量
            audioManager?.let {
                val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = (newVolume * maxVolume).toInt()
                it.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            }
        }
    }
    
    /**
     * 处理亮度调节逻辑
     * @param intent 亮度调节意图
     * @param scope 协程作用域
     */
    private fun handleAdjustBrightness(intent: PlayerIntent.AdjustBrightness, scope: CoroutineScope) {
        val currentState = _state.value
        val newBrightness = (currentState.brightness + intent.delta).coerceIn(0f, 1f)
        
        // 只有当亮度真正改变时才更新状态
        if (currentState.brightness != newBrightness) {
            _state.update { it.copy(brightness = newBrightness) }
            // 这里需要通过系统API设置亮度
        }
    }
    
    /**
     * 处理进度调节逻辑
     * @param intent 进度调节意图
     */
    private fun handleAdjustProgress(intent: PlayerIntent.AdjustProgress) {
        val currentState = _state.value
        val newPosition = (currentState.currentPosition + intent.delta).coerceIn(0, currentState.duration)
        
        // 只有当位置真正改变时才执行跳转
        if (newPosition != currentState.currentPosition) {
            player?.seekTo(newPosition)
        }
    }
    
    /**
     * 处理设置纯音频模式逻辑
     * @param intent 设置纯音频模式意图
     */
    private fun handleSetAudioOnlyMode(intent: PlayerIntent.SetAudioOnlyMode) {
        val currentState = _state.value
        
        // 只有当纯音频模式真正改变时才更新状态
        if (currentState.isAudioOnlyMode != intent.enabled) {
            _state.update { it.copy(isAudioOnlyMode = intent.enabled) }
        }
    }
    
    /**
     * 处理保存播放进度逻辑
     * @param scope 协程作用域
     */
    private fun handleSavePlaybackProgress(scope: CoroutineScope) {
        // 处理保存播放进度逻辑
        // 这里需要实现持久化存储
    }
    
    /**
     * 处理加载播放进度逻辑
     * @param scope 协程作用域
     */
    private fun handleLoadPlaybackProgress(scope: CoroutineScope) {
        // 处理加载播放进度逻辑
        // 这里需要从持久化存储中加载
    }
    
    /**
     * 处理预加载视频逻辑
     * @param intent 预加载视频意图
     * @param scope 协程作用域
     */
    private fun handlePreloadVideo(intent: PlayerIntent.PreloadVideo, scope: CoroutineScope) {
        // 处理预加载视频逻辑
    }
    
    /**
     * 处理视频下载逻辑
     * @param intent 视频下载意图
     * @param scope 协程作用域
     */
    private fun handleDownloadVideo(intent: PlayerIntent.DownloadVideo, scope: CoroutineScope) {
        // 处理视频下载逻辑
        // 这里需要使用WorkManager实现后台下载
    }
    
    /**
     * 处理暂停下载逻辑
     * @param intent 暂停下载意图
     * @param scope 协程作用域
     */
    private fun handlePauseDownload(intent: PlayerIntent.PauseDownload, scope: CoroutineScope) {
        // 处理暂停下载逻辑
    }
    
    /**
     * 处理取消下载逻辑
     * @param intent 取消下载意图
     * @param scope 协程作用域
     */
    private fun handleCancelDownload(intent: PlayerIntent.CancelDownload, scope: CoroutineScope) {
        // 处理取消下载逻辑
    }
    
    /**
     * 处理获取下载状态逻辑
     * @param scope 协程作用域
     */
    private fun handleGetDownloadStatus(scope: CoroutineScope) {
        // 处理获取下载状态逻辑
    }
    
    /**
     * 处理获取已下载列表逻辑
     * @param scope 协程作用域
     */
    private fun handleGetDownloadedList(scope: CoroutineScope) {
        // 处理获取已下载列表逻辑
    }
    
    /**
     * 处理删除已下载视频逻辑
     * @param intent 删除已下载视频意图
     * @param scope 协程作用域
     */
    private fun handleDeleteDownloadedVideo(intent: PlayerIntent.DeleteDownloadedVideo, scope: CoroutineScope) {
        // 处理删除已下载视频逻辑
    }
    
    /**
     * 处理清除缓存逻辑
     * @param scope 协程作用域
     */
    private fun handleClearCache(scope: CoroutineScope) {
        val currentState = _state.value
        
        // 只有当缓存大小真正改变时才更新状态
        if (currentState.cacheSize != 0L) {
            _state.update { it.copy(cacheSize = 0L) }
        }
    }
    
    /**
     * 处理获取缓存大小逻辑
     * @param scope 协程作用域
     */
    private fun handleGetCacheSize(scope: CoroutineScope) {
        // 处理获取缓存大小逻辑
        // 这里需要实现获取实际缓存大小的逻辑
    }
    
    /**
     * 处理设置最大缓存容量逻辑
     * @param intent 设置最大缓存容量意图
     */
    private fun handleSetMaxCacheSize(intent: PlayerIntent.SetMaxCacheSize) {
        val currentState = _state.value
        
        // 只有当最大缓存容量真正改变时才更新状态
        if (currentState.maxCacheSize != intent.size) {
            _state.update { it.copy(maxCacheSize = intent.size) }
        }
    }
    
    /**
     * 处理截图逻辑
     * @param scope 协程作用域
     */
    private fun handleTakeScreenshot(scope: CoroutineScope) {
        // 处理截图逻辑
        // 这里需要实现视频截图功能
    }
    
    /**
     * 处理恢复逻辑
     */
    private fun handleOnResume() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (!currentState.isPlaying) {
            player?.play()
            _state.update { it.copy(isPlaying = true) }
        }
    }
    
    /**
     * 处理暂停逻辑
     */
    private fun handleOnPause() {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (currentState.isPlaying) {
            player?.pause()
            _state.update { it.copy(isPlaying = false) }
        }
    }
    
    /**
     * 处理销毁逻辑
     */
    private fun handleOnDestroy() {
        player?.release()
    }
    
    /**
     * 处理网络状态变化逻辑
     * @param intent 网络状态变化意图
     */
    private fun handleNetworkStateChanged(intent: PlayerIntent.NetworkStateChanged) {
        val currentState = _state.value
        
        // 只有当网络状态真正改变时才更新状态
        if (currentState.networkType != intent.networkType) {
            _state.update { it.copy(networkType = intent.networkType) }
            
            // 发送网络状态变化事件
            scope.launch {
                _events.emit(PlayerEvent.NetworkStateChanged(intent.networkType))
            }
        }
    }
    
    /**
     * 处理设置刘海屏适配逻辑
     * @param intent 设置刘海屏适配意图
     */
    private fun handleSetCutoutAdapted(intent: PlayerIntent.SetCutoutAdapted) {
        val currentState = _state.value
        
        // 只有当刘海屏适配状态真正改变时才更新状态
        if (currentState.isCutoutAdapted != intent.adapted) {
            _state.update { it.copy(isCutoutAdapted = intent.adapted) }
        }
    }
    
    /**
     * 更新播放器实时状态
     * @param position 当前播放位置
     * @param bufferedPosition 缓冲位置
     * @param duration 总时长
     * @param isPlaying 是否正在播放
     */
    fun updatePlayerState(position: Long, bufferedPosition: Long, duration: Long, isPlaying: Boolean) {
        val currentState = _state.value
        
        // 只有当状态真正改变时才更新状态
        if (currentState.currentPosition != position ||
            currentState.bufferedPosition != bufferedPosition ||
            currentState.duration != duration ||
            currentState.isPlaying != isPlaying) {
            
            _state.update {
                it.copy(
                    currentPosition = position,
                    bufferedPosition = bufferedPosition,
                    duration = duration,
                    isPlaying = isPlaying
                )
            }
        }
    }
    
    /**
     * 清除错误状态
     */
    fun clearErrorState() {
        val currentState = _state.value
        
        // 只有当错误状态真正存在时才清除
        if (currentState.errorMessage != null) {
            _state.update { it.copy(errorMessage = null) }
        }
    }
}
