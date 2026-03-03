package com.dlh.toolmedia3.network.repository

import android.content.Context
import androidx.media3.common.util.Log
import com.dlh.toolmedia3.network.NetworkService
import com.dlh.toolmedia3.network.client.OkHttpClientManager
import com.dlh.toolmedia3.network.model.VideoListResponse
import com.dlh.toolmedia3.network.model.VideoDetailResponse
import com.dlh.toolmedia3.network.service.ApiService
import com.dlh.toolmedia3.network.service.RetrofitService
import com.dlh.toolmedia3.network.state.NetworkStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import retrofit2.Response

/**
 * 网络请求封装类
 * 提供统一的网络请求处理、错误处理和网络状态检查
 */
class NetworkRepository(private val context: Context) {

    private val apiService: ApiService by lazy {
        NetworkService.getApiService()
    }

    private val networkStateManager: NetworkStateManager by lazy {
        NetworkStateManager.getInstance(context)
    }

    /**
     * 执行网络请求
     * @param request 网络请求函数
     * @param T 响应数据类型
     * @return 网络请求结果
     */
    suspend fun <T> executeRequest(request: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                // 检查网络状态
                if (!isNetworkAvailable()) {
                    return@withContext NetworkResult.Error("网络连接不可用")
                }

                val response = request()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error("响应数据为空")
                    }
                } else {
                    NetworkResult.Error("请求失败: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NetworkRepository", "executeRequest: ${e.message}")
                NetworkResult.Error("网络请求异常: ${e.message}")

            }
        }
    }

    /**
     * 检查网络是否可用
     * @return 网络是否可用
     */
    private fun isNetworkAvailable(): Boolean {
        return networkStateManager.isNetworkAvailable()
    }

    /**
     * 获取视频列表
     * @param pg 页码
     * @return 视频列表网络请求结果
     */
    suspend fun getVideoList(pg: Int = 3): NetworkResult<VideoListResponse> {
        return executeRequest {
            apiService.getVideoList(pg = pg)
        }
    }

    /**
     * 获取视频详情
     * @param pg 页码
     * @return 视频详情网络请求结果
     */
    suspend fun getVideoDetail(pg: Int = 1): NetworkResult<VideoDetailResponse> {
        return executeRequest {
            apiService.getVideoDetail(pg = pg)
        }
    }

    /**
     * 通用网络请求
     * @param url 请求地址
     * @return 网络请求结果
     */
    suspend fun genericRequest(url: String): NetworkResult<String> {
        return executeRequest {

            apiService.genericGetRequest(url)
        }
    }
}

/**
 * 网络请求结果密封类
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
}
