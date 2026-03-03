package com.dlh.toolmedia3.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dlh.toolmedia3.data.dao.CacheInfoDao
import com.dlh.toolmedia3.data.dao.DownloadTaskDao
import com.dlh.toolmedia3.data.dao.PlaybackProgressDao
import com.dlh.toolmedia3.data.dao.SourceDao
import com.dlh.toolmedia3.data.dao.UserSettingDao
import com.dlh.toolmedia3.data.dao.VideoFavoriteDao
import com.dlh.toolmedia3.data.dao.VideoHistoryDao
import com.dlh.toolmedia3.data.dao.VideoSourceDao
import com.dlh.toolmedia3.data.model.CacheInfo
import com.dlh.toolmedia3.data.model.DownloadTask
import com.dlh.toolmedia3.data.model.PlaybackProgress
import com.dlh.toolmedia3.data.model.PlaybackSource
import com.dlh.toolmedia3.data.model.UserSetting
import com.dlh.toolmedia3.data.model.VideoFavorite
import com.dlh.toolmedia3.data.model.VideoHistory
import com.dlh.toolmedia3.data.model.VideoSource

@Database(
    entities = [
        VideoHistory::class,
        PlaybackProgress::class,
        DownloadTask::class,
        VideoFavorite::class,
        UserSetting::class,
        CacheInfo::class,
        VideoSource::class,
        PlaybackSource::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoHistoryDao(): VideoHistoryDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun downloadTaskDao(): DownloadTaskDao
    abstract fun videoFavoriteDao(): VideoFavoriteDao
    abstract fun userSettingDao(): UserSettingDao
    abstract fun cacheInfoDao(): CacheInfoDao
    abstract fun videoSourceDao(): VideoSourceDao
    abstract fun sourceDao(): SourceDao
}
