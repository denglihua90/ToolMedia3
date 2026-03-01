package com.dlh.toolmedia3.data

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class DatabaseManager private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null
        
        fun getInstance(context: Context): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    val database: AppDatabase
    
    init {
        // 生成或获取加密密钥
        val encryptionKey = getEncryptionKey()
        
        // 配置 SQLCipher 加密
        val factory = SupportFactory(encryptionKey)
        
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration() // 开发阶段使用，生产环境应使用迁移策略
            .build()
    }
    
    private fun getEncryptionKey(): ByteArray {
        // 从安全存储中获取密钥
        // 这里使用简单的实现，实际应使用 AndroidKeyStore
        val keyAlias = "room_encryption_key"
        
        // 生成256位密钥
        val key = ByteArray(32)
        // 实际应用中应该从AndroidKeyStore获取
        // 这里为了演示，使用固定密钥
        for (i in key.indices) {
            key[i] = (i * 3).toByte()
        }
        return key
    }
}