package com.dlh.toolmedia3.network.client

import android.content.Context
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class OkHttpClientManager {

    companion object {
        private const val DEFAULT_TIMEOUT = 15L
        private const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
        private var baseUrl: String = "https://api.yzzy-api.com/"
        private var okHttpClient: OkHttpClient? = null
        private var context: Context? = null

        fun initialize(context: Context) {
            this.context = context
        }

        fun getInstance(): OkHttpClient {
            if (okHttpClient == null) {
                synchronized(OkHttpClientManager::class.java) {
                    if (okHttpClient == null) {
                        okHttpClient = buildOkHttpClient()
                    }
                }
            }
            return okHttpClient!!
        }

        fun setBaseUrl(url: String) {
            baseUrl = url
            // 重新构建 OkHttpClient 以应用新的 baseUrl
            okHttpClient = buildOkHttpClient()
        }

        fun getBaseUrl(): String {
            return baseUrl
        }

        private fun buildOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val builder = OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(BaseUrlInterceptor())

            // 添加缓存配置
            context?.let {
                val cacheDir = File(it.cacheDir, "http_cache")
                val cache = Cache(cacheDir, CACHE_SIZE)
                builder.cache(cache)
            }

            return builder.build()
        }
    }

    /**
     * 动态 BaseUrl 拦截器
     * 用于在请求时动态替换 baseUrl
     * 当请求使用完整URL时，不进行baseUrl替换
     */
    private class BaseUrlInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val originalHttpUrl = request.url
            
            // 解析配置的 baseUrl
            val newBaseUrl = baseUrl.toHttpUrlOrNull() ?: originalHttpUrl
            
            // 检查原始URL的host是否与配置的baseUrl的host不同
            // 如果不同，则认为是完整URL，不进行baseUrl替换
            if (originalHttpUrl.host != newBaseUrl.host) {
                return chain.proceed(request)
            }
            
            // 构建新的 URL，保留原始路径和查询参数
            val newUrl = originalHttpUrl.newBuilder()
                .scheme(newBaseUrl.scheme)
                .host(newBaseUrl.host)
                .port(newBaseUrl.port)
                .build()

            // 构建新的请求
            val newRequest: Request = request.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(newRequest)
        }
    }
}
