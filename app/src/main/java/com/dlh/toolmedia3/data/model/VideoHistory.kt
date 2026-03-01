package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_history")
data class VideoHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoUrl: String,
    val title: String,
    val lastPlayPosition: Long,
    val lastPlayTime: Long,
    val duration: Long,
    val thumbnailUrl: String? = null
)