package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_progress")
data class PlaybackProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoUrl: String,
    val position: Long,
    val duration: Long,
    val updatedAt: Long
)