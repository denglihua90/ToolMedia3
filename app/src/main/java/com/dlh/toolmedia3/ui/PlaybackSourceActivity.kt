package com.dlh.toolmedia3.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.architecture.viewmodel.SourceViewModel
import com.dlh.toolmedia3.databinding.ActivityPlaybackSourceBinding
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PlaybackSourceActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityPlaybackSourceBinding.inflate(layoutInflater)
    }
    // 初始化 SourceViewModel
    private  val sourceViewModel by lazy {
        SourceViewModel(this)
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(binding.toolbarSource)
        binding.toolbarSource.setNavigationIcon(R.drawable.baseline_back_arrow_24)
        binding.toolbarSource.setNavigationOnClickListener {
            finish()
        }
        binding.toolbarSource.title="设置播放源"
        
        // 实现沉浸式标题栏
        immersionBar {
            statusBarColor(R.color.dodgerblue)
            navigationBarColor(R.color.dodgerblue)
            statusBarDarkFont(false)
            navigationBarDarkIcon(false)
            fitsSystemWindows(true)
            autoStatusBarDarkModeEnable(false)
            autoNavigationBarDarkModeEnable(false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left,0, systemBars.right, systemBars.bottom)
            insets
        }


        
        // 观察状态
        observeState()
        
        // 观察事件
        observeEvents()

        // 测试按钮点击事件
        binding.btnSubmit.setOnClickListener {
            testSourceUrl()
        }

        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            saveSource()
        }

    }
    
    /**
     * 观察状态
     */
    private fun observeState() {
        CoroutineScope(Dispatchers.Main).launch {
            sourceViewModel.state.collect {
                // 处理加载状态
                if (it.isLoading) {
                    binding.textResult.text = getString(R.string.loading)
                }
                
                // 处理测试
                 if (it.isTesting) {  //状态
                    binding.textResult.text = getString(R.string.loading)
                }
                
                // 处理错误状态
                if (it.error != null) {
                    binding.textResult.text = it.error
                }
                
                // 处理测试结果
                if (it.testResult != null && !it.isTesting) {
                    binding.textResult.text = it.testResult
                }
                
                // 处理保存成功状态
                if (it.isSaved) {
                    binding.textResult.text = getString(R.string.source_saved)
                }
            }
        }
    }
    
    /**
     * 观察事件
     */
    private fun observeEvents() {
        CoroutineScope(Dispatchers.Main).launch {
            sourceViewModel.events.collect {
                when (it) {
                    is com.dlh.toolmedia3.architecture.event.SourceEvent.SaveSuccess -> {
                        Toast.makeText(this@PlaybackSourceActivity, getString(R.string.source_saved), Toast.LENGTH_SHORT).show()
                    }
                    is com.dlh.toolmedia3.architecture.event.SourceEvent.SaveError -> {
                        Toast.makeText(this@PlaybackSourceActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    is com.dlh.toolmedia3.architecture.event.SourceEvent.TestSuccess -> {
                        // 测试成功，结果已在状态中处理
                    }
                    is com.dlh.toolmedia3.architecture.event.SourceEvent.TestError -> {
                        Toast.makeText(this@PlaybackSourceActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 处理其他事件
                    }
                }
            }
        }
    }

    /**
     * 测试源地址
     */
    private fun testSourceUrl() {
        val sourceUrl = binding.tvSourceUrl.text.toString().trim()
        if (sourceUrl.isEmpty()) {
            Toast.makeText(this, getString(R.string.hint_source_url), Toast.LENGTH_SHORT).show()
            return
        }

        // 使用 MVI 架构测试源地址
        sourceViewModel.testSource(sourceUrl)
    }

    /**
     * 保存源
     */
    private fun saveSource() {
        val sourceName = binding.tvSourceName.text.toString().trim()
        val sourceUrl = binding.tvSourceUrl.text.toString().trim()

        if (sourceName.isEmpty() || sourceUrl.isEmpty()) {
            Toast.makeText(this, getString(R.string.hint_source_input), Toast.LENGTH_SHORT).show()
            return
        }

        // 使用 MVI 架构保存播放源
        sourceViewModel.saveSource(sourceName, sourceUrl)
    }
}