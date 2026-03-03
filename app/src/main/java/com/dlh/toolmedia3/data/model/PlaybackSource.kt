package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 播放源数据模型
 */
@Entity(tableName = "playback_sources")
data class PlaybackSource(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
