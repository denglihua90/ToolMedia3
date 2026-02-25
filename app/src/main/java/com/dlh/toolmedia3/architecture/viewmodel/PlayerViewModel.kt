package com.dlh.toolmedia3.architecture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlh.toolmedia3.architecture.event.PlayerEvent
import com.dlh.toolmedia3.architecture.intent.PlayerIntent
import com.dlh.toolmedia3.architecture.processor.PlayerProcessor
import com.dlh.toolmedia3.architecture.state.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@androidx.media3.common.util.UnstableApi

/**
 * 播放器ViewModel
 */
class PlayerViewModel(
    private val processor: PlayerProcessor
) : ViewModel() {
    
    // 播放器状态
    val state: StateFlow<PlayerState> = processor.state
    
    // 播放器事件
    val events: Flow<PlayerEvent> = processor.events
    
    /**
     * 发送意图
     */
    fun sendIntent(intent: PlayerIntent) {
        processor.processIntent(intent, viewModelScope)
    }
    
    /**
     * 播放视频
     */
    fun play(url: String? = null) {
        sendIntent(PlayerIntent.Play(url))
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        sendIntent(PlayerIntent.Pause)
    }
    
    /**
     * 停止播放
     */
    fun stop() {
        sendIntent(PlayerIntent.Stop)
    }
    
    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long) {
        sendIntent(PlayerIntent.SeekTo(position))
    }
    
    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        sendIntent(PlayerIntent.SetVolume(volume))
    }
    
    /**
     * 设置播放速度
     */
    fun setPlaybackSpeed(speed: Float) {
        sendIntent(PlayerIntent.SetPlaybackSpeed(speed))
    }
    
    /**
     * 切换清晰度
     */
    fun changeQuality(quality: String) {
        sendIntent(PlayerIntent.ChangeQuality(quality))
    }
    
    /**
     * 加载清晰度列表
     */
    fun loadQualities() {
        sendIntent(PlayerIntent.LoadQualities)
    }
    
    /**
     * 切换全屏
     */
    fun toggleFullScreen() {
        sendIntent(PlayerIntent.ToggleFullScreen)
    }
    
    /**
     * 进入画中画模式
     */
    fun enterPictureInPicture() {
        sendIntent(PlayerIntent.EnterPictureInPicture)
    }
    
    /**
     * 退出画中画模式
     */
    fun exitPictureInPicture() {
        sendIntent(PlayerIntent.ExitPictureInPicture)
    }
    
    /**
     * 设置屏幕模式
     */
    fun setScreenMode(mode: Int) {
        sendIntent(PlayerIntent.SetScreenMode(mode))
    }
    
    /**
     * 设置缩放类型
     */
    fun setScaleType(scaleType: Int) {
        sendIntent(PlayerIntent.SetScaleType(scaleType))
    }
    
    /**
     * 设置视频旋转角度
     */
    fun setVideoRotation(rotation: Int) {
        sendIntent(PlayerIntent.SetVideoRotation(rotation))
    }
    
    /**
     * 设置视频镜像
     */
    fun setVideoMirror(mirrored: Boolean) {
        sendIntent(PlayerIntent.SetVideoMirror(mirrored))
    }
    
    /**
     * 调节音量
     */
    fun adjustVolume(delta: Float) {
        sendIntent(PlayerIntent.AdjustVolume(delta))
    }
    
    /**
     * 调节亮度
     */
    fun adjustBrightness(delta: Float) {
        sendIntent(PlayerIntent.AdjustBrightness(delta))
    }
    
    /**
     * 调节进度
     */
    fun adjustProgress(delta: Long) {
        sendIntent(PlayerIntent.AdjustProgress(delta))
    }
    
    /**
     * 设置纯音频模式
     */
    fun setAudioOnlyMode(enabled: Boolean) {
        sendIntent(PlayerIntent.SetAudioOnlyMode(enabled))
    }
    
    /**
     * 保存播放进度
     */
    fun savePlaybackProgress() {
        sendIntent(PlayerIntent.SavePlaybackProgress)
    }
    
    /**
     * 加载播放进度
     */
    fun loadPlaybackProgress() {
        sendIntent(PlayerIntent.LoadPlaybackProgress)
    }
    
    /**
     * 预加载视频
     */
    fun preloadVideo(url: String) {
        sendIntent(PlayerIntent.PreloadVideo(url))
    }
    
    /**
     * 下载视频
     */
    fun downloadVideo(url: String, quality: String) {
        sendIntent(PlayerIntent.DownloadVideo(url, quality))
    }
    
    /**
     * 暂停下载
     */
    fun pauseDownload(downloadId: String) {
        sendIntent(PlayerIntent.PauseDownload(downloadId))
    }
    
    /**
     * 取消下载
     */
    fun cancelDownload(downloadId: String) {
        sendIntent(PlayerIntent.CancelDownload(downloadId))
    }
    
    /**
     * 获取下载状态
     */
    fun getDownloadStatus() {
        sendIntent(PlayerIntent.GetDownloadStatus)
    }
    
    /**
     * 获取已下载列表
     */
    fun getDownloadedList() {
        sendIntent(PlayerIntent.GetDownloadedList)
    }
    
    /**
     * 删除已下载视频
     */
    fun deleteDownloadedVideo(videoId: String) {
        sendIntent(PlayerIntent.DeleteDownloadedVideo(videoId))
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        sendIntent(PlayerIntent.ClearCache)
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize() {
        sendIntent(PlayerIntent.GetCacheSize)
    }
    
    /**
     * 设置最大缓存容量
     */
    fun setMaxCacheSize(size: Long) {
        sendIntent(PlayerIntent.SetMaxCacheSize(size))
    }
    
    /**
     * 截图
     */
    fun takeScreenshot() {
        sendIntent(PlayerIntent.TakeScreenshot)
    }
    
    /**
     * 生命周期恢复
     */
    fun onResume() {
        sendIntent(PlayerIntent.OnResume)
    }
    
    /**
     * 生命周期暂停
     */
    fun onPause() {
        sendIntent(PlayerIntent.OnPause)
    }
    
    /**
     * 生命周期销毁
     */
    fun onDestroy() {
        sendIntent(PlayerIntent.OnDestroy)
    }
    
    /**
     * 网络状态变化
     */
    fun networkStateChanged(networkType: Int) {
        sendIntent(PlayerIntent.NetworkStateChanged(networkType))
    }
    
    /**
     * 设置刘海屏适配
     */
    fun setCutoutAdapted(adapted: Boolean) {
        sendIntent(PlayerIntent.SetCutoutAdapted(adapted))
    }
    
    /**
     * 更新播放器实时状态
     */
    fun updatePlayerState(position: Long, bufferedPosition: Long, duration: Long, isPlaying: Boolean) {
        processor.updatePlayerState(position, bufferedPosition, duration, isPlaying)
    }
}
