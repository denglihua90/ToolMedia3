package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.VideoHistory

@Dao
interface VideoHistoryDao {
    @Query("SELECT * FROM video_history ORDER BY lastPlayTime DESC")
    suspend fun getAllHistory(): List<VideoHistory>
    
    @Query("SELECT * FROM video_history WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getHistoryByUrl(videoUrl: String): VideoHistory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: VideoHistory)
    
    @Update
    suspend fun update(history: VideoHistory)
    
    @Query("DELETE FROM video_history WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("DELETE FROM video_history")
    suspend fun deleteAll()
}