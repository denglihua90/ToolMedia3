package com.dlh.toolmedia3.download.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.download.model.DownloadStatus

/**
 * 下载通知管理器
 */
class DownloadNotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val channelId = "download_channel"
    
    init {
        createNotificationChannel()
    }
    
    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.download_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = context.getString(R.string.download_channel_description)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    /**
     * 显示下载通知
     */
    fun showDownloadNotification(
        id: Int,
        title: String,
        status: DownloadStatus,
        progress: Int,
        totalSize: Long,
        downloadedSize: Long
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(status == DownloadStatus.DOWNLOADING)
        
        when (status) {
            DownloadStatus.PENDING -> {
                builder.setContentText(context.getString(R.string.download_waiting))
                    .setProgress(0, 0, true)
            }
            DownloadStatus.DOWNLOADING -> {
                builder.setContentText("下载中: ${formatSize(downloadedSize)} / ${formatSize(totalSize)}")
                    .setProgress(100, progress, false)
            }
            DownloadStatus.PAUSED -> {
                builder.setContentText(context.getString(R.string.download_paused))
                    .setProgress(100, progress, false)
                    .setOngoing(false)
            }
            DownloadStatus.COMPLETED -> {
                builder.setContentText(context.getString(R.string.download_completed))
                    .setProgress(100, 100, false)
                    .setOngoing(false)
            }
            DownloadStatus.FAILED -> {
                builder.setContentText(context.getString(R.string.download_failed))
                    .setProgress(0, 0, false)
                    .setOngoing(false)
            }
            DownloadStatus.CANCELLED -> {
                builder.setContentText(context.getString(R.string.download_canceled))
                    .setProgress(0, 0, false)
                    .setOngoing(false)
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager?.notify(id, builder.build())
            }
        } else {
            notificationManager?.notify(id, builder.build())
        }
    }
    
    /**
     * 取消通知
     */
    fun cancelNotification(id: Int) {
        notificationManager?.cancel(id)
    }
    
    /**
     * 格式化文件大小
     */
    private fun formatSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return String.format("%.2f %s", size, units[unitIndex])
    }
}
