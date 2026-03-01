package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.PlaybackProgress

@Dao
interface PlaybackProgressDao {
    @Query("SELECT * FROM playback_progress WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getProgressByUrl(videoUrl: String): PlaybackProgress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: PlaybackProgress)
    
    @Update
    suspend fun update(progress: PlaybackProgress)
    
    @Query("DELETE FROM playback_progress WHERE videoUrl = :videoUrl")
    suspend fun deleteByUrl(videoUrl: String)
    
    @Query("DELETE FROM playback_progress")
    suspend fun deleteAll()
}