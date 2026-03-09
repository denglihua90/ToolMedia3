package com.dlh.toolmedia3.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 播放源数据模型
 */
@Entity(
    tableName = "playback_sources",
    indices = [
        Index(value = ["name"], unique = false),
        Index(value = ["url"], unique = false)
    ]
)
data class PlaybackSource(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
