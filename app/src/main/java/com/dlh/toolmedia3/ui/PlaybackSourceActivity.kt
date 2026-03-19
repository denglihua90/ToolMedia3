package com.dlh.toolmedia3.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.architecture.event.SourceEvent
import com.dlh.toolmedia3.architecture.viewmodel.SourceViewModel
import com.dlh.toolmedia3.data.model.CategoryItem
import com.dlh.toolmedia3.databinding.ActivityPlaybackSourceBinding
import com.dlh.toolmedia3.ui.adapter.CategoryAdapter
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.drake.statelayout.StateLayout

class PlaybackSourceActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityPlaybackSourceBinding.inflate(layoutInflater)
    }
    // 初始化 SourceViewModel
    private val sourceViewModel by lazy {
        SourceViewModel(this)
    }
    
    // StateLayout 实例
    private lateinit var stateLayout: StateLayout
    
    // RecyclerView 适配器
    private lateinit var categoryAdapter: CategoryAdapter

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
        
        // 初始化 StateLayout
        stateLayout = binding.stateLayout
        
        // 初始化 RecyclerView
        binding.recyclerCategories.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter()
        binding.recyclerCategories.adapter = categoryAdapter
        
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
        lifecycleScope.launch {
            sourceViewModel.state.collect {
                // 处理加载状态
                if (it.isLoading || it.isTesting) {
                    stateLayout.showLoading()
                } else if (it.error != null) {
                    // 处理错误状态
                    stateLayout.showError()
                } else {
                    // 处理其他状态，显示内容
                    stateLayout.showContent()
                    
                    // 更新结果
                    when {
                        it.categories.isNotEmpty() -> {
                            // 直接使用状态中的 categories 字段
                            val categories = it.categories
                            // 更新适配器数据，使用 DiffUtil 优化
                            categoryAdapter.updateData(categories)
                        }
                        it.isSaved -> {
                            // 保存成功，清空列表
                            categoryAdapter.updateData(emptyList())
                            Toast.makeText(this@PlaybackSourceActivity, getString(R.string.source_saved), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // 清空列表
                            categoryAdapter.updateData(emptyList())
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 观察事件
     */
    private fun observeEvents() {
        lifecycleScope.launch {
            sourceViewModel.events.collect {
                when (it) {
                    is SourceEvent.SaveError -> {
                        Toast.makeText(this@PlaybackSourceActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    is SourceEvent.TestError -> {
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