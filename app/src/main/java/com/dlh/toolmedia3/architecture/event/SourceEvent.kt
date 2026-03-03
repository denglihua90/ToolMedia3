package com.dlh.toolmedia3.architecture.event

/**
 * 播放源相关的事件
 */
sealed class SourceEvent {
    /**
     * 保存成功事件
     */
    object SaveSuccess : SourceEvent()
    
    /**
     * 保存失败事件
     */
    data class SaveError(val message: String) : SourceEvent()
    
    /**
     * 加载成功事件
     */
    object LoadSuccess : SourceEvent()
    
    /**
     * 加载失败事件
     */
    data class LoadError(val message: String) : SourceEvent()
    
    /**
     * 删除成功事件
     */
    object DeleteSuccess : SourceEvent()
    
    /**
     * 删除失败事件
     */
    data class DeleteError(val message: String) : SourceEvent()
    
    /**
     * 测试成功事件
     */
    data class TestSuccess(val result: String) : SourceEvent()
    
    /**
     * 测试失败事件
     */
    data class TestError(val message: String) : SourceEvent()
}
