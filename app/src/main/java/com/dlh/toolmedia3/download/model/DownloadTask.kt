package com.dlh.toolmedia3.download.model

/**
 * 下载任务状态
 */
enum class DownloadStatus {
    PENDING,     // 等待中
    DOWNLOADING, // 下载中
    PAUSED,      // 暂停
    COMPLETED,   // 完成
    FAILED,      // 失败
    CANCELLED    // 取消
}

/**
 * 下载任务
 */
data class DownloadTask(
    // 任务ID
    val id: String,
    // 视频URL
    val videoUrl: String,
    // 视频标题
    val title: String,
    // 清晰度
    val quality: String,
    // 保存路径
    var savePath: String = "",
    // 状态
    var status: DownloadStatus = DownloadStatus.PENDING,
    // 进度
    var progress: Int = 0,
    // 总大小
    var totalSize: Long = 0,
    // 已下载大小
    var downloadedSize: Long = 0,
    // 错误信息
    var errorMessage: String? = null,
    // 创建时间
    val createTime: Long = System.currentTimeMillis(),
    // 更新时间
    var updateTime: Long = System.currentTimeMillis()
)

/**
 * 已下载视频
 */
data class DownloadedVideo(
    // 视频ID
    val id: String,
    // 视频标题
    val title: String,
    // 视频路径
    val path: String,
    // 视频大小
    val size: Long,
    // 下载时间
    val downloadTime: Long,
    // 清晰度
    val quality: String,
    // 视频时长
    val duration: Long = 0
)
