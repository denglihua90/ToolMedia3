package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "video_source")
data class VideoSource(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sourceUrl: String,
    val sourceName: String,
    val sourceItemJson:String
)
