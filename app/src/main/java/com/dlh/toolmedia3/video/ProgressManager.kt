package com.dlh.toolmedia3.video

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 播放进度管理器
 */
class ProgressManager(private val context: Context) {
    
    companion object {
        private const val PREF_NAME = "playback_progress"
        private const val KEY_PREFIX = "progress_"
    }
    
    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 保存播放进度
     */
    suspend fun saveProgress(videoUrl: String, position: Long, duration: Long) {
        withContext(Dispatchers.IO) {
            preferences.edit()
                .putLong(getKey(videoUrl, "position"), position)
                .putLong(getKey(videoUrl, "duration"), duration)
                .putLong(getKey(videoUrl, "timestamp"), System.currentTimeMillis())
                .apply()
        }
    }
    
    /**
     * 加载播放进度
     */
    suspend fun loadProgress(videoUrl: String): Long? {
        return withContext(Dispatchers.IO) {
            val position = preferences.getLong(getKey(videoUrl, "position"), -1)
            if (position > 0) {
                position
            } else {
                null
            }
        }
    }
    
    /**
     * 清除播放进度
     */
    suspend fun clearProgress(videoUrl: String) {
        withContext(Dispatchers.IO) {
            preferences.edit()
                .remove(getKey(videoUrl, "position"))
                .remove(getKey(videoUrl, "duration"))
                .remove(getKey(videoUrl, "timestamp"))
                .apply()
        }
    }
    
    /**
     * 获取所有保存的进度
     */
    suspend fun getAllProgress(): Map<String, Long> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, Long>()
            val allKeys = preferences.all.keys
            
            allKeys.forEach {
                if (it.startsWith(KEY_PREFIX) && it.endsWith("_position")) {
                    val videoUrl = it.substring(KEY_PREFIX.length, it.length - "_position".length)
                    val position = preferences.getLong(it, 0)
                    result[videoUrl] = position
                }
            }
            
            result
        }
    }
    
    /**
     * 清理过期的进度数据
     */
    suspend fun cleanExpiredProgress(days: Int = 30) {
        withContext(Dispatchers.IO) {
            val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
            val allKeys = preferences.all.keys
            val toRemove = mutableListOf<String>()
            
            allKeys.forEach {
                if (it.startsWith(KEY_PREFIX) && it.endsWith("_timestamp")) {
                    val timestamp = preferences.getLong(it, 0)
                    if (timestamp < cutoffTime) {
                        val videoUrl = it.substring(KEY_PREFIX.length, it.length - "_timestamp".length)
                        toRemove.add(getKey(videoUrl, "position"))
                        toRemove.add(getKey(videoUrl, "duration"))
                        toRemove.add(getKey(videoUrl, "timestamp"))
                    }
                }
            }
            
            val editor = preferences.edit()
            toRemove.forEach { editor.remove(it) }
            editor.apply()
        }
    }
    
    /**
     * 生成存储键
     */
    private fun getKey(videoUrl: String, suffix: String): String {
        return "${KEY_PREFIX}${videoUrl.hashCode()}_${suffix}"
    }
}
