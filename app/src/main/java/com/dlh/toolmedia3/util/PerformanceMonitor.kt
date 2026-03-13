package com.dlh.toolmedia3.util

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.Player
import com.dlh.toolmedia3.architecture.processor.PlayerProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong

class PerformanceMonitor(private val player: Player) {
    
    companion object {
        private const val TAG = "PerformanceMonitor"
        private const val MONITOR_INTERVAL = 1000L // 1秒
        private const val FRAME_RATE_SAMPLE_SIZE = 30 // 采样帧数
    }
    
    /**
     * 在主线程上执行操作并返回结果
     */
    private fun <T> runOnMainThread(block: () -> T): T {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            return block()
        }
        
        val latch = CountDownLatch(1)
        var result: T? = null
        
        Handler(Looper.getMainLooper()).post {
            try {
                result = block()
            } finally {
                latch.countDown()
            }
        }
        
        try {
            latch.await()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
    
    private val handler = Handler(Looper.getMainLooper())
    private val frameCount = AtomicLong(0)
    private val lastFrameTime = AtomicLong(0)
    private val frameTimes = mutableListOf<Long>()
    
    private var isMonitoring = false
    
    private lateinit var monitoringRunnable: Runnable
    
    /**
     * 开始性能监控
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        // 初始化监控Runnable
        monitoringRunnable = Runnable {
            if (!isMonitoring) return@Runnable
            
            CoroutineScope(Dispatchers.Default).launch {
                val fps = calculateFPS()
                val bufferHealth = calculateBufferHealth()
                val cpuUsage = calculateCpuUsage()
                
                Log.d(TAG, "Performance metrics - FPS: ${String.format("%.2f", fps)}, Buffer: ${String.format("%.2f", bufferHealth)}s, CPU: ${String.format("%.2f", cpuUsage)}%")
            }
            
            handler.postDelayed(monitoringRunnable, MONITOR_INTERVAL)
        }
        
        isMonitoring = true
        frameCount.set(0)
        lastFrameTime.set(System.currentTimeMillis())
        frameTimes.clear()
        
        handler.postDelayed(monitoringRunnable, MONITOR_INTERVAL)
        
        Log.d(TAG, "Performance monitoring started")
    }
    
    /**
     * 停止性能监控
     */
    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(monitoringRunnable)
        
        Log.d(TAG, "Performance monitoring stopped")
    }
    
    /**
     * 记录帧绘制
     */
    fun onFrameRendered() {
        if (!isMonitoring) return
        
        val currentTime = System.currentTimeMillis()
        frameCount.incrementAndGet()
        
        if (lastFrameTime.get() > 0) {
            val frameTime = currentTime - lastFrameTime.get()
            frameTimes.add(frameTime)
            
            // 保持采样大小
            if (frameTimes.size > FRAME_RATE_SAMPLE_SIZE) {
                frameTimes.removeAt(0)
            }
        }
        
        lastFrameTime.set(currentTime)
    }
    
    /**
     * 计算帧率
     */
    private fun calculateFPS(): Double {
        if (frameTimes.isEmpty()) return 0.0
        
        val totalFrameTime = frameTimes.sum().toDouble()
        val avgFrameTime = totalFrameTime / frameTimes.size
        return 1000.0 / avgFrameTime
    }
    
    /**
     * 计算缓冲健康度（秒）
     */
    private fun calculateBufferHealth(): Double {
        return runOnMainThread {
            if (player.isPlaying) {
                val bufferedPosition = player.bufferedPosition
                val currentPosition = player.currentPosition
                (bufferedPosition - currentPosition) / 1000.0
            } else {
                0.0
            }
        }
    }
    
    /**
     * 计算CPU使用率
     */
    private fun calculateCpuUsage(): Double {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return 0.0 // 旧版本不支持
        }
        
        try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("top -n 1")
            val inputStream = process.inputStream
            val bytes = inputStream.readBytes()
            val output = String(bytes)
            
            // 解析top命令输出，提取CPU使用率
            // 这里只是简单实现，实际项目中可能需要更复杂的解析
            val lines = output.lines()
            for (line in lines) {
                if (line.contains("CPU:")) {
                    val parts = line.split(" ").filter { it.isNotEmpty() }
                    if (parts.size > 1) {
                        val cpuUsageStr = parts[1].replace("%", "")
                        return cpuUsageStr.toDoubleOrNull() ?: 0.0
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating CPU usage", e)
        }
        
        return 0.0
    }
    
    /**
     * 获取性能报告
     */
    fun getPerformanceReport(): PerformanceReport {
        return runOnMainThread {
            PerformanceReport(
                fps = calculateFPS(),
                bufferHealth = calculateBufferHealth(),
                cpuUsage = calculateCpuUsage(),
                isPlaying = player.isPlaying,
                playbackState = player.playbackState
            )
        }
    }
    
    data class PerformanceReport(
        val fps: Double,
        val bufferHealth: Double,
        val cpuUsage: Double,
        val isPlaying: Boolean,
        val playbackState: Int
    ) {
        override fun toString(): String {
            return "PerformanceReport(fps=%.2f, bufferHealth=%.2fs, cpuUsage=%.2f%%, isPlaying=$isPlaying, playbackState=$playbackState)".format(
                fps, bufferHealth, cpuUsage
            )
        }
    }
}