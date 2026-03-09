package com.dlh.toolmedia3.data.repository

import com.dlh.toolmedia3.data.dao.CategoryItemDao
import com.dlh.toolmedia3.data.dao.PlaybackSourceDao
import com.dlh.toolmedia3.data.model.CategoryItem
import com.dlh.toolmedia3.data.model.PlaybackSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 播放源仓库类
 * 封装对PlaybackSourceDao的操作，提供更高级别的数据访问接口
 */
class PlaybackSourceRepository(private val sourceDao: PlaybackSourceDao, private val categoryItemDao: CategoryItemDao) {

    /**
     * 插入播放源
     */
    suspend fun insertSource(source: PlaybackSource): Result<Unit> {
        return try {
            sourceDao.insertSource(source)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 插入播放源并同时插入分类
     */
    suspend fun insertSourceWithCategories(source: PlaybackSource, categories: List<CategoryItem>): Result<Unit> {
        return try {
            // 插入PlaybackSource并获取ID
            val sourceId = sourceDao.insertSourceAndReturnId(source).toInt()
            
            // 为每个CategoryItem设置playbackSourceId
            val categoriesWithSourceId = categories.map {
                it.copy(playbackSourceId = sourceId)
            }
            
            // 批量插入CategoryItem
            if (categoriesWithSourceId.isNotEmpty()) {
                categoryItemDao.insertCategories(categoriesWithSourceId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量插入播放源
     */
    suspend fun insertSources(sources: List<PlaybackSource>): Result<Unit> {
        return try {
            sourceDao.insertSources(sources)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新播放源
     */
    suspend fun updateSource(source: PlaybackSource): Result<Unit> {
        return try {
            sourceDao.updateSource(source)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量更新播放源
     */
    suspend fun updateSources(sources: List<PlaybackSource>): Result<Unit> {
        return try {
            sourceDao.updateSources(sources)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除播放源
     */
    suspend fun deleteSource(source: PlaybackSource): Result<Unit> {
        return try {
            sourceDao.deleteSource(source)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量删除播放源
     */
    suspend fun deleteSources(sources: List<PlaybackSource>): Result<Unit> {
        return try {
            sourceDao.deleteSources(sources)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据ID删除播放源
     */
    suspend fun deleteSourceById(id: Int): Result<Unit> {
        return try {
            sourceDao.deleteSourceById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量删除播放源（根据ID列表）
     */
    suspend fun deleteSourcesByIds(ids: List<Int>): Result<Unit> {
        return try {
            sourceDao.deleteSourcesByIds(ids)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取所有播放源
     */
    fun getAllSources(): Flow<Result<List<PlaybackSource>>> {
        return sourceDao.getAllSources()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    /**
     * 分页获取播放源
     */
    suspend fun getSourcesPaged(limit: Int, offset: Int): Result<List<PlaybackSource>> {
        return try {
            val sources = sourceDao.getSourcesPaged(limit, offset)
            Result.success(sources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据ID获取播放源
     */
    suspend fun getSourceById(id: Int): Result<PlaybackSource?> {
        return try {
            val source = sourceDao.getSourceById(id)
            Result.success(source)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据名称获取播放源
     */
    suspend fun getSourceByName(name: String): Result<PlaybackSource?> {
        return try {
            val source = sourceDao.getSourceByName(name)
            Result.success(source)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 检查播放源是否存在
     */
    suspend fun existsByName(name: String): Result<Boolean> {
        return try {
            val exists = sourceDao.existsByName(name) > 0
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 保存或更新播放源
     * 如果已存在同名播放源，则更新；否则，插入新播放源
     */
    suspend fun saveOrUpdateSource(name: String, url: String): Result<PlaybackSource> {
        return saveOrUpdateSource(name, url, emptyList())
    }

    /**
     * 保存或更新播放源并同时保存分类
     * 如果已存在同名播放源，则更新；否则，插入新播放源
     */
    suspend fun saveOrUpdateSource(name: String, url: String, categories: List<CategoryItem>): Result<PlaybackSource> {
        return try {
            val existingSource = sourceDao.getSourceByName(name)
            val currentTime = System.currentTimeMillis()
            if (existingSource != null) {
                val updatedSource = existingSource.copy(
                    url = url,
                    updatedAt = currentTime
                )
                sourceDao.updateSource(updatedSource)
                
                // 删除旧的分类
                categoryItemDao.deleteCategoriesByPlaybackSourceId(existingSource.id)
                
                // 插入新的分类
                if (categories.isNotEmpty()) {
                    val categoriesWithSourceId = categories.map {
                        it.copy(playbackSourceId = existingSource.id)
                    }
                    categoryItemDao.insertCategories(categoriesWithSourceId)
                }
                
                Result.success(updatedSource)
            } else {
                val newSource = PlaybackSource(
                    name = name,
                    url = url,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                
                // 插入新的播放源和分类
                val result = insertSourceWithCategories(newSource, categories)
                if (result.isSuccess) {
                    Result.success(newSource)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("保存失败"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量保存或更新播放源
     */
    suspend fun saveOrUpdateSources(sources: List<PlaybackSource>): Result<Unit> {
        return try {
            sourceDao.upsertSources(sources)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 清空所有播放源
     */
    suspend fun clearAllSources(): Result<Unit> {
        return try {
            sourceDao.clearAllSources()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取播放源数量
     */
    suspend fun getSourceCount(): Result<Int> {
        return try {
            val count = sourceDao.getSourceCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
