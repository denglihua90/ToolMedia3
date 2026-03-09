package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.VideoItem

/**
 * 视频项DAO接口
 */
@Dao
interface VideoItemDao {
    /**
     * 插入视频项
     */
    @Insert
    suspend fun insertVideo(video: VideoItem)

    /**
     * 批量插入视频项
     */
    @Insert
    suspend fun insertVideos(videos: List<VideoItem>)

    /**
     * 更新视频项
     */
    @Update
    suspend fun updateVideo(video: VideoItem)

    /**
     * 删除视频项
     */
    @Delete
    suspend fun deleteVideo(video: VideoItem)

    /**
     * 根据ID删除视频项
     */
    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: Int)

    /**
     * 获取所有视频项
     */
    @Query("SELECT * FROM videos")
    suspend fun getAllVideos(): List<VideoItem>

    /**
     * 根据ID获取视频项
     */
    @Query("SELECT * FROM videos WHERE id = :id")
    suspend fun getVideoById(id: Int): VideoItem?

    /**
     * 根据vodId获取视频项
     */
    @Query("SELECT * FROM videos WHERE vodId = :vodId")
    suspend fun getVideoByVodId(vodId: String): VideoItem?

    /**
     * 根据typeId获取视频项
     */
    @Query("SELECT * FROM videos WHERE typeId = :typeId")
    suspend fun getVideosByTypeId(typeId: String): List<VideoItem>

    /**
     * 搜索视频项
     */
    @Query("SELECT * FROM videos WHERE vodName LIKE :keyword OR vodActor LIKE :keyword OR vodDirector LIKE :keyword")
    suspend fun searchVideos(keyword: String): List<VideoItem>

    /**
     * 获取视频项数量
     */
    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int

    /**
     * 根据typeId获取视频项数量
     */
    @Query("SELECT COUNT(*) FROM videos WHERE typeId = :typeId")
    suspend fun getVideoCountByTypeId(typeId: String): Int

    /**
     * 清空所有视频项
     */
    @Query("DELETE FROM videos")
    suspend fun clearAllVideos()
}
