package com.dlh.toolmedia3.architecture.state

import com.dlh.toolmedia3.data.model.PlaybackSource

/**
 * 播放源相关的状态
 */
data class SourceState(
    val sources: List<PlaybackSource> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: String? = null
)
