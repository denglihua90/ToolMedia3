package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.dlh.toolmedia3.data.model.VideoSource

@Dao
interface VideoSourceDao {
    // 插入单个VideoSource
    @Insert
    suspend fun insert(videoSource: VideoSource)

    // 插入多个VideoSource
    @Insert
    suspend fun insertAll(videoSources: List<VideoSource>)

    // 更新VideoSource
    @Update
    suspend fun update(videoSource: VideoSource)

    // 删除VideoSource
    @Delete
    suspend fun delete(videoSource: VideoSource)

    // 根据ID删除VideoSource
    @Query("DELETE FROM video_source WHERE id = :id")
    suspend fun deleteById(id: Int)

    // 删除所有VideoSource
    @Query("DELETE FROM video_source")
    suspend fun deleteAll()

    // 根据ID查询VideoSource
    @Query("SELECT * FROM video_source WHERE id = :id")
    suspend fun getById(id: Int): VideoSource?

    // 查询所有VideoSource
    @Query("SELECT * FROM video_source")
    suspend fun getAll(): List<VideoSource>

    // 根据源名称查询VideoSource
    @Query("SELECT * FROM video_source WHERE sourceName = :sourceName")
    suspend fun getBySourceName(sourceName: String): VideoSource?

    // 根据源URL查询VideoSource
    @Query("SELECT * FROM video_source WHERE sourceUrl = :sourceUrl")
    suspend fun getBySourceUrl(sourceUrl: String): VideoSource?

    // 统计VideoSource数量
    @Query("SELECT COUNT(*) FROM video_source")
    suspend fun count(): Int
}
