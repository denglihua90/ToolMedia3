package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_info")
data class CacheInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val filePath: String,
    val size: Long,
    val createdAt: Long,
    val lastAccessed: Long
)