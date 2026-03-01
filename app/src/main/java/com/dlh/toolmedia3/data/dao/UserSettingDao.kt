package com.dlh.toolmedia3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dlh.toolmedia3.data.model.UserSetting

@Dao
interface UserSettingDao {
    @Query("SELECT * FROM user_setting WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): UserSetting?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: UserSetting)
    
    @Update
    suspend fun update(setting: UserSetting)
    
    @Query("DELETE FROM user_setting WHERE key = :key")
    suspend fun deleteByKey(key: String)
    
    @Query("DELETE FROM user_setting")
    suspend fun deleteAll()
}