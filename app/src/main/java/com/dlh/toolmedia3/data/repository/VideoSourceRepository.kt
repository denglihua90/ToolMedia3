package com.dlh.toolmedia3.data.repository

import com.dlh.toolmedia3.data.DatabaseManager
import com.dlh.toolmedia3.data.model.VideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoSourceRepository(private val databaseManager: DatabaseManager) {
    private val videoSourceDao = databaseManager.database.videoSourceDao()

    /**
     * 插入单个VideoSource
     */
    suspend fun insert(videoSource: VideoSource) = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.insert(videoSource)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 插入多个VideoSource
     */
    suspend fun insertAll(videoSources: List<VideoSource>) = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.insertAll(videoSources)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 更新VideoSource
     */
    suspend fun update(videoSource: VideoSource) = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.update(videoSource)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除VideoSource
     */
    suspend fun delete(videoSource: VideoSource) = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.delete(videoSource)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 根据ID删除VideoSource
     */
    suspend fun deleteById(id: Int) = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.deleteById(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除所有VideoSource
     */
    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.deleteAll()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 根据ID查询VideoSource
     */
    suspend fun getById(id: Int): VideoSource? = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.getById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 查询所有VideoSource
     */
    suspend fun getAll(): List<VideoSource> = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.getAll()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 根据源名称查询VideoSource
     */
    suspend fun getBySourceName(sourceName: String): VideoSource? = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.getBySourceName(sourceName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据源URL查询VideoSource
     */
    suspend fun getBySourceUrl(sourceUrl: String): VideoSource? = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.getBySourceUrl(sourceUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 统计VideoSource数量
     */
    suspend fun count(): Int = withContext(Dispatchers.IO) {
        try {
            videoSourceDao.count()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
