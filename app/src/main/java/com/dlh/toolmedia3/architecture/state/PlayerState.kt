package com.dlh.toolmedia3.architecture.state

import androidx.media3.common.Player

/**
 * 播放器状态
 */
data class PlayerState(
    // 播放状态
    val playState: Int = Player.STATE_IDLE,
    // 播放位置
    val currentPosition: Long = 0L,
    // 总时长
    val duration: Long = 0L,
    // 缓冲位置
    val bufferedPosition: Long = 0L,
    // 是否正在播放
    val isPlaying: Boolean = false,
    // 音量
    val volume: Float = 1.0f,
    // 亮度
    val brightness: Float = 0.5f,
    // 播放速度
    val playbackSpeed: Float = 1.0f,
    // 当前清晰度
    val currentQuality: String = "",
    // 可用清晰度列表
    val availableQualities: List<String> = emptyList(),
    // 是否全屏
    val isFullScreen: Boolean = false,
    // 是否画中画模式
    val isPictureInPicture: Boolean = false,
    // 屏幕模式
    val screenMode: Int = SCREEN_MODE_NORMAL,
    // 缩放类型
    val scaleType: Int = SCALE_TYPE_FIT_CENTER,
    // 视频旋转角度
    val videoRotation: Int = 0,
    // 是否镜像视频
    val isVideoMirrored: Boolean = false,
    // 是否纯音频模式
    val isAudioOnlyMode: Boolean = false,
    // 缓存大小
    val cacheSize: Long = 0L,
    // 最大缓存容量
    val maxCacheSize: Long = 512 * 1024 * 1024L, // 默认512MB
    // 网络状态
    val networkType: Int = NETWORK_UNKNOWN,
    // 是否开启刘海屏适配
    val isCutoutAdapted: Boolean = true, // 默认开启刘海屏适配
    // 错误信息
    val errorMessage: String? = null
) {
    companion object {
        // 屏幕模式
        const val SCREEN_MODE_NORMAL = 0
        const val SCREEN_MODE_FULLSCREEN = 1
        const val SCREEN_MODE_PICTURE_IN_PICTURE = 2
        
        // 缩放类型
        const val SCALE_TYPE_FIT_CENTER = 0
        const val SCALE_TYPE_FILL = 1
        const val SCALE_TYPE_ZOOM = 2
        
        // 网络状态
        const val NETWORK_UNKNOWN = -1
        const val NETWORK_NONE = 0
        const val NETWORK_WIFI = 1
        const val NETWORK_MOBILE = 2
    }
}
