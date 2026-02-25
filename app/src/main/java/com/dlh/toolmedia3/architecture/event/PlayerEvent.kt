package com.dlh.toolmedia3.architecture.event

import androidx.media3.common.Player
import androidx.media3.common.Tracks

/**
 * 播放器事件
 */
sealed class PlayerEvent {
    // 播放状态变化
    data class PlaybackStateChanged(val state: Int) : PlayerEvent()
    data class IsPlayingChanged(val isPlaying: Boolean) : PlayerEvent()
    data class PositionDiscontinuity(val reason: Int) : PlayerEvent()
    data class SeekProcessed(val position: Long) : PlayerEvent()
    
    // 轨道变化
    data class TracksChanged(val tracks: Tracks) : PlayerEvent()
    
    // 错误事件
    data class PlayerError(val error: Exception) : PlayerEvent()
    
    // 下载事件
    data class DownloadStarted(val downloadId: String) : PlayerEvent()
    data class DownloadProgress(val downloadId: String, val progress: Int) : PlayerEvent()
    data class DownloadCompleted(val downloadId: String, val filePath: String) : PlayerEvent()
    data class DownloadFailed(val downloadId: String, val error: String) : PlayerEvent()
    data class DownloadPaused(val downloadId: String) : PlayerEvent()
    data class DownloadCanceled(val downloadId: String) : PlayerEvent()
    
    // 缓存事件
    data class CacheSizeChanged(val size: Long) : PlayerEvent()
    data class CacheCleared(val success: Boolean) : PlayerEvent()
    
    // 其他事件
    data class ScreenshotTaken(val filePath: String) : PlayerEvent()
    data class PlaybackProgressSaved(val position: Long) : PlayerEvent()
    data class PlaybackProgressLoaded(val position: Long) : PlayerEvent()
    data class NetworkStateChanged(val networkType: Int) : PlayerEvent()
}
