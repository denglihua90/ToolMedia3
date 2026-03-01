package com.dlh.toolmedia3

import android.app.Application
import com.dlh.toolmedia3.network.NetworkService
import com.dlh.toolmedia3.network.client.DiskLruCacheManager
import com.dlh.toolmedia3.network.client.OkHttpClientManager

class ToolMediaApplication : Application() {

    companion object {
        private lateinit var instance: ToolMediaApplication

        fun getInstance(): ToolMediaApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化网络相关组件
        initializeNetworkComponents()
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