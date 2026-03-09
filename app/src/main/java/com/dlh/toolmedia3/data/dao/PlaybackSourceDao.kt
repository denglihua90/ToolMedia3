package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dlh.toolmedia3.data.model.PlaybackSource
import kotlinx.coroutines.flow.Flow

/**
 * 播放源DAO接口
 */
@Dao
interface PlaybackSourceDao {
    /**
     * 插入播放源
     */
    @Insert
    suspend fun insertSource(source: PlaybackSource)

    /**
     * 插入播放源并返回ID
     */
    @Insert
    suspend fun insertSourceAndReturnId(source: PlaybackSource): Long

    /**
     * 批量插入播放源
     */
    @Insert
    suspend fun insertSources(sources: List<PlaybackSource>)

    /**
     * 更新播放源
     */
    @Update
    suspend fun updateSource(source: PlaybackSource)

    /**
     * 批量更新播放源
     */
    @Update
    suspend fun updateSources(sources: List<PlaybackSource>)

    /**
     * 删除播放源
     */
    @Delete
    suspend fun deleteSource(source: PlaybackSource)

    /**
     * 批量删除播放源
     */
    @Delete
    suspend fun deleteSources(sources: List<PlaybackSource>)

    /**
     * 根据ID删除播放源
     */
    @Query("DELETE FROM playback_sources WHERE id = :id")
    suspend fun deleteSourceById(id: Int)

    /**
     * 批量删除播放源（根据ID列表）
     */
    @Query("DELETE FROM playback_sources WHERE id IN (:ids)")
    suspend fun deleteSourcesByIds(ids: List<Int>)

    /**
     * 获取所有播放源
     */
    @Query("SELECT * FROM playback_sources ORDER BY created_at DESC")
    fun getAllSources(): Flow<List<PlaybackSource>>

    /**
     * 分页获取播放源
     */
    @Query("SELECT * FROM playback_sources ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getSourcesPaged(limit: Int, offset: Int): List<PlaybackSource>

    /**
     * 根据ID获取播放源
     */
    @Query("SELECT * FROM playback_sources WHERE id = :id")
    suspend fun getSourceById(id: Int): PlaybackSource?

    /**
     * 根据名称获取播放源
     */
    @Query("SELECT * FROM playback_sources WHERE name = :name")
    suspend fun getSourceByName(name: String): PlaybackSource?

    /**
     * 检查播放源是否存在
     */
    @Query("SELECT COUNT(*) FROM playback_sources WHERE name = :name")
    suspend fun existsByName(name: String): Int

    /**
     * 清空所有播放源
     */
    @Query("DELETE FROM playback_sources")
    suspend fun clearAllSources()

    /**
     * 获取播放源数量
     */
    @Query("SELECT COUNT(*) FROM playback_sources")
    suspend fun getSourceCount(): Int

    /**
     * 批量操作事务示例
     */
    @Transaction
    suspend fun upsertSources(sources: List<PlaybackSource>) {
        for (source in sources) {
            val existing = getSourceByName(source.name)
            if (existing != null) {
                updateSource(source)
            } else {
                insertSource(source)
            }
        }
    }
}
