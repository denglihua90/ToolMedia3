package com.dlh.toolmedia3.video

import androidx.media3.exoplayer.DefaultLoadControl

/**
 * 播放器配置常量类
 */
@androidx.media3.common.util.UnstableApi
object PlayerConfig {
    // 网络配置
    const val CONNECT_TIMEOUT_MS = 8000
    const val READ_TIMEOUT_MS = 12000
    const val USER_AGENT = "ToolMedia3/1.0"
    
    // 播放模式缓冲策略
    const val PLAY_MIN_BUFFER_MS = 3000 // 增加到3秒，提高稳定性
    const val PLAY_MAX_BUFFER_MS = 25000 // 增加到25秒，增加缓冲量
    const val PLAY_BUFFER_FOR_PLAYBACK_MS = 500 // 增加到500ms，确保有足够的初始缓冲
    const val PLAY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 1200 // 增加到1.2秒，避免频繁缓冲
    
    // 预加载模式缓冲策略
    const val PRELOAD_MIN_BUFFER_MS = 5000
    const val PRELOAD_MAX_BUFFER_MS = 150 * 1024 * 1024 // 150MB
    const val PRELOAD_BUFFER_FOR_PLAYBACK_MS = 1000
    const val PRELOAD_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2000
    
    // 预加载参数
    const val PRELOAD_DURATION = 8000 // 预加载8秒
    
    /**
     * 创建播放模式的 LoadControl
     */
    fun createPlayLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                PLAY_MIN_BUFFER_MS,
                PLAY_MAX_BUFFER_MS,
                PLAY_BUFFER_FOR_PLAYBACK_MS,
                PLAY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
    }
    
    /**
     * 创建预加载模式的 LoadControl
     */
    fun createPreloadLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                PRELOAD_MIN_BUFFER_MS,
                PRELOAD_MAX_BUFFER_MS,
                PRELOAD_BUFFER_FOR_PLAYBACK_MS,
                PRELOAD_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
    }
}