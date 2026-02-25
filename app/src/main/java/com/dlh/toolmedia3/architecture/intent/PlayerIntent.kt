package com.dlh.toolmedia3.architecture.intent

/**
 * 播放器意图
 */
sealed class PlayerIntent {
    // 播放控制
    data class Play(val url: String? = null) : PlayerIntent()
    object Pause : PlayerIntent()
    object Stop : PlayerIntent()
    data class SeekTo(val position: Long) : PlayerIntent()
    data class SetVolume(val volume: Float) : PlayerIntent()
    data class SetPlaybackSpeed(val speed: Float) : PlayerIntent()
    
    // 清晰度控制
    data class ChangeQuality(val quality: String) : PlayerIntent()
    object LoadQualities : PlayerIntent()
    
    // 屏幕控制
    object ToggleFullScreen : PlayerIntent()
    object EnterPictureInPicture : PlayerIntent()
    object ExitPictureInPicture : PlayerIntent()
    data class SetScreenMode(val mode: Int) : PlayerIntent()
    data class SetScaleType(val scaleType: Int) : PlayerIntent()
    
    // 视频效果
    data class SetVideoRotation(val rotation: Int) : PlayerIntent()
    data class SetVideoMirror(val mirrored: Boolean) : PlayerIntent()
    
    // 手势控制
    data class AdjustVolume(val delta: Float) : PlayerIntent()
    data class AdjustBrightness(val delta: Float) : PlayerIntent()
    data class AdjustProgress(val delta: Long) : PlayerIntent()
    
    // 音频模式
    data class SetAudioOnlyMode(val enabled: Boolean) : PlayerIntent()
    
    // 播放进度
    object SavePlaybackProgress : PlayerIntent()
    object LoadPlaybackProgress : PlayerIntent()
    
    // 预加载
    data class PreloadVideo(val url: String) : PlayerIntent()
    
    // 下载管理
    data class DownloadVideo(val url: String, val quality: String) : PlayerIntent()
    data class PauseDownload(val downloadId: String) : PlayerIntent()
    data class CancelDownload(val downloadId: String) : PlayerIntent()
    object GetDownloadStatus : PlayerIntent()
    object GetDownloadedList : PlayerIntent()
    data class DeleteDownloadedVideo(val videoId: String) : PlayerIntent()
    
    // 缓存管理
    object ClearCache : PlayerIntent()
    object GetCacheSize : PlayerIntent()
    data class SetMaxCacheSize(val size: Long) : PlayerIntent()
    
    // 其他功能
    object TakeScreenshot : PlayerIntent()
    
    // 生命周期事件
    object OnResume : PlayerIntent()
    object OnPause : PlayerIntent()
    object OnDestroy : PlayerIntent()
    
    // 网络状态变化
    data class NetworkStateChanged(val networkType: Int) : PlayerIntent()
    
    // 设置刘海屏适配
    data class SetCutoutAdapted(val adapted: Boolean) : PlayerIntent()
}
