package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_task")
data class DownloadTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoUrl: String,
    val title: String,
    val status: Int, // 0: 等待, 1: 下载中, 2: 已完成, 3: 失败
    val progress: Int,
    val filePath: String? = null,
    val size: Long,
    val createdAt: Long,
    val updatedAt: Long
)