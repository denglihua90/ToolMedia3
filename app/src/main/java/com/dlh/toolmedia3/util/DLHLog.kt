package com.dlh.toolmedia3.util

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

/**
 * 日志类
 * 
 */
object DLHLog {
    private const val DEFAULT_TAG = "ToolMedia3"
    private const val LOG_DIR = "ToolMedia3/logs"
    private const val MAX_LOG_FILE_SIZE = 1024 * 1024 // 1MB
    private const val MAX_LOG_FILES = 5
    private const val LOG_BATCH_SIZE = 10 // 批量写入日志条数
    private const val LOG_BATCH_DELAY = 1000 // 批量写入延迟时间（毫秒）

    private var isDebug = true
    private var globalLogLevel = LogLevel.DEBUG
    private val moduleLogLevels = mutableMapOf<String, LogLevel>()
    private var isFileLogEnabled = false
    private val logExecutor = Executors.newSingleThreadExecutor()
    private var logFile: File? = null
    private var fileWriter: FileWriter? = null
    
    // 缓存调用位置信息
    private val logInfoCache = ConcurrentHashMap<String, LogInfo>()
    // 日志缓存队列
    private val logCacheQueue = LinkedBlockingQueue<LogEntry>()
    // LogInfo对象池
    private val logInfoPool = LinkedBlockingQueue<LogInfo>()
    // StringBuilder对象池
    private val stringBuilderPool = LinkedBlockingQueue<StringBuilder>()
    // 批量写入任务标志
    private var batchWriteScheduled = false

