package com.dlh.toolmedia3.architecture.processor

import android.content.Context
import android.media.AudioManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
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
import kotlinx.coroutines.withContext

@androidx.media3.common.util.UnstableApi

/**
 * 播放器处理器
 */
class PlayerProcessor(
    private val context: Context,
    private val player: Player? = null
) {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()
    
    private val _events = MutableSharedFlow<PlayerEvent>()
    val events: Flow<PlayerEvent> = _events.asSharedFlow()
    
    // 音频管理器
    private val audioManager: AudioManager = context.getSystemService(AudioManager::class.java)
    
    init {
        // 监听ExoPlayer状态变化
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
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
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPositionDiscontinuity(reason: Int) {
                if (player.currentPosition != _state.value.currentPosition) {
                    _state.update { it.copy(currentPosition = player.currentPosition) }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _state.update { it.copy(duration = player.duration) }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                // 详细的错误处理逻辑
                val errorMessage = buildErrorMessage(error)
                _state.update { it.copy(errorMessage = errorMessage) }
            }
            
            /**
             * 构建错误信息
             */
            private fun buildErrorMessage(error: PlaybackException): String {
                // 首先检查错误原因
                val cause = error.cause
                if (cause is HttpDataSource.HttpDataSourceException) {
                    // HTTP 数据源错误
                    return handleHttpError(cause)
                }
                
                // 使用错误代码管理工具类进行错误信息生成
                return ErrorCodeManager.getErrorMessage(context, error)
            }
            
            /**
             * 处理 HTTP 错误
             */
            private fun handleHttpError(httpError: HttpDataSource.HttpDataSourceException): String {
                return when (httpError) {
                    is InvalidResponseCodeException -> {
                        // HTTP 响应码错误
                        val responseCode = httpError.responseCode
                        val responseMessage = httpError.responseMessage ?: ""
                        ErrorCodeManager.getHttpErrorMessage(context, responseCode, responseMessage)
                    }
                    else -> {
                        // 其他 HTTP 错误
                        val rootCause = httpError.cause
                        ErrorCodeManager.getHttpConnectionErrorMessage(context, rootCause)
                    }
                }
            }
        })
    }
    
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
    
    private fun handlePlay(intent: PlayerIntent.Play, scope: CoroutineScope) {
        // 处理播放逻辑
        _state.update { it.copy(isPlaying = true) }
        player?.play()
    }
    
    private fun handlePause() {
        // 处理暂停逻辑
        _state.update { it.copy(isPlaying = false) }
        player?.pause()
    }
    
    private fun handleStop() {
        // 处理停止逻辑
        _state.update { it.copy(isPlaying = false) }
        player?.stop()
    }
    
    private fun handleSeekTo(intent: PlayerIntent.SeekTo) {
        // 处理跳转逻辑
        player?.seekTo(intent.position)
    }
    
    private fun handleSetVolume(intent: PlayerIntent.SetVolume) {
        // 处理设置音量逻辑
        _state.update { it.copy(volume = intent.volume) }
        
        // 使用AudioManager设置系统音量
        audioManager?.let {
            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = (intent.volume * maxVolume).toInt()
            it.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        }
    }
    
    private fun handleSetPlaybackSpeed(intent: PlayerIntent.SetPlaybackSpeed) {
        // 处理设置播放速度逻辑
        _state.update { it.copy(playbackSpeed = intent.speed) }
        player?.playbackParameters = player?.playbackParameters?.withSpeed(intent.speed) ?: androidx.media3.common.PlaybackParameters.DEFAULT
    }
    
    private fun handleChangeQuality(intent: PlayerIntent.ChangeQuality, scope: CoroutineScope) {
        // 处理清晰度切换逻辑
        _state.update { it.copy(currentQuality = intent.quality) }
        // 这里需要根据清晰度切换逻辑更新媒体源
        // 实际实现中需要根据不同清晰度的URL重新设置媒体源
        // 例如: val newUrl = getUrlByQuality(intent.quality)
        // player.setMediaItem(MediaItem.fromUri(newUrl))
        // player.prepare()
        // player.play()
    }
    
    private fun handleLoadQualities(scope: CoroutineScope) {
        // 处理加载清晰度列表逻辑
        val qualities = listOf(
            context.getString(R.string.quality_smooth),
            context.getString(R.string.quality_standard),
            context.getString(R.string.quality_high),
            context.getString(R.string.quality_super),
            context.getString(R.string.quality_blue)
        )
        _state.update { it.copy(availableQualities = qualities) }
    }
    
    private fun handleToggleFullScreen() {
        // 处理全屏切换逻辑
        val newState = !_state.value.isFullScreen
        _state.update { it.copy(isFullScreen = newState) }
        // 这里需要通过回调或其他方式通知UI执行屏幕切换
    }
    
    private fun handleEnterPictureInPicture() {
        // 处理进入画中画逻辑
        _state.update { it.copy(isPictureInPicture = true) }
        // 这里需要通过回调或其他方式通知UI执行画中画切换
    }
    
    private fun handleExitPictureInPicture() {
        // 处理退出画中画逻辑
        _state.update { it.copy(isPictureInPicture = false) }
        // 这里需要通过回调或其他方式通知UI执行画中画切换
    }
    
    private fun handleSetScreenMode(intent: PlayerIntent.SetScreenMode) {
        // 处理设置屏幕模式逻辑
        _state.update { it.copy(screenMode = intent.mode) }
        // 这里需要根据模式设置屏幕状态
    }
    
    private fun handleSetScaleType(intent: PlayerIntent.SetScaleType) {
        // 处理设置缩放类型逻辑
        _state.update { it.copy(scaleType = intent.scaleType) }
        // 这里需要根据缩放类型设置播放器的缩放模式
    }
    
    private fun handleSetVideoRotation(intent: PlayerIntent.SetVideoRotation) {
        // 处理设置视频旋转逻辑
        _state.update { it.copy(videoRotation = intent.rotation) }
    }
    
    private fun handleSetVideoMirror(intent: PlayerIntent.SetVideoMirror) {
        // 处理设置视频镜像逻辑
        _state.update { it.copy(isVideoMirrored = intent.mirrored) }
    }
    
    private fun handleAdjustVolume(intent: PlayerIntent.AdjustVolume) {
        // 处理音量调节逻辑
        val newVolume = (_state.value.volume + intent.delta).coerceIn(0f, 1f)
        _state.update { it.copy(volume = newVolume) }
        
        // 使用AudioManager设置系统音量
        audioManager?.let {
            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = (newVolume * maxVolume).toInt()
            it.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        }
    }
    
    private fun handleAdjustBrightness(intent: PlayerIntent.AdjustBrightness, scope: CoroutineScope) {
        // 处理亮度调节逻辑
        val newBrightness = (_state.value.brightness + intent.delta).coerceIn(0f, 1f)
        _state.update { it.copy(brightness = newBrightness) }
        // 这里需要通过系统API设置亮度
    }
    
    private fun handleAdjustProgress(intent: PlayerIntent.AdjustProgress) {
        // 处理进度调节逻辑
        val newPosition = (_state.value.currentPosition + intent.delta).coerceIn(0, _state.value.duration)
        player?.seekTo(newPosition)
    }
    
    private fun handleSetAudioOnlyMode(intent: PlayerIntent.SetAudioOnlyMode) {
        // 处理设置纯音频模式逻辑
        _state.update { it.copy(isAudioOnlyMode = intent.enabled) }
    }
    
    private fun handleSavePlaybackProgress(scope: CoroutineScope) {
        // 处理保存播放进度逻辑
        // 这里需要实现持久化存储
    }
    
    private fun handleLoadPlaybackProgress(scope: CoroutineScope) {
        // 处理加载播放进度逻辑
        // 这里需要从持久化存储中加载
    }
    
    private fun handlePreloadVideo(intent: PlayerIntent.PreloadVideo, scope: CoroutineScope) {
        // 处理预加载视频逻辑
    }
    
    private fun handleDownloadVideo(intent: PlayerIntent.DownloadVideo, scope: CoroutineScope) {
        // 处理视频下载逻辑
        // 这里需要使用WorkManager实现后台下载
    }
    
    private fun handlePauseDownload(intent: PlayerIntent.PauseDownload, scope: CoroutineScope) {
        // 处理暂停下载逻辑
    }
    
    private fun handleCancelDownload(intent: PlayerIntent.CancelDownload, scope: CoroutineScope) {
        // 处理取消下载逻辑
    }
    
    private fun handleGetDownloadStatus(scope: CoroutineScope) {
        // 处理获取下载状态逻辑
    }
    
    private fun handleGetDownloadedList(scope: CoroutineScope) {
        // 处理获取已下载列表逻辑
    }
    
    private fun handleDeleteDownloadedVideo(intent: PlayerIntent.DeleteDownloadedVideo, scope: CoroutineScope) {
        // 处理删除已下载视频逻辑
    }
    
    private fun handleClearCache(scope: CoroutineScope) {
        // 处理清除缓存逻辑
        _state.update { it.copy(cacheSize = 0L) }
    }
    
    private fun handleGetCacheSize(scope: CoroutineScope) {
        // 处理获取缓存大小逻辑
        // 这里需要实现获取实际缓存大小的逻辑
    }
    
    private fun handleSetMaxCacheSize(intent: PlayerIntent.SetMaxCacheSize) {
        // 处理设置最大缓存容量逻辑
        _state.update { it.copy(maxCacheSize = intent.size) }
    }
    
    private fun handleTakeScreenshot(scope: CoroutineScope) {
        // 处理截图逻辑
        // 这里需要实现视频截图功能
    }
    
    private fun handleOnResume() {
        // 处理恢复逻辑
        player?.play()
        _state.update { it.copy(isPlaying = true) }
    }
    
    private fun handleOnPause() {
        // 处理暂停逻辑
        player?.pause()
        _state.update { it.copy(isPlaying = false) }
    }
    
    private fun handleOnDestroy() {
        // 处理销毁逻辑
        player?.release()
    }
    
    private fun handleNetworkStateChanged(intent: PlayerIntent.NetworkStateChanged) {
        // 处理网络状态变化逻辑
        _state.update { it.copy(networkType = intent.networkType) }
    }
    
    private fun handleSetCutoutAdapted(intent: PlayerIntent.SetCutoutAdapted) {
        // 处理设置刘海屏适配逻辑
        _state.update { it.copy(isCutoutAdapted = intent.adapted) }
    }
    
    /**
     * 更新播放器实时状态
     */
    fun updatePlayerState(position: Long, bufferedPosition: Long, duration: Long, isPlaying: Boolean) {
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
