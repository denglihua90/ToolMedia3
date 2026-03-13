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
        private const val CACHE_SIZE = 50 * 1024 * 1024L // 50MB 增加缓存大小
        private var baseUrl: String = "https://api.yzzy-api.com/"
        private var okHttpClient: OkHttpClient? = null
        private var context: Context? = null
        private var isDebug = true // 调试模式标志

        fun initialize(context: Context, debug: Boolean = true) {
            this.context = context
            this.isDebug = debug
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
            // 根据调试模式设置日志级别
            loggingInterceptor.level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }

            val builder = OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(BaseUrlInterceptor())
                .addInterceptor {
                    val request = it.request()
                    // 添加网络请求调试信息
                    if (isDebug) {
                        println("[Network] Request: ${request.method} ${request.url}")
                    }
                    val response = it.proceed(request)
                    if (isDebug) {
                        println("[Network] Response: ${response.code} ${response.message}")
                    }
                    response
                }

            // 添加缓存配置
            context?.let {
                val cacheDir = File(it.cacheDir, "http_cache")
                val cache = Cache(cacheDir, CACHE_SIZE)
                builder.cache(cache)
                // 添加缓存控制拦截器
                builder.addInterceptor {
                    val request = it.request()
                    val response = it.proceed(request)
                    // 设置缓存策略
                    response.newBuilder()
                        .header("Cache-Control", "max-age=86400") // 24小时缓存
                        .build()
                }
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
            val originalUrlString = originalHttpUrl.toString()
            
            // 解析配置的 baseUrl
            val newBaseUrl = baseUrl.toHttpUrlOrNull() ?: originalHttpUrl
            
            // 检查原始URL是否为完整URL（包含scheme和host）
            // 如果原始URL的host与配置的baseUrl的host不同，则不进行替换
            if (originalHttpUrl.host != newBaseUrl.host) {
                if (isDebug) {
                    println("[BaseUrlInterceptor] Using original URL: $originalUrlString")
                }
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

            if (isDebug) {
                println("[BaseUrlInterceptor] Replaced URL: $originalUrlString -> ${newUrl.toString()}")
            }

            return chain.proceed(newRequest)
        }
    }
}
