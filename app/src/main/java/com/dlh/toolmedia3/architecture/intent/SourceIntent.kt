package com.dlh.toolmedia3.architecture.intent

import com.dlh.toolmedia3.data.model.PlaybackSource

/**
 * 播放源相关的 Intent
 */
sealed class SourceIntent {
    /**
     * 保存播放源
     */
    data class SaveSource(val name: String, val url: String) : SourceIntent()
    
    /**
     * 加载所有播放源
     */
    object LoadSources : SourceIntent()
    
    /**
     * 删除播放源
     */
    data class DeleteSource(val id: Int) : SourceIntent()
    
    /**
     * 清除错误状态
     */
    object ClearError : SourceIntent()
    
    /**
     * 测试源地址
     */
    data class TestSource(val url: String) : SourceIntent()
}
