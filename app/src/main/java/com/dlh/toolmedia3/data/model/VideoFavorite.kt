package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_favorite")
data class VideoFavorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoUrl: String,
    val title: String,
    val thumbnailUrl: String? = null,
    val addedAt: Long
)