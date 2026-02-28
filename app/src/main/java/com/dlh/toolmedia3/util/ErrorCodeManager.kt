package com.dlh.toolmedia3.util

import android.content.Context
import androidx.media3.common.PlaybackException
import com.dlh.toolmedia3.R

/**
 * 错误代码管理工具类
 * 用于集中管理 Media3 错误代码和对应的错误信息
 */
object ErrorCodeManager {
    
    // 错误代码范围定义
    
    /**
     * 网络错误范围起始值
     */
    const val NETWORK_ERROR_START = 1000
    
    /**
     * 网络错误范围结束值
     */
    const val NETWORK_ERROR_END = 1099
    
    /**
     * 解码错误范围起始值
     */
    const val DECODE_ERROR_START = 1100
    
    /**
     * 解码错误范围结束值
     */
    const val DECODE_ERROR_END = 1199
    
    /**
     * 格式错误范围起始值
     */
    const val FORMAT_ERROR_START = 2000
    
    /**
     * 格式错误范围结束值
     */
    const val FORMAT_ERROR_END = 2099
    
    /**
     * 初始化错误范围起始值
     */
    const val INITIALIZATION_ERROR_START = 3000
    
    /**
     * 初始化错误范围结束值
     */
    const val INITIALIZATION_ERROR_END = 3099
    
    // 特定错误代码
    
    /**
     * 播放格式不支持
     */
    const val ERROR_CODE_FORMAT_UNSUPPORTED = 2000
    
    /**
     * 播放源错误
     */
    const val ERROR_CODE_SOURCE_ERROR = 2004
    
    /**
     * 播放器初始化失败
     */
    const val ERROR_CODE_INITIALIZATION_FAILED = 3000
    
    /**
     * 根据错误代码获取错误信息
     * 
     * @param context 上下文对象，用于获取字符串资源
     * @param errorCode 错误代码
     * @return 错误信息字符串
     */
    fun getErrorMessage(context: Context, errorCode: Int): String {
        return when {
            // 网络错误范围
            isNetworkError(errorCode) -> context.getString(R.string.error_network)
            // 解码错误范围
            isDecodeError(errorCode) -> context.getString(R.string.error_decode)
            // 格式错误范围
            isFormatError(errorCode) -> when (errorCode) {
                ERROR_CODE_FORMAT_UNSUPPORTED -> context.getString(R.string.error_format_unsupported)
                ERROR_CODE_SOURCE_ERROR -> context.getString(R.string.error_source)
                else -> context.getString(R.string.error_format_generic, errorCode)
            }
            // 初始化错误范围
            isInitializationError(errorCode) -> when (errorCode) {
                ERROR_CODE_INITIALIZATION_FAILED -> context.getString(R.string.error_initialization_failed)
                else -> context.getString(R.string.error_initialization_generic, errorCode)
            }
            // 其他错误
            else -> context.getString(R.string.error_generic, errorCode)
        }
    }
    
    /**
     * 从 PlaybackException 获取错误信息
     * 
     * @param context 上下文对象，用于获取字符串资源
     * @param error PlaybackException 对象
     * @return 错误信息字符串
     */
    fun getErrorMessage(context: Context, error: PlaybackException): String {
        return getErrorMessage(context, error.errorCode)
    }
    
    /**
     * 检查是否为网络错误
     * 
     * @param errorCode 错误代码
     * @return 是否为网络错误
     */
    fun isNetworkError(errorCode: Int): Boolean {
        return errorCode in NETWORK_ERROR_START..NETWORK_ERROR_END
    }
    
    /**
     * 检查是否为解码错误
     * 
     * @param errorCode 错误代码
     * @return 是否为解码错误
     */
    fun isDecodeError(errorCode: Int): Boolean {
        return errorCode in DECODE_ERROR_START..DECODE_ERROR_END
    }
    
    /**
     * 检查是否为格式错误
     * 
     * @param errorCode 错误代码
     * @return 是否为格式错误
     */
    fun isFormatError(errorCode: Int): Boolean {
        return errorCode in FORMAT_ERROR_START..FORMAT_ERROR_END
    }
    
    /**
     * 检查是否为初始化错误
     * 
     * @param errorCode 错误代码
     * @return 是否为初始化错误
     */
    fun isInitializationError(errorCode: Int): Boolean {
        return errorCode in INITIALIZATION_ERROR_START..INITIALIZATION_ERROR_END
    }
    
    /**
     * 获取错误类型描述
     * 
     * @param context 上下文对象，用于获取字符串资源
     * @param errorCode 错误代码
     * @return 错误类型描述
     */
    fun getErrorType(context: Context, errorCode: Int): String {
        return when {
            isNetworkError(errorCode) -> context.getString(R.string.error_network)
            isDecodeError(errorCode) -> context.getString(R.string.error_decode)
            isFormatError(errorCode) -> context.getString(R.string.error_format_unsupported)
            isInitializationError(errorCode) -> context.getString(R.string.error_initialization_failed)
            else -> context.getString(R.string.error_unknown)
        }
    }
    
    /**
     * 获取 HTTP 错误信息
     * 
     * @param context 上下文对象，用于获取字符串资源
     * @param responseCode HTTP 响应码
     * @param responseMessage HTTP 响应消息
     * @return HTTP 错误信息字符串
     */
    fun getHttpErrorMessage(context: Context, responseCode: Int, responseMessage: String): String {
        return when (responseCode) {
            in 400..499 -> context.getString(R.string.error_http_request, responseCode, responseMessage)
            in 500..599 -> context.getString(R.string.error_http_server, responseCode, responseMessage)
            403 -> context.getString(R.string.error_http_forbidden)
            404 -> context.getString(R.string.error_http_not_found)
            408 -> context.getString(R.string.error_http_timeout)
            else -> context.getString(R.string.error_http_generic, responseCode, responseMessage)
        }
    }
    
    /**
     * 获取 HTTP 连接错误信息
     * 
     * @param context 上下文对象，用于获取字符串资源
     * @param cause 错误原因
     * @return HTTP 连接错误信息字符串
     */
    fun getHttpConnectionErrorMessage(context: Context, cause: Throwable?): String {
        return if (cause != null && cause.message != null) {
            context.getString(R.string.error_http_cause, cause.message)
        } else {
            context.getString(R.string.error_http_connection_failed)
        }
    }
}