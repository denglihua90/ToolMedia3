package com.dlh.toolmedia3.video

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

/**
 * 智能预加载管理器
 * 根据用户行为预测可能播放的视频
 */
@androidx.media3.common.util.UnstableApi
class SmartPreloadManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: SmartPreloadManager? = null
        
        fun getInstance(context: Context): SmartPreloadManager {
            return instance ?: synchronized(this) {
                instance ?: SmartPreloadManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    // 预加载优先级队列
    private val priorityQueue = PriorityBlockingQueue<PreloadItem>()
    // 正在预加载的任务
    private val preloadingTasks = ConcurrentHashMap<String, PreloadItem>()
    // 最大同时预加载任务数
    private val MAX_SIMULTANEOUS_PRELOADS = 2
    // 预加载管理器
    private val preloadManager = PreloadManager.getInstance(context)
    
    /**
     * 基于浏览行为的预加载
     */
    fun preloadBasedOnBrowsing(url: String, title: String, position: Int) {
        // 根据位置计算优先级，位置越靠前优先级越高
        val priority = 100 - position // 假设最多100个视频
        val item = PreloadItem(url, title, priority, PreloadType.BROWSING)
        addToPreloadQueue(item)
    }
    
    /**
     * 基于历史记录的预加载
     */
    fun preloadBasedOnHistory(url: String, title: String, playCount: Int) {
        // 根据播放次数计算优先级，播放次数越多优先级越高
        val priority = playCount * 10
        val item = PreloadItem(url, title, priority, PreloadType.HISTORY)
        addToPreloadQueue(item)
    }
    
    /**
     * 基于相关性的预加载
     */
    fun preloadBasedOnRelated(currentUrl: String, relatedUrls: List<Pair<String, String>>) {
        relatedUrls.forEachIndexed { index, (url, title) ->
            // 根据相关性顺序计算优先级
            val priority = 80 - index * 10 // 相关性越高优先级越高
            val item = PreloadItem(url, title, priority, PreloadType.RELATED)
            addToPreloadQueue(item)
        }
    }
    
    /**
     * 添加到预加载队列
     */
    private fun addToPreloadQueue(item: PreloadItem) {
        // 如果已经在预加载中，不重复添加
        if (preloadingTasks.containsKey(item.url)) {
            return
        }
        
        // 添加到优先级队列
        priorityQueue.offer(item)
        
        // 启动预加载
        startPreloadTasks()
    }
    
    /**
     * 启动预加载任务
     */
    private fun startPreloadTasks() {
        // 检查是否有空闲的预加载槽位
        while (preloadingTasks.size < MAX_SIMULTANEOUS_PRELOADS && priorityQueue.isNotEmpty()) {
            val item = priorityQueue.poll() ?: break
            
            // 标记为正在预加载
            preloadingTasks[item.url] = item
            
            // 开始预加载
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    preloadManager.preloadVideo(item.url, item.title)
                    // 预加载完成后移除任务
                    preloadingTasks.remove(item.url)
                    // 继续启动下一个预加载任务
                    startPreloadTasks()
                } catch (e: Exception) {
                    // 预加载失败，移除任务
                    preloadingTasks.remove(item.url)
                    // 继续启动下一个预加载任务
                    startPreloadTasks()
                }
            }
        }
    }
    
    /**
     * 取消预加载
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun cancelPreload(url: String) {
        // 从队列中移除
        priorityQueue.removeIf { it.url == url }
        // 取消正在预加载的任务
        val item = preloadingTasks.remove(url)
        if (item != null) {
            preloadManager.cancelPreload(url)
        }
    }
    
    /**
     * 取消所有预加载
     */
    fun cancelAllPreload() {
        // 清空队列
        priorityQueue.clear()
        // 取消所有正在预加载的任务
        preloadingTasks.keys.forEach { url ->
            preloadManager.cancelPreload(url)
        }
        preloadingTasks.clear()
    }
    
    /**
     * 获取预加载状态
     */
    fun getPreloadStatus(url: String): PreloadManager.PreloadStatus {
        return preloadManager.getPreloadStatus(url)
    }
    
    /**
     * 预加载项
     */
    private data class PreloadItem(
        val url: String,
        val title: String,
        val priority: Int,
        val type: PreloadType
    ) : Comparable<PreloadItem> {
        override fun compareTo(other: PreloadItem): Int {
            // 优先级高的排在前面
            return other.priority - this.priority
        }
    }
    
    /**
     * 预加载类型
     */
    private enum class PreloadType {
        BROWSING,    // 基于浏览行为
        HISTORY,     // 基于历史记录
        RELATED      // 基于相关性
    }
}