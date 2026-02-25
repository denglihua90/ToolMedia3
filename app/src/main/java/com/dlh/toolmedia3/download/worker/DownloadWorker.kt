package com.dlh.toolmedia3.download.worker

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dlh.toolmedia3.download.model.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 下载Worker
 */
class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val KEY_VIDEO_URL = "video_url"
        const val KEY_TITLE = "title"
        const val KEY_QUALITY = "quality"
        const val KEY_SAVE_PATH = "save_path"
        const val KEY_TASK_ID = "task_id"
        const val KEY_STATUS = "status"
        const val KEY_PROGRESS = "progress"
        const val KEY_TOTAL_SIZE = "total_size"
        const val KEY_DOWNLOADED_SIZE = "downloaded_size"
        const val KEY_ERROR_MESSAGE = "error_message"
    }
    
    override suspend fun doWork(): Result {
        val videoUrl = inputData.getString(KEY_VIDEO_URL) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: ""
        val quality = inputData.getString(KEY_QUALITY) ?: ""
        val savePath = inputData.getString(KEY_SAVE_PATH) ?: return Result.failure()
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()
        
        try {
            // 下载文件
            val result = downloadFile(videoUrl, savePath)
            
            when (result) {
                is DownloadResult.Success -> {
                    val outputData = workDataOf(
                        KEY_STATUS to DownloadStatus.COMPLETED.name,
                        KEY_PROGRESS to 100,
                        KEY_TOTAL_SIZE to result.totalSize,
                        KEY_DOWNLOADED_SIZE to result.totalSize
                    )
                    return Result.success(outputData)
                }
                is DownloadResult.Failure -> {
                    val outputData = workDataOf(
                        KEY_STATUS to DownloadStatus.FAILED.name,
                        KEY_ERROR_MESSAGE to result.errorMessage
                    )
                    return Result.failure(outputData)
                }
            }
        } catch (e: Exception) {
            val outputData = workDataOf(
                KEY_STATUS to DownloadStatus.FAILED.name,
                KEY_ERROR_MESSAGE to e.message
            )
            return Result.failure(outputData)
        }
    }
    
    /**
     * 下载文件
     */
    private suspend fun downloadFile(url: String, savePath: String): DownloadResult {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            
            try {
                val fileUrl = URL(url)
                connection = fileUrl.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext DownloadResult.Failure("服务器返回错误: $responseCode")
                }
                
                val totalSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connection.contentLengthLong
                } else {
                    connection.contentLength.toLong()
                }
                inputStream = connection.inputStream
                
                // 创建保存目录
                val file = File(savePath)
                val parentDir = file.parentFile
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs()
                }
                
                outputStream = file.outputStream()
                val buffer = ByteArray(4096)
                var downloadedSize: Long = 0
                var bytesRead: Int
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    if (isStopped) {
                        return@withContext DownloadResult.Failure("下载已取消")
                    }
                    
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    
                    // 计算进度
                    val progress = if (totalSize > 0) {
                        ((downloadedSize * 100) / totalSize).toInt()
                    } else {
                        0
                    }
                    
                    // 更新进度
                    setProgress(workDataOf(
                        KEY_PROGRESS to progress,
                        KEY_TOTAL_SIZE to totalSize,
                        KEY_DOWNLOADED_SIZE to downloadedSize
                    ))
                }
                
                outputStream.flush()
                return@withContext DownloadResult.Success(totalSize)
                
            } catch (e: Exception) {
                return@withContext DownloadResult.Failure(e.message ?: "下载失败")
            } finally {
                connection?.disconnect()
                inputStream?.close()
                outputStream?.close()
            }
        }
    }
    
    /**
     * 下载结果
     */
    sealed class DownloadResult {
        data class Success(val totalSize: Long) : DownloadResult()
        data class Failure(val errorMessage: String) : DownloadResult()
    }
}
