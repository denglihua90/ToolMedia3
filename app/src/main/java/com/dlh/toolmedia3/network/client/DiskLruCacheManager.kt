package com.dlh.toolmedia3.network.client

import android.content.Context
import com.jakewharton.disklrucache.DiskLruCache
import java.io.File
import java.io.IOException

class DiskLruCacheManager private constructor() {

    companion object {
        private const val CACHE_SIZE = 50 * 1024 * 1024L // 50MB
        private const val CACHE_VERSION = 1
        private const val CACHE_VALUE_COUNT = 1
        private var instance: DiskLruCacheManager? = null
        private var diskLruCache: DiskLruCache? = null

        fun getInstance(): DiskLruCacheManager {
            if (instance == null) {
                synchronized(DiskLruCacheManager::class.java) {
                    if (instance == null) {
                        instance = DiskLruCacheManager()
                    }
                }
            }
            return instance!!
        }

        fun initialize(context: Context) {
            try {
                val cacheDir = File(context.cacheDir, "disk_lru_cache")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                diskLruCache = DiskLruCache.open(cacheDir, CACHE_VERSION, CACHE_VALUE_COUNT, CACHE_SIZE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getDiskLruCache(): DiskLruCache? {
            return diskLruCache
        }

        fun put(key: String, value: String) {
            diskLruCache?.let {cache ->
                try {
                    val editor = cache.edit(key)
                    editor?.let {
                        it.set(0, value)
                        it.commit()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun get(key: String): String? {
            diskLruCache?.let {cache ->
                try {
                    val snapshot = cache.get(key)
                    snapshot?.let {
                        val value = it.getString(0)
                        snapshot.close()
                        return value
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        fun remove(key: String) {
            diskLruCache?.let {cache ->
                try {
                    cache.remove(key)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun clear() {
            diskLruCache?.let {cache ->
                try {
                    cache.delete()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun close() {
            diskLruCache?.let {cache ->
                try {
                    cache.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
