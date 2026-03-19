package com.dlh.toolmedia3.network

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.dlh.toolmedia3.ToolMediaApplication
import com.dlh.toolmedia3.network.repository.NetworkRepository
import com.dlh.toolmedia3.network.service.ApiService
import com.dlh.toolmedia3.network.service.RetrofitService

/**
 * 网络服务管理类
 * 提供全局单例访问网络功能
 */
@OptIn(UnstableApi::class)
object NetworkService {

    private lateinit var networkRepository: NetworkRepository
    private lateinit var apiService: ApiService

    /**
     * 初始化网络服务
     */
    fun initialize() {
        val context = ToolMediaApplication.getInstance()
        networkRepository = NetworkRepository(context)
        apiService = RetrofitService.create(ApiService::class.java)
    }

    /**
     * 获取 NetworkRepository 实例
     */
    fun getRepository(): NetworkRepository {
        if (!::networkRepository.isInitialized) {
            initialize()
        }
        return networkRepository
    }

    /**
     * 获取 ApiService 实例
     */
    fun getApiService(): ApiService {
        if (!::apiService.isInitialized) {
            initialize()
        }
        return apiService
    }

    /**
     * 刷新 Retrofit 实例
     * 当 baseUrl 或其他配置变更时调用
     */
    fun refreshRetrofit() {
        RetrofitService.refreshRetrofit()
        apiService = RetrofitService.create(ApiService::class.java)
    }
}
