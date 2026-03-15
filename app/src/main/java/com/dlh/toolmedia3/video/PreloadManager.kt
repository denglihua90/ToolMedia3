package com.dlh.toolmedia3.video

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * 视频预加载管理器
 */
@androidx.media3.common.util.UnstableApi
class PreloadManager(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: PreloadManager? = null
        
        fun getInstance(context: Context): PreloadManager {
            return instance ?: synchronized(this) {
                instance ?: PreloadManager(context.applicationContext).also {
                    instance = it
                }
            }
        }


    }
    
    // 预加载任务
    private val preloadTasks = ConcurrentHashMap<String, PreloadTask>()
    
    /**
     * 预加载视频
     */
    fun preloadVideo(url: String, title: String = "") {
        // 如果已经有预加载任务，直接返回
        if (preloadTasks.containsKey(url)) {
            return
        }
        
        // 创建预加载任务
        val task = PreloadTask(context, url, title)
        preloadTasks[url] = task
        
        // 开始预加载
        CoroutineScope(Dispatchers.IO).launch {
            task.preload()
        }
    }
    
    /**
     * 取消预加载
     */
    fun cancelPreload(url: String) {
        val task = preloadTasks.remove(url)
        task?.cancel()
    }
    
    /**
     * 取消所有预加载
     */
    fun cancelAllPreload() {
        preloadTasks.values.forEach {
            it.cancel()
        }
        preloadTasks.clear()
    }
    
    /**
     * 获取预加载状态
     */
    fun getPreloadStatus(url: String): PreloadStatus {
        return preloadTasks[url]?.status ?: PreloadStatus.IDLE
    }
    
    /**
     * 预加载任务
     */
    private class PreloadTask(
        private val context: Context,
        private val url: String,
        private val title: String
    ) {
        
        var status = PreloadStatus.IDLE
            private set
        
        private var player: ExoPlayer? = null
        private var isCancelled = false
        
        /**
         * 开始预加载
         */
        suspend fun preload() {
            try {
                status = PreloadStatus.PRELOADING
                
                // 从播放器池获取播放器实例
                player = PlayerPool.getInstance(context).acquireForPreload()

                val mediaItem = MediaItem.fromUri(url)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                
                // 预加载指定时间
                withContext(Dispatchers.Main) {
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < PlayerConfig.PRELOAD_DURATION && !isCancelled) {
                        Thread.sleep(100)
                    }
                    if (!isCancelled) {
                        status = PreloadStatus.COMPLETED
                    }
                    cancel()
                }
                
            } catch (e: Exception) {
                status = PreloadStatus.FAILED
                // 发生异常时释放播放器实例
                releasePlayer()
            }
        }
        
        /**
         * 取消预加载
         */
        fun cancel() {
            isCancelled = true
            releasePlayer()
            if (status != PreloadStatus.COMPLETED && status != PreloadStatus.FAILED) {
                status = PreloadStatus.CANCELLED
            }
        }
        
        /**
         * 释放播放器实例回池
         */
        private fun releasePlayer() {
            player?.let {
                PlayerPool.getInstance(context).releaseFromPreload(it)
                player = null
            }
        }
    }
    
    /**
     * 预加载状态
     */
    enum class PreloadStatus {
        IDLE,        // 未开始
        PRELOADING,  // 预加载中
        COMPLETED,   // 预加载完成
        FAILED,      // 预加载失败
        CANCELLED    // 预加载取消
    }
}
