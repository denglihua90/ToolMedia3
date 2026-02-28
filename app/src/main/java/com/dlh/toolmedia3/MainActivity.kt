package com.dlh.toolmedia3

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlh.toolmedia3.databinding.ActivityMainBinding
import com.dlh.toolmedia3.video.BaseVideoView


@androidx.media3.common.util.UnstableApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var videoView: BaseVideoView
    
    // SharedPreferences for saving settings
    private val sharedPreferences by lazy {
        getSharedPreferences("ToolMedia3Settings", MODE_PRIVATE)
    }
    
    // Key for auto save progress setting
    private val KEY_AUTO_SAVE_PROGRESS = "auto_save_progress"
    
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // 初始化视频播放器
        initVideoPlayer()
        
        // 为播放按钮添加点击监听器
        binding.btnVideo.setOnClickListener {
            // 从输入框获取视频URL
            val videoUrl = binding.textInputVideoUrl.text.toString().trim()
            
            // 非空检查
            if (videoUrl.isEmpty()) {
                // 显示提示
                Toast.makeText(this, getString(R.string.hint_video_url), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 使用获取的URL播放视频
            val videoTitle = getString(R.string.user_input_video)
            videoView.setVideoSource(videoUrl, videoTitle)
            videoView.play()
        }
        
        // 从SharedPreferences加载自动保存进度的状态
        val isAutoSaveEnabled = sharedPreferences.getBoolean(KEY_AUTO_SAVE_PROGRESS, true)
        binding.switchAutoSaveProgress.isChecked = isAutoSaveEnabled
        videoView.setAutoSaveProgressEnabled(false)
        
        // 为自动保存进度开关添加状态变化监听器
        binding.switchAutoSaveProgress.setOnCheckedChangeListener { _, isChecked ->
            videoView.setAutoSaveProgressEnabled(isChecked)
            // 保存状态到SharedPreferences
            sharedPreferences.edit().putBoolean(KEY_AUTO_SAVE_PROGRESS, false).apply()
        }

        println("Kotlin version: ${KotlinVersion.CURRENT}")
    }
    
    private fun initVideoPlayer() {
        // 获取BaseVideoView实例
        videoView = binding.videoView
        
        // 初始化播放器
        videoView.initPlayer(this)
        // 设置示例视频源（使用ExoPlayer官方示例视频）
        val videoUrl = "https://cdn.yzzy31-play.com/20260221/14914_d41b5422/index.m3u8"
        val videoTitle = "霹雳神鹰"
        videoView.setCutoutAdapted(false)
        // 预加载视频
        videoView.preloadVideo(videoUrl, videoTitle)
        
        // 设置视频源并开始播放
        videoView.setVideoSource(videoUrl, videoTitle)
    }
    
    override fun onResume() {
        super.onResume()
        // 播放器恢复
        videoView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        // 播放器暂停
        videoView.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 播放器销毁
        videoView.onDestroy()
    }
    
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        // 处理窗口焦点变化，在全屏状态下保持系统UI隐藏
        if (::videoView.isInitialized) {
            try {
                videoView.screenController.onWindowFocusChanged(hasWindowFocus)
            } catch (e: UninitializedPropertyAccessException) {
                // screenController尚未初始化，忽略
            }
        }
    }
    

}