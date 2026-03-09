package com.dlh.toolmedia3.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dlh.toolmedia3.data.dao.CategoryItemDao
import com.dlh.toolmedia3.data.dao.PlaybackSourceDao
import com.dlh.toolmedia3.data.dao.VideoItemDao
import com.dlh.toolmedia3.data.repository.PlaybackSourceRepository
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class DatabaseManager private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null
        
        // 数据库迁移：从版本1到版本2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 重命名表以保留原始数据
                database.execSQL("ALTER TABLE playback_sources RENAME TO playback_sources_old")
                
                // 创建新表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS playback_sources (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        url TEXT NOT NULL,
                        created_at INTEGER NOT NULL DEFAULT 0,
                        updated_at INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // 从旧表迁移数据到新表
                database.execSQL("""
                    INSERT INTO playback_sources (id, name, url, created_at, updated_at)
                    SELECT id, name, url, CAST(strftime('%s', createdAt) * 1000 AS INTEGER), CAST(strftime('%s', updatedAt) * 1000 AS INTEGER)
                    FROM playback_sources_old
                """)
                
                // 删除旧表
                database.execSQL("DROP TABLE playback_sources_old")
                
                // 添加索引
                database.execSQL("CREATE INDEX IF NOT EXISTS index_playback_sources_name ON playback_sources (name)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_playback_sources_url ON playback_sources (url)")
            }
        }
        
        // 数据库迁移：从版本2到版本3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建categories表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        type_id TEXT NOT NULL,
                        type_pid TEXT NOT NULL,
                        type_name TEXT NOT NULL,
                        playbackSourceId INTEGER NOT NULL,
                        FOREIGN KEY (playbackSourceId) REFERENCES playback_sources (id) ON DELETE CASCADE
                    )
                """)
                
                // 添加categories表索引
                database.execSQL("CREATE INDEX IF NOT EXISTS index_categories_playbackSourceId ON categories (playbackSourceId)")
                
                // 创建videos表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS videos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vod_time TEXT NOT NULL,
                        vod_id TEXT NOT NULL,
                        vod_name TEXT NOT NULL,
                        vod_enname TEXT NOT NULL,
                        vod_en TEXT NOT NULL DEFAULT '',
                        vod_sub TEXT NOT NULL DEFAULT '',
                        vod_letter TEXT NOT NULL DEFAULT '',
                        vod_color TEXT NOT NULL DEFAULT '',
                        vod_tag TEXT NOT NULL DEFAULT '',
                        vod_class TEXT NOT NULL DEFAULT '',
                        type_id TEXT NOT NULL,
                        type_name TEXT NOT NULL,
                        vod_pic TEXT NOT NULL DEFAULT '',
                        vod_lang TEXT NOT NULL DEFAULT '',
                        vod_area TEXT NOT NULL DEFAULT '',
                        vod_year TEXT NOT NULL DEFAULT '',
                        vod_remarks TEXT NOT NULL,
                        vod_actor TEXT NOT NULL DEFAULT '',
                        vod_director TEXT NOT NULL DEFAULT '',
                        vod_serial TEXT NOT NULL DEFAULT '',
                        vod_isend INTEGER NOT NULL DEFAULT 0,
                        vod_lock TEXT NOT NULL DEFAULT '',
                        vod_level TEXT NOT NULL DEFAULT '',
                        vod_hits TEXT NOT NULL DEFAULT '',
                        vod_hits_day TEXT NOT NULL DEFAULT '',
                        vod_hits_week TEXT NOT NULL DEFAULT '',
                        vod_hits_month TEXT NOT NULL DEFAULT '',
                        vod_duration TEXT NOT NULL DEFAULT '',
                        vod_up TEXT NOT NULL DEFAULT '',
                        vod_down TEXT NOT NULL DEFAULT '',
                        vod_score TEXT NOT NULL DEFAULT '',
                        vod_score_all TEXT NOT NULL DEFAULT '',
                        vod_score_num TEXT NOT NULL DEFAULT '',
                        vod_points_play TEXT NOT NULL DEFAULT '',
                        vod_points_down TEXT NOT NULL DEFAULT '',
                        vod_content TEXT NOT NULL DEFAULT '',
                        vod_play_from TEXT NOT NULL,
                        vod_play_note TEXT NOT NULL DEFAULT '',
                        vod_play_server TEXT NOT NULL DEFAULT '',
                        vod_play_url TEXT NOT NULL DEFAULT '',
                        vod_down_from TEXT NOT NULL DEFAULT '',
                        vod_down_note TEXT NOT NULL DEFAULT '',
                        vod_down_server TEXT NOT NULL DEFAULT '',
                        vod_down_url TEXT NOT NULL DEFAULT ''
                    )
                """)
            }
        }
        
        fun getInstance(context: Context): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    val database: AppDatabase
    val playbackSourceRepository: PlaybackSourceRepository
    val categoryItemDao: CategoryItemDao
    val videoItemDao: VideoItemDao
    
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
        
        // 初始化PlaybackSourceRepository
        playbackSourceRepository = PlaybackSourceRepository(database.playbackSourceDao(), database.categoryItemDao())
        
        // 初始化DAO
        categoryItemDao = database.categoryItemDao()
        videoItemDao = database.videoItemDao()
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