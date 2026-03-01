package com.dlh.toolmedia3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_setting")
data class UserSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val key: String,
    val value: String,
    val updatedAt: Long
)