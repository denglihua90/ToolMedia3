package com.dlh.toolmedia3.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import java.util.LinkedList
import java.util.concurrent.Semaphore

/**
 * 播放器池管理类
 * 用于管理ExoPlayer实例，减少播放器初始化时间
 * 集成了缓存系统和playWhenReady特性
 */
@androidx.media3.common.util.UnstableApi
class PlayerPool(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: PlayerPool? = null
        
        fun getInstance(context: Context): PlayerPool {
            return instance ?: synchronized(this) {
                instance ?: PlayerPool(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        // 最大池大小
        private const val MAX_POOL_SIZE = 3
    }
    
    // 播放专用播放器池
    private val pool = LinkedList<ExoPlayer>()
    // 预加载专用播放器池
    private val preloadPool = LinkedList<ExoPlayer>()
    // 信号量，控制并发访问
    private val semaphore = Semaphore(MAX_POOL_SIZE)
    // 媒体缓存管理器
    private val mediaCacheManager = MediaCacheManager.getInstance(context)
    // 主线程Handler，用于ExoPlayer操作
    private val mainHandler = Handler(Looper.getMainLooper())
    
    /**
     * 从池中获取播放器实例
     */
    fun acquire(): ExoPlayer {
        semaphore.acquireUninterruptibly()
        synchronized(pool) {
            return if (pool.isNotEmpty()) {
                pool.removeFirst()
            } else {
                createPlayer()
            }
        }
    }
    
    /**
     * 释放播放器实例回池
     */
    fun release(player: ExoPlayer) {
        mainHandler.post {
            synchronized(pool) {
                if (pool.size < MAX_POOL_SIZE) {
                    // 重置播放器状态
                    player.stop()
                    player.playWhenReady = false
                    player.clearMediaItems()
                    pool.add(player)
                } else {
                    player.release()
                }
            }
            semaphore.release()
        }
    }
    
    /**
     * 创建新的播放器实例
     */
    private fun createPlayer(): ExoPlayer {
        // 配置HttpDataSource
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(PlayerConfig.CONNECT_TIMEOUT_MS)
            .setReadTimeoutMs(PlayerConfig.READ_TIMEOUT_MS)
            .setUserAgent(PlayerConfig.USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
        
        // 配置缓存DataSource
        val defaultDataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val dataSourceFactory = mediaCacheManager.getCacheDataSourceFactory(defaultDataSourceFactory)
        
        // 配置LoadControl
        val loadControl = PlayerConfig.createPlayLoadControl()
        
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setLoadControl(loadControl)
            .build()
    }
    
    /**
     * 创建预加载专用的播放器实例
     */
    private fun createPreloadPlayer(): ExoPlayer {
        // 配置HttpDataSource
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(PlayerConfig.CONNECT_TIMEOUT_MS)
            .setReadTimeoutMs(PlayerConfig.READ_TIMEOUT_MS)
            .setUserAgent(PlayerConfig.USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
        
        // 配置缓存DataSource
        val defaultDataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val dataSourceFactory = mediaCacheManager.getCacheDataSourceFactory(defaultDataSourceFactory)
        
        // 配置预加载专用的LoadControl
        val loadControl = PlayerConfig.createPreloadLoadControl()
        
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setLoadControl(loadControl)
            .build()
    }
    
    /**
     * 获取预加载专用的播放器实例
     */
    fun acquireForPreload(): ExoPlayer {
        semaphore.acquireUninterruptibly()
        synchronized(preloadPool) {
            return if (preloadPool.isNotEmpty()) {
                preloadPool.removeFirst()
            } else {
                createPreloadPlayer()
            }
        }
    }
    
    /**
     * 释放预加载专用的播放器实例回池
     */
    fun releaseFromPreload(player: ExoPlayer) {
        mainHandler.post {
            synchronized(preloadPool) {
                if (preloadPool.size < MAX_POOL_SIZE) {
                    // 重置播放器状态
                    player.stop()
                    player.playWhenReady = false
                    player.clearMediaItems()
                    preloadPool.add(player)
                } else {
                    player.release()
                }
            }
            semaphore.release()
        }
    }
    
    /**
     * 清空播放器池
     */
    fun clear() {
        mainHandler.post {
            synchronized(pool) {
                pool.forEach { it.release() }
                pool.clear()
            }
            synchronized(preloadPool) {
                preloadPool.forEach { it.release() }
                preloadPool.clear()
            }
        }
    }
    
    /**
     * 获取当前池大小
     */
    fun size(): Int {
        synchronized(pool) {
            synchronized(preloadPool) {
                return pool.size + preloadPool.size
            }
        }
    }
    
    /**
     * 获取播放专用池大小
     */
    fun playPoolSize(): Int {
        synchronized(pool) {
            return pool.size
        }
    }
    
    /**
     * 获取预加载专用池大小
     */
    fun preloadPoolSize(): Int {
        synchronized(preloadPool) {
            return preloadPool.size
        }
    }
    
    /**
     * 设置缓存开关
     */
    fun setCacheEnabled(enabled: Boolean) {
        mediaCacheManager.setCacheEnabled(enabled)
    }
    
    /**
     * 清空缓存
     */
    fun clearCache() {
        mediaCacheManager.clearCache()
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Long {
        return mediaCacheManager.getCacheSize()
    }
}