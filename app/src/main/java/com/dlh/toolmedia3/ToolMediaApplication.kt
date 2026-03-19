package com.dlh.toolmedia3

import android.app.Application
import com.dlh.toolmedia3.network.NetworkService
import com.dlh.toolmedia3.network.client.DiskLruCacheManager
import com.dlh.toolmedia3.network.client.OkHttpClientManager
import com.dlh.toolmedia3.util.DLHLog
import com.dlh.toolmedia3.video.PlayerPool

import com.dlh.toolmedia3.BuildConfig
import com.drake.statelayout.StateConfig

@androidx.media3.common.util.UnstableApi
class ToolMediaApplication : Application() {

    companion object {
        private lateinit var instance: ToolMediaApplication
        lateinit var playerPool: PlayerPool

        fun getInstance(): ToolMediaApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
      // 启用文件日志
        DLHLog.enableFileLog(true)
        // 设置日志级别
        DLHLog.setGlobalLogLevel(if (BuildConfig.DEBUG) DLHLog.LogLevel.DEBUG else DLHLog.LogLevel.INFO)
        // 初始化播放器池
        playerPool = PlayerPool.getInstance(this)
        // 预创建一个播放器实例
        val player = playerPool.acquire()
        playerPool.release(player)
        // 初始化网络相关组件
        initializeNetworkComponents()

        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            errorLayout = R.layout.layout_error
            loadingLayout = R.layout.layout_loading

            setRetryIds(R.id.msg) // 全局的重试Id

            onLoading {

            }

            onEmpty {

            }

            onError {

            }
        }
    }

    /**
     * 初始化网络相关组件
     */
    private fun initializeNetworkComponents() {
        // 初始化 OkHttpClientManager
        OkHttpClientManager.Companion.initialize(this)

        // 初始化 DiskLruCacheManager
        DiskLruCacheManager.Companion.initialize(this)

        // 初始化 NetworkService
        NetworkService.initialize()
    }
}