package com.dlh.toolmedia3.download.manager

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.dlh.toolmedia3.download.model.DownloadStatus
import com.dlh.toolmedia3.download.model.DownloadTask
import com.dlh.toolmedia3.download.model.DownloadedVideo
import com.dlh.toolmedia3.download.notification.DownloadNotificationManager
import com.dlh.toolmedia3.download.worker.DownloadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 下载管理器
 */
class DownloadManager(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: DownloadManager? = null
        
        fun getInstance(context: Context): DownloadManager {
            return instance ?: synchronized(this) {
                instance ?: DownloadManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    private val workManager = WorkManager.getInstance(context)
    private val notificationManager = DownloadNotificationManager(context)
    private val downloadTasks = ConcurrentHashMap<String, DownloadTask>()
    private val _downloadTasksFlow = MutableStateFlow<List<DownloadTask>>(emptyList())
    val downloadTasksFlow: StateFlow<List<DownloadTask>> = _downloadTasksFlow
    
    // 下载目录
    private val downloadDir by lazy {
        File(context.getExternalFilesDir(null), "downloads").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * 开始下载
     */
    fun startDownload(videoUrl: String, title: String, quality: String): String {
        val taskId = UUID.randomUUID().toString()
        val savePath = "${downloadDir.path}/${title}_${quality}_${System.currentTimeMillis()}.mp4"
        
        val task = DownloadTask(
            id = taskId,
            videoUrl = videoUrl,
            title = title,
            quality = quality,
            savePath = savePath,
            status = DownloadStatus.PENDING
        )
        
        downloadTasks[taskId] = task
        updateTasksFlow()
        
        // 创建下载工作
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val inputData = Data.Builder()
            .putString(DownloadWorker.KEY_VIDEO_URL, videoUrl)
            .putString(DownloadWorker.KEY_TITLE, title)
            .putString(DownloadWorker.KEY_QUALITY, quality)
            .putString(DownloadWorker.KEY_SAVE_PATH, savePath)
            .putString(DownloadWorker.KEY_TASK_ID, taskId)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        
        // 保存workId与taskId的映射
        workManager.enqueue(workRequest)
        
        // 监听工作状态
        CoroutineScope(Dispatchers.Main).launch {
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .observeForever {workInfo ->
                    workInfo?.let {
                        when {
                            it.state.isFinished -> {
                                // 工作完成
                                val outputData = it.outputData
                                val status = outputData.getString(DownloadWorker.KEY_STATUS)
                                when (status) {
                                    DownloadStatus.COMPLETED.name -> {
                                        task.status = DownloadStatus.COMPLETED
                                        task.progress = 100
                                        task.totalSize = outputData.getLong(DownloadWorker.KEY_TOTAL_SIZE, 0)
                                        task.downloadedSize = outputData.getLong(DownloadWorker.KEY_DOWNLOADED_SIZE, 0)
                                    }
                                    DownloadStatus.FAILED.name -> {
                                        task.status = DownloadStatus.FAILED
                                        task.errorMessage = outputData.getString(DownloadWorker.KEY_ERROR_MESSAGE)
                                    }
                                }
                            }
                            it.progress.keyValueMap.isNotEmpty() -> {
                                // 工作进行中，更新进度
                                val progress = it.progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
                                val totalSize = it.progress.getLong(DownloadWorker.KEY_TOTAL_SIZE, 0)
                                val downloadedSize = it.progress.getLong(DownloadWorker.KEY_DOWNLOADED_SIZE, 0)
                                
                                task.status = DownloadStatus.DOWNLOADING
                                task.progress = progress
                                task.totalSize = totalSize
                                task.downloadedSize = downloadedSize
                            }
                        }
                        
                        task.updateTime = System.currentTimeMillis()
                        downloadTasks[taskId] = task
                        updateTasksFlow()
                        
                        // 显示通知
                        notificationManager.showDownloadNotification(
                            taskId.hashCode(),
                            task.title,
                            task.status,
                            task.progress,
                            task.totalSize,
                            task.downloadedSize
                        )
                    }
                }
        }
        
        return taskId
    }
    
    /**
     * 暂停下载
     */
    fun pauseDownload(taskId: String) {
        val task = downloadTasks[taskId]
        if (task != null) {
            task.status = DownloadStatus.PAUSED
            task.updateTime = System.currentTimeMillis()
            downloadTasks[taskId] = task
            updateTasksFlow()
            
            // 这里需要取消当前的Work，后续可以重新创建Work继续下载
            // 实际实现中需要保存断点信息
            
            notificationManager.showDownloadNotification(
                taskId.hashCode(),
                task.title,
                task.status,
                task.progress,
                task.totalSize,
                task.downloadedSize
            )
        }
    }
    
    /**
     * 取消下载
     */
    fun cancelDownload(taskId: String) {
        val task = downloadTasks[taskId]
        if (task != null) {
            task.status = DownloadStatus.CANCELLED
            task.updateTime = System.currentTimeMillis()
            downloadTasks[taskId] = task
            updateTasksFlow()
            
            // 删除文件
            val file = File(task.savePath)
            if (file.exists()) {
                file.delete()
            }
            
            notificationManager.showDownloadNotification(
                taskId.hashCode(),
                task.title,
                task.status,
                task.progress,
                task.totalSize,
                task.downloadedSize
            )
            
            // 取消通知
            notificationManager.cancelNotification(taskId.hashCode())
        }
    }
    
    /**
     * 获取下载任务
     */
    fun getDownloadTask(taskId: String): DownloadTask? {
        return downloadTasks[taskId]
    }
    
    /**
     * 获取所有下载任务
     */
    fun getAllDownloadTasks(): List<DownloadTask> {
        return downloadTasks.values.toList()
    }
    
    /**
     * 获取已下载视频列表
     */
    suspend fun getDownloadedVideos(): List<DownloadedVideo> {
        return withContext(Dispatchers.IO) {
            val videos = mutableListOf<DownloadedVideo>()
            
            // 遍历下载目录
            if (downloadDir.exists() && downloadDir.isDirectory) {
                downloadDir.listFiles()?.forEach {file ->
                    if (file.isFile && file.extension == "mp4") {
                        val video = DownloadedVideo(
                            id = UUID.randomUUID().toString(),
                            title = file.nameWithoutExtension,
                            path = file.path,
                            size = file.length(),
                            downloadTime = file.lastModified(),
                            quality = ""
                        )
                        videos.add(video)
                    }
                }
            }
            
            videos
        }
    }
    
    /**
     * 检查视频是否已下载
     */
    fun isVideoDownloaded(videoUrl: String): Boolean {
        // 这里需要根据实际情况实现，比如通过URL或者视频ID判断
        return false
    }
    
    /**
     * 删除已下载视频
     */
    suspend fun deleteDownloadedVideo(videoPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(videoPath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        }
    }
    
    /**
     * 获取已下载视频路径
     */
    fun getDownloadedVideoPath(videoUrl: String): String? {
        // 这里需要根据实际情况实现
        return null
    }
    
    /**
     * 清除所有下载任务
     */
    fun clearAllTasks() {
        downloadTasks.clear()
        updateTasksFlow()
    }
    
    /**
     * 更新任务流
     */
    private fun updateTasksFlow() {
        _downloadTasksFlow.value = downloadTasks.values.toList().sortedByDescending { it.updateTime }
    }
}