    /**
     * 日志级别枚举
     */
    enum class LogLevel(val value: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT)
    }

    /**
     * 调试日志
     */
    fun d(msg: () -> String) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.DEBUG, logInfo.tag)) {
                val message = msg()
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(message)
                    Log.d(logInfo.tag, sb.toString())
                    writeLogToFile("DEBUG", logInfo.tag, logInfo.location, message)
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 调试日志（直接字符串）
     */
    fun d(msg: String) {
        d { msg }
    }

    /**
     * 调试日志（带格式化）
     */
    fun d(msg: String, vararg args: Any) {
        d { String.format(msg, *args) }
    }

    /**
     * 错误日志
     */
    fun e(msg: () -> String) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.ERROR, logInfo.tag)) {
                val message = msg()
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(message)
                    Log.e(logInfo.tag, sb.toString())
                    writeLogToFile("ERROR", logInfo.tag, logInfo.location, message)
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 错误日志（直接字符串）
     */
    fun e(msg: String) {
        e { msg }
    }

    /**
     * 错误日志（带异常）
     */
    fun e(msg: String, throwable: Throwable) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.ERROR, logInfo.tag)) {
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(msg)
                    Log.e(logInfo.tag, sb.toString(), throwable)
                    writeLogToFile("ERROR", logInfo.tag, logInfo.location, "$msg\n${throwable.stackTraceToString()}")
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 错误日志（带格式化）
     */
    fun e(msg: String, vararg args: Any) {
        e { String.format(msg, *args) }
    }

    /**
     * 信息日志
     */
    fun i(msg: () -> String) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.INFO, logInfo.tag)) {
                val message = msg()
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(message)
                    Log.i(logInfo.tag, sb.toString())
                    writeLogToFile("INFO", logInfo.tag, logInfo.location, message)
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 信息日志（直接字符串）
     */
    fun i(msg: String) {
        i { msg }
    }

    /**
     * 信息日志（带格式化）
     */
    fun i(msg: String, vararg args: Any) {
        i { String.format(msg, *args) }
    }

    /**
     * 警告日志
     */
    fun w(msg: () -> String) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.WARN, logInfo.tag)) {
                val message = msg()
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(message)
                    Log.w(logInfo.tag, sb.toString())
                    writeLogToFile("WARN", logInfo.tag, logInfo.location, message)
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 警告日志（直接字符串）
     */
    fun w(msg: String) {
        w { msg }
    }

    /**
     * 警告日志（带格式化）
     */
    fun w(msg: String, vararg args: Any) {
        w { String.format(msg, *args) }
    }

    /**
     * 详细日志
     */
    fun v(msg: () -> String) {
        if (isDebug) {
            val logInfo = getLogInfo()
            if (shouldLog(LogLevel.VERBOSE, logInfo.tag)) {
                val message = msg()
                val sb = obtainStringBuilder()
                try {
                    sb.append(logInfo.location).append(" ").append(message)
                    Log.v(logInfo.tag, sb.toString())
                    writeLogToFile("VERBOSE", logInfo.tag, logInfo.location, message)
                } finally {
                    recycleStringBuilder(sb)
                }
            }
        }
    }

    /**
     * 详细日志（直接字符串）
     */
    fun v(msg: String) {
        v { msg }
    }

    /**
     * 详细日志（带格式化）
     */
    fun v(msg: String, vararg args: Any) {
        v { String.format(msg, *args) }
    }

    /**
     * 设置调试模式
     */
    fun setDebug(isDebug: Boolean) {
        DLHLog.isDebug = isDebug
    }

    /**
     * 设置全局日志级别
     */
    fun setGlobalLogLevel(level: LogLevel) {
        globalLogLevel = level
    }

    /**
     * 设置模块日志级别
     */
    fun setModuleLogLevel(module: String, level: LogLevel) {
        moduleLogLevels[module] = level
    }

    /**
     * 检查是否应该记录指定级别的日志
     */
    private fun shouldLog(level: LogLevel, tag: String): Boolean {
        if (!isDebug) return false
        
        val moduleLevel = moduleLogLevels[tag] ?: globalLogLevel
        return level.value >= moduleLevel.value
    }
    
    /**
     * 检查是否应该记录指定级别的日志
     */
    private fun shouldLog(level: LogLevel): Boolean {
        if (!isDebug) return false
        
        val logInfo = getLogInfo()
        return shouldLog(level, logInfo.tag)
    }

    /**
     * 启用文件日志
     */
    fun enableFileLog(enable: Boolean) {
        isFileLogEnabled = enable
        if (enable) {
            initLogFile()
        } else {
            closeLogFile()
        }
    }

    /**
     * 初始化日志文件
     */
    private fun initLogFile() {
        try {
            val logDir = File(Environment.getExternalStorageDirectory(), LOG_DIR)
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            
            // 清理旧日志文件
            cleanupOldLogFiles(logDir)
            
            // 创建新日志文件
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "log_${dateFormat.format(Date())}.txt"
            logFile = File(logDir, fileName)
            fileWriter = FileWriter(logFile, true)
            
            // 写入日志头部
            val header = "\n=== ToolMedia3 Log Start ===\n"
            fileWriter?.write(header)
            fileWriter?.flush()
        } catch (e: Exception) {
            Log.e(DEFAULT_TAG, "Failed to initialize log file", e)
        }
    }

    /**
     * 关闭日志文件
     */
    private fun closeLogFile() {
        try {
            fileWriter?.close()
            fileWriter = null
            logFile = null
        } catch (e: Exception) {
            Log.e(DEFAULT_TAG, "Failed to close log file", e)
        }
    }

    /**
     * 清理旧日志文件
     */
    private fun cleanupOldLogFiles(logDir: File) {
        val logFiles = logDir.listFiles { file -> file.name.endsWith(".txt") }?.sortedByDescending { it.lastModified() }
        if (logFiles != null && logFiles.size > MAX_LOG_FILES) {
            for (i in MAX_LOG_FILES until logFiles.size) {
                logFiles[i].delete()
            }
        }
    }

    /**
     * 写入日志到文件
     */
    private fun writeLogToFile(level: String, tag: String, location: String, message: String) {
        if (!isFileLogEnabled) return
        
        // 将日志添加到缓存队列
        logCacheQueue.offer(LogEntry(level, tag, location, message))
        
        // 触发批量写入
        if (logCacheQueue.size >= LOG_BATCH_SIZE || !batchWriteScheduled) {
            batchWriteLogs()
        }
    }

    /**
     * 检查日志文件大小
     */
    private fun checkLogFileSize() {
        val file = logFile ?: return
        if (file.length() > MAX_LOG_FILE_SIZE) {
            closeLogFile()
            initLogFile()
        }
    }

    /**
     * 获取日志信息
     */
    private fun getLogInfo(): LogInfo {
        // 优化：使用Throwable().stackTrace获取调用位置信息
        // 但只在必要时创建Throwable对象
        val stackTrace = Throwable().stackTrace
        var callerIndex = 0
        
        // 找到调用日志方法的类，跳过L类内部的调用
        for (i in 1 until stackTrace.size) {
            val className = stackTrace[i].className
            if (!className.equals(DLHLog::class.java.name, ignoreCase = true)) {
                callerIndex = i
                break
            }
        }
        
        val element = stackTrace[callerIndex]
        val cacheKey = "${element.className}.${element.methodName}:${element.lineNumber}"
        
        // 检查缓存
        val cachedLogInfo = logInfoCache[cacheKey]
        if (cachedLogInfo != null) {
            return cachedLogInfo
        }
        
        val tag = element.className.substringAfterLast(".")
        val location = "${element.className}.${element.methodName}:${element.lineNumber}"
        val logInfo = LogInfo(tag, location)
        
        // 缓存结果
        logInfoCache[cacheKey] = logInfo
        
        return logInfo
    }

    /**
     * 日志信息数据类
     */
    private data class LogInfo(val tag: String, val location: String)
    
    /**
     * 日志条目数据类
     */
    private data class LogEntry(val level: String, val tag: String, val location: String, val message: String)
    
    /**
     * 从对象池获取LogInfo
     */
    private fun obtainLogInfo(tag: String, location: String): LogInfo {
        val logInfo = logInfoPool.poll() ?: LogInfo(tag, location)
        return logInfo.copy(tag = tag, location = location)
    }
    
    /**
     * 回收LogInfo到对象池
     */
    private fun recycleLogInfo(logInfo: LogInfo) {
        logInfoPool.offer(logInfo)
    }
    
    /**
     * 从对象池获取StringBuilder
     */
    private fun obtainStringBuilder(): StringBuilder {
        val sb = stringBuilderPool.poll() ?: StringBuilder(128)
        sb.setLength(0)
        return sb
    }
    
    /**
     * 回收StringBuilder到对象池
     */
    private fun recycleStringBuilder(sb: StringBuilder) {
        if (sb.capacity() <= 1024) { // 限制对象池大小
            sb.setLength(0)
            stringBuilderPool.offer(sb)
        }
    }
    
    /**
     * 批量写入日志
     */
    private fun batchWriteLogs() {
        if (batchWriteScheduled) return
        
        batchWriteScheduled = true
        logExecutor.execute {
            try {
                Thread.sleep(LOG_BATCH_DELAY.toLong())
                
                val logEntries = mutableListOf<LogEntry>()
                logCacheQueue.drainTo(logEntries, LOG_BATCH_SIZE)
                
                if (logEntries.isNotEmpty() && fileWriter != null) {
                    checkLogFileSize()
                    
                    val sb = obtainStringBuilder()
                    try {
                        for (entry in logEntries) {
                            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                            sb.append("[${timestamp}] [${entry.level}] [${entry.tag}] ${entry.location} ${entry.message}\n")
                        }
                        fileWriter?.write(sb.toString())
                        fileWriter?.flush()
                    } finally {
                        recycleStringBuilder(sb)
                    }
                }
            } catch (e: Exception) {
                Log.e(DEFAULT_TAG, "Failed to batch write logs", e)
            } finally {
                batchWriteScheduled = false
            }
        }
    }
}

