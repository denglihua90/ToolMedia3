package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.PlaybackSource
import kotlinx.coroutines.flow.Flow

/**
 * 播放源数据访问对象
 */
@Dao
interface SourceDao {
    /**
     * 插入播放源
     */
    @Insert
    suspend fun insertSource(source: PlaybackSource): Long
    
    /**
     * 更新播放源
     */
    @Update
    suspend fun updateSource(source: PlaybackSource)
    
    /**
     * 删除播放源
     */
    @Query("DELETE FROM playback_sources WHERE id = :id")
    suspend fun deleteSource(id: Int)
    
    /**
     * 获取所有播放源
     */
    @Query("SELECT * FROM playback_sources ORDER BY createdAt DESC")
    fun getAllSources(): Flow<List<PlaybackSource>>
    
    /**
     * 根据名称获取播放源
     */
    @Query("SELECT * FROM playback_sources WHERE name = :name LIMIT 1")
    suspend fun getSourceByName(name: String): PlaybackSource?
}
