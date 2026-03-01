package com.dlh.toolmedia3.network.service

import com.dlh.toolmedia3.network.client.OkHttpClientManager
import com.dlh.toolmedia3.network.model.VideoListResponse
import com.dlh.toolmedia3.network.model.VideoDetailResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RetrofitService {

    companion object {
        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            if (retrofit == null) {
                synchronized(RetrofitService::class.java) {
                    if (retrofit == null) {
                        retrofit = buildRetrofit()
                    }
                }
            }
            return retrofit!!
        }

        fun refreshRetrofit() {
            retrofit = buildRetrofit()
        }

        private fun buildRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(OkHttpClientManager.getBaseUrl())
                .client(OkHttpClientManager.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        inline fun <reified T> create(service: Class<T>): T {
            return getInstance().create(service)
        }
    }
}

/**
 * API 接口定义
 */
interface ApiService {
    /**
     * 获取视频列表
     * @param ac 操作类型，固定为 "list"
     * @param t 类型参数，固定为 "66"
     * @param h 小时参数，固定为 "24"
     * @param pg 页码
     * @return 视频列表响应
     */
    @GET("inc/api_mac10.php")
    suspend fun getVideoList(
        @Query("ac") ac: String = "list",
        @Query("t") t: String = "66",
        @Query("h") h: String = "24",
        @Query("pg") pg: Int = 3
    ): Response<VideoListResponse>

    /**
     * 获取视频详情
     * @param ac 操作类型，固定为 "detail"
     * @param t 类型参数，固定为 "66"
     * @param h 小时参数，固定为 "24"
     * @param pg 页码
     * @return 视频详情响应
     */
    @GET("inc/api_mac10.php")
    suspend fun getVideoDetail(
        @Query("ac") ac: String = "detail",
        @Query("t") t: String = "66",
        @Query("h") h: String = "24",
        @Query("pg") pg: Int = 1
    ): Response<VideoDetailResponse>
}
