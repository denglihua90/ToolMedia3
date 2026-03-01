package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dlh.toolmedia3.data.model.VideoFavorite

@Dao
interface VideoFavoriteDao {
    @Query("SELECT * FROM video_favorite ORDER BY addedAt DESC")
    suspend fun getAllFavorites(): List<VideoFavorite>
    
    @Query("SELECT * FROM video_favorite WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getFavoriteByUrl(videoUrl: String): VideoFavorite?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: VideoFavorite)
    
    @Query("DELETE FROM video_favorite WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("DELETE FROM video_favorite WHERE videoUrl = :videoUrl")
    suspend fun deleteByUrl(videoUrl: String)
    
    @Query("DELETE FROM video_favorite")
    suspend fun deleteAll()
}