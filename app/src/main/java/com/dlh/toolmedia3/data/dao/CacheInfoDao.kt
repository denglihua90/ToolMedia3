package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.CacheInfo

@Dao
interface CacheInfoDao {
    @Query("SELECT * FROM cache_info WHERE url = :url LIMIT 1")
    suspend fun getCacheByUrl(url: String): CacheInfo?
    
    @Query("SELECT * FROM cache_info ORDER BY lastAccessed DESC")
    suspend fun getAllCaches(): List<CacheInfo>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cacheInfo: CacheInfo)
    
    @Update
    suspend fun update(cacheInfo: CacheInfo)
    
    @Query("DELETE FROM cache_info WHERE url = :url")
    suspend fun deleteByUrl(url: String)
    
    @Query("DELETE FROM cache_info")
    suspend fun deleteAll()
}