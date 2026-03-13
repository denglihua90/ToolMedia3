package com.dlh.toolmedia3.architecture.processor

import androidx.media3.common.Player
import com.dlh.toolmedia3.architecture.state.PlayerState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@androidx.media3.common.util.UnstableApi
class PlayerProcessorTest {

    private lateinit var processor: PlayerProcessor

    @Before
    fun setup() {
        // 创建一个没有player的PlayerProcessor实例
        processor = PlayerProcessor(mockkContext())
    }

    @After
    fun teardown() {
        // 清理资源
    }

    // 简单的Context mock实现
    private fun mockkContext(): android.content.Context {
        return object : android.content.Context {
            override fun getApplicationContext() = this
            override fun getSystemService(name: String) = null
            override fun getPackageName() = "com.dlh.toolmedia3"
            override fun getResources() = null
            override fun getAssets() = null
            override fun getClassLoader() = null
            override fun getTheme() = null
            override fun setTheme(resid: Int) {}
            override fun getSharedPreferences(name: String, mode: Int) = null
            override fun getString(id: Int) = ""
            override fun getString(id: Int, vararg formatArgs: Any?) = ""
            override fun getText(id: Int) = null
            override fun getDrawable(id: Int) = null
            override fun getColor(id: Int) = 0
            override fun getColorStateList(id: Int) = null
            override fun getDimension(id: Int) = 0f
            override fun getDimensionPixelOffset(id: Int) = 0
            override fun getDimensionPixelSize(id: Int) = 0
            override fun getFraction(id: Int, base: Int, pbase: Int) = 0f
            override fun getInteger(id: Int) = 0
            override fun getBoolean(id: Int) = false
            override fun getStringArray(id: Int) = emptyArray<String>()
            override fun getIntArray(id: Int) = emptyIntArray()
            override fun getTextArray(id: Int) = emptyArray<CharSequence>()
            override fun obtainStyledAttributes(attrs: IntArray) = null
            override fun obtainStyledAttributes(resid: Int, attrs: IntArray) = null
            override fun obtainStyledAttributes(theme: android.content.res.Resources.Theme?, attrs: IntArray) = null
            override fun obtainStyledAttributes(theme: android.content.res.Resources.Theme?, resid: Int, attrs: IntArray) = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?) = null
            override fun unregisterReceiver(receiver: android.content.BroadcastReceiver?) {}
            override fun sendBroadcast(intent: android.content.Intent?) {}
            override fun sendBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun sendStickyBroadcast(intent: android.content.Intent?) {}
            override fun sendStickyOrderedBroadcast(intent: android.content.Intent?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun removeStickyBroadcast(intent: android.content.Intent?) {}
            override fun startService(service: android.content.Intent?) = null
            override fun stopService(name: android.content.Intent?) = false
            override fun bindService(service: android.content.Intent?, conn: android.content.ServiceConnection?, flags: Int) = false
            override fun unbindService(conn: android.content.ServiceConnection?) {}
            override fun startInstrumentation(ai: android.content.ComponentName?, profileFile: String?, arguments: android.os.Bundle?) = null
            override fun grantUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) {}
            override fun revokeUriPermission(uri: android.net.Uri?, modeFlags: Int) {}
            override fun checkUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int) = 0
            override fun checkUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int) = 0
            override fun getContentResolver() = null
            override fun openFileOutput(name: String?, mode: Int) = null
            override fun openFileInput(name: String?) = null
            override fun deleteFile(name: String?) = false
            override fun fileList() = emptyArray<String>()
            override fun getFileStreamPath(name: String?) = null
            override fun getCacheDir() = null
            override fun getFilesDir() = null
            override fun getDir(name: String?, mode: Int) = null
            override fun getExternalCacheDir() = null
            override fun getExternalFilesDir(type: String?) = null
            override fun getExternalMediaDirs() = emptyArray<java.io.File>()
            override fun getObbDir() = null
            override fun getExternalCacheDirs() = emptyArray<java.io.File>()
            override fun getExternalFilesDirs(type: String?) = emptyArray<java.io.File>()
            override fun getExternalStorageState() = ""
            override fun getPackageManager() = null
            override fun getSystemServiceName(serviceClass: Class<*>) = ""
            override fun startActivities(intents: Array<out android.content.Intent>?) {}
            override fun startActivities(intents: Array<out android.content.Intent>?, options: android.os.Bundle?) {}
            override fun startActivity(intent: android.content.Intent?) {}
            override fun startActivity(intent: android.content.Intent?, options: android.os.Bundle?) {}
            override fun startIntentSenderForResult(sender: android.content.IntentSender?, requestCode: Int, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: android.os.Bundle?) {}
            override fun startActivityForResult(intent: android.content.Intent?, requestCode: Int) {}
            override fun startActivityForResult(intent: android.content.Intent?, requestCode: Int, options: android.os.Bundle?) {}
            override fun createPendingResult(requestCode: Int, data: android.content.Intent?, flags: Int) = null
            override fun checkCallingOrSelfPermission(permission: String?) = 0
            override fun checkCallingPermission(permission: String?) = 0
            override fun checkSelfPermission(permission: String?) = 0
            override fun checkPermission(permission: String?, pid: Int, uid: Int) = 0
            override fun enforcePermission(permission: String?, pid: Int, uid: Int, message: String?) {}
            override fun enforceCallingOrSelfPermission(permission: String?, message: String?) {}
            override fun enforceCallingPermission(permission: String?, message: String?) {}
            override fun getMainLooper() = null
            override fun getWallpaper() = null
            override fun setWallpaper(bitmap: android.graphics.Bitmap?) {}
            override fun setWallpaper(bytes: ByteArray?) {}
            override fun clearWallpaper() {}
            override fun getWallpaperDesiredMinimumWidth() = 0
            override fun getWallpaperDesiredMinimumHeight() = 0
            override fun getCacheQuotaBytes(quotaUuid: String?) = 0L
            override fun getCodeCacheDir() = null
            override fun getNoBackupFilesDir() = null
            override fun getDatabasePath(name: String?) = null
            override fun moveDatabaseFrom(sourceContext: android.content.Context?, name: String?) = false
            override fun getFileStreamDescriptor(name: String?) = null
            override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?) = null
            override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?, errorHandler: android.database.sqlite.SQLiteDatabase.CursorFactory?) = null
            override fun deleteDatabase(name: String?) = false
            override fun getDatabasePath(name: String?) = null
            override fun moveDatabaseFrom(sourceContext: android.content.Context?, name: String?) = false
            override fun getFilesDir() = null
            override fun getDir(name: String?, mode: Int) = null
            override fun getExternalCacheDir() = null
            override fun getExternalFilesDir(type: String?) = null
            override fun getExternalMediaDirs() = emptyArray<java.io.File>()
            override fun getObbDir() = null
            override fun getExternalCacheDirs() = emptyArray<java.io.File>()
            override fun getExternalFilesDirs(type: String?) = emptyArray<java.io.File>()
            override fun getExternalStorageState() = ""
            override fun getPackageManager() = null
            override fun getSystemServiceName(serviceClass: Class<*>) = ""
        }
    }

    @Test
    fun `测试初始状态`() {
        val initialState = processor.state.value
        assertEquals(Player.STATE_IDLE, initialState.playState)
        assertEquals(0L, initialState.currentPosition)
        assertEquals(0L, initialState.duration)
        assertEquals(0L, initialState.bufferedPosition)
        assertEquals(false, initialState.isPlaying)
        assertEquals(1.0f, initialState.volume)
        assertEquals(0.5f, initialState.brightness)
        assertEquals(1.0f, initialState.playbackSpeed)
        assertEquals(false, initialState.isFullScreen)
        assertEquals(false, initialState.isPictureInPicture)
        assertEquals(PlayerState.SCREEN_MODE_NORMAL, initialState.screenMode)
        assertEquals(PlayerState.SCALE_TYPE_FIT_CENTER, initialState.scaleType)
        assertEquals(0, initialState.videoRotation)
        assertEquals(false, initialState.isVideoMirrored)
        assertEquals(false, initialState.isAudioOnlyMode)
        assertEquals(0L, initialState.cacheSize)
        assertEquals(512 * 1024 * 1024L, initialState.maxCacheSize)
        assertEquals(PlayerState.NETWORK_UNKNOWN, initialState.networkType)
        assertEquals(true, initialState.isCutoutAdapted)
        assertEquals(null, initialState.errorMessage)
    }

    @Test
    fun `测试updatePlayerState方法`() {
        // 初始状态
        val initialState = processor.state.value
        assertEquals(0L, initialState.currentPosition)
        assertEquals(0L, initialState.bufferedPosition)
        assertEquals(0L, initialState.duration)
        assertEquals(false, initialState.isPlaying)

        // 更新状态
        processor.updatePlayerState(1000L, 2000L, 3000L, true)

        // 验证状态更新
        val updatedState = processor.state.value
        assertEquals(1000L, updatedState.currentPosition)
        assertEquals(2000L, updatedState.bufferedPosition)
        assertEquals(3000L, updatedState.duration)
        assertEquals(true, updatedState.isPlaying)
    }

    @Test
    fun `测试clearErrorState方法`() {
        // 清除错误状态
        processor.clearErrorState()

        // 验证错误状态清除
        assertEquals(null, processor.state.value.errorMessage)
    }

    @Test
    fun `验证与现有PlayerState管理的兼容性`() {
        // 测试状态更新后其他属性保持不变
        processor.updatePlayerState(5000L, 10000L, 15000L, true)
        val updatedState = processor.state.value
        
        // 验证更新的属性
        assertEquals(5000L, updatedState.currentPosition)
        assertEquals(10000L, updatedState.bufferedPosition)
        assertEquals(15000L, updatedState.duration)
        assertEquals(true, updatedState.isPlaying)
        
        // 验证其他属性保持不变
        assertEquals(Player.STATE_IDLE, updatedState.playState)
        assertEquals(1.0f, updatedState.volume)
        assertEquals(0.5f, updatedState.brightness)
        assertEquals(1.0f, updatedState.playbackSpeed)
        assertEquals(false, updatedState.isFullScreen)
        assertEquals(false, updatedState.isPictureInPicture)
        assertEquals(PlayerState.SCREEN_MODE_NORMAL, updatedState.screenMode)
        assertEquals(PlayerState.SCALE_TYPE_FIT_CENTER, updatedState.scaleType)
    }
}
