package com.dlh.toolmedia3.video

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
class MediaCacheManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: MediaCacheManager? = null

        fun getInstance(context: Context): MediaCacheManager {
            return instance ?: synchronized(this) {
                instance ?: MediaCacheManager(context.applicationContext).also {
                    instance = it
                }
            }
        }

        private const val CACHE_DIR = "exoplayer_cache"
        private const val DEFAULT_CACHE_SIZE = 512 * 1024 * 1024L
    }

    private var cache: Cache? = null
    private var isCacheEnabled = true

    init {
        initCache()
    }

    private fun initCache() {
        try {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val evictor = LeastRecentlyUsedCacheEvictor(DEFAULT_CACHE_SIZE)
            cache = SimpleCache(cacheDir, evictor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCache(): Cache? {
        return if (isCacheEnabled) cache else null
    }

    fun setCacheEnabled(enabled: Boolean) {
        isCacheEnabled = enabled
    }

    fun isCacheEnabled(): Boolean {
        return isCacheEnabled
    }

    fun getCacheDataSourceFactory(
        upstreamFactory: androidx.media3.datasource.DataSource.Factory
    ): androidx.media3.datasource.DataSource.Factory {
        return if (isCacheEnabled && cache != null) {
            CacheDataSource.Factory()
                .setCache(cache!!)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        } else {
            upstreamFactory
        }
    }

    fun clearCache() {
        try {
            cache?.release()
            initCache()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCacheSize(): Long {
        return cache?.cacheSpace ?: 0L
    }

    fun release() {
        try {
            cache?.release()
            cache = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
