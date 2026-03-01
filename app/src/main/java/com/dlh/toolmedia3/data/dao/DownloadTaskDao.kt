package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.DownloadTask

@Dao
interface DownloadTaskDao {
    @Query("SELECT * FROM download_task ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<DownloadTask>
    
    @Query("SELECT * FROM download_task WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): DownloadTask?
    
    @Query("SELECT * FROM download_task WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getTaskByUrl(videoUrl: String): DownloadTask?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadTask)
    
    @Update
    suspend fun update(task: DownloadTask)
    
    @Query("DELETE FROM download_task WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("DELETE FROM download_task")
    suspend fun deleteAll()
}