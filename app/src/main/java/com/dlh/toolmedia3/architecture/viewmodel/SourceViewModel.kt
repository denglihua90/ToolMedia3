package com.dlh.toolmedia3.architecture.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlh.toolmedia3.architecture.event.SourceEvent
import com.dlh.toolmedia3.architecture.intent.SourceIntent
import com.dlh.toolmedia3.architecture.processor.SourceProcessor
import com.dlh.toolmedia3.architecture.state.SourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 播放源 ViewModel
 */
class SourceViewModel(private val context: Context) : ViewModel() {
    
    private val processor = SourceProcessor(context)
    
    // 状态流
    val state: StateFlow<SourceState> = processor.state
    
    // 事件流
    val events: Flow<SourceEvent> = processor.events
    
    /**
     * 发送 Intent
     */
    fun sendIntent(intent: SourceIntent) {
        processor.processIntent(intent, viewModelScope)
    }
    
    /**
     * 保存播放源
     */
    fun saveSource(name: String, url: String) {
        sendIntent(SourceIntent.SaveSource(name, url))
    }
    
    /**
     * 加载所有播放源
     */
    fun loadSources() {
        sendIntent(SourceIntent.LoadSources)
    }
    
    /**
     * 删除播放源
     */
    fun deleteSource(id: Int) {
        sendIntent(SourceIntent.DeleteSource(id))
    }
    
    /**
     * 清除错误状态
     */
    fun clearError() {
        sendIntent(SourceIntent.ClearError)
    }
    
    /**
     * 测试源地址
     */
    fun testSource(url: String) {
        sendIntent(SourceIntent.TestSource(url))
    }
}
