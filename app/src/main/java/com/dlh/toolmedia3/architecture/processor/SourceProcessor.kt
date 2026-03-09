package com.dlh.toolmedia3.architecture.processor

import android.content.Context
import com.dlh.toolmedia3.architecture.event.SourceEvent
import com.dlh.toolmedia3.architecture.intent.SourceIntent
import com.dlh.toolmedia3.architecture.state.SourceState
import com.dlh.toolmedia3.data.DatabaseManager
import com.dlh.toolmedia3.data.model.PlaybackSource
import com.dlh.toolmedia3.network.NetworkService
import com.dlh.toolmedia3.network.repository.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * 播放源处理器
 */
class SourceProcessor(private val context: Context) {
    
    // 网络请求仓库
    private val networkRepository by lazy {
        NetworkService.getRepository()
    }

    // 状态流
    private val _state = MutableStateFlow(SourceState())
    val state = _state.asStateFlow()
    
    // 事件流
    private val _events = MutableSharedFlow<SourceEvent>()
    val events = _events.asSharedFlow()
    
    /**
     * 处理 Intent
     */
    fun processIntent(intent: SourceIntent, scope: CoroutineScope) {
        when (intent) {
            is SourceIntent.SaveSource -> saveSource(intent.name, intent.url, scope)
            is SourceIntent.LoadSources -> loadSources(scope)
            is SourceIntent.DeleteSource -> deleteSource(intent.id, scope)
            is SourceIntent.ClearError -> clearError()
            is SourceIntent.TestSource -> testSource(intent.url, scope)
        }
    }
    
    /**
     * 保存播放源
     */
    private fun saveSource(name: String, url: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                // 更新状态为加载中
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = true, error = null, isSaved = false)
                }
                
                // 获取数据库实例
                val database = DatabaseManager.getInstance(context).database
                val sourceDao = database.playbackSourceDao()
                
                // 检查是否已存在同名播放源
                val existingSource = sourceDao.getSourceByName(name)
                
                if (existingSource != null) {
                    // 更新现有播放源
                    val updatedSource = existingSource.copy(
                        url = url,
                        updatedAt = System.currentTimeMillis()
                    )
                    sourceDao.updateSource(updatedSource)
                } else {
                    // 创建新播放源
                    val newSource = PlaybackSource(
                        name = name,
                        url = url
                    )
                    sourceDao.insertSource(newSource)
                }
                
                // 更新状态为保存成功
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false, isSaved = true)
                }
                
                // 发送保存成功事件
                _events.emit(SourceEvent.SaveSuccess)
            } catch (e: Exception) {
                // 更新状态为保存失败
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
                
                // 发送保存失败事件
                _events.emit(SourceEvent.SaveError(e.message ?: "保存失败"))
            }
        }
    }
    
    /**
     * 加载所有播放源
     */
    private fun loadSources(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                // 更新状态为加载中
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }
                
                // 获取数据库实例
                val database = DatabaseManager.getInstance(context).database
                val sourceDao = database.playbackSourceDao()
                
                // 加载所有播放源
                val sources = sourceDao.getAllSources().collect {
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            sources = it,
                            isLoading = false
                        )
                    }
                }
                
                // 发送加载成功事件
                _events.emit(SourceEvent.LoadSuccess)
            } catch (e: Exception) {
                // 更新状态为加载失败
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
                
                // 发送加载失败事件
                _events.emit(SourceEvent.LoadError(e.message ?: "加载失败"))
            }
        }
    }
    
    /**
     * 删除播放源
     */
    private fun deleteSource(id: Int, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                // 更新状态为加载中
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }
                
                // 获取数据库实例
                val database = DatabaseManager.getInstance(context).database
                val sourceDao = database.playbackSourceDao()
                
                // 删除播放源
                sourceDao.deleteSourceById(id)
                
                // 更新状态为删除成功
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false)
                }
                
                // 发送删除成功事件
                _events.emit(SourceEvent.DeleteSuccess)
            } catch (e: Exception) {
                // 更新状态为删除失败
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
                
                // 发送删除失败事件
                _events.emit(SourceEvent.DeleteError(e.message ?: "删除失败"))
            }
        }
    }
    
    /**
     * 清除错误状态
     */
    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    /**
     * 测试源地址
     */
    private fun testSource(url: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                // 更新状态为测试中
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isTesting = true, error = null, testResult = null)
                }

                // 执行网络请求
                val result = networkRepository.genericRequest(url)
//                NetworkService.refreshRetrofit()

                // 处理网络请求结果
                when (result) {
                    is com.dlh.toolmedia3.network.repository.NetworkResult.Success -> {
                        // 更新状态为测试成功
                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(isTesting = false, testResult = result.data.categories.toString())
                        }
                        
                        // 发送测试成功事件
                        _events.emit(SourceEvent.TestSuccess(result.data.toString()))
                    }
                    is com.dlh.toolmedia3.network.repository.NetworkResult.Error -> {
                        // 更新状态为测试失败
                        val errorMessage = result.message
                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(isTesting = false, error = errorMessage, testResult = errorMessage)
                        }
                        
                        // 发送测试失败事件
                        _events.emit(SourceEvent.TestError(errorMessage))
                    }
                }
            } catch (e: Exception) {
                // 更新状态为测试失败
                val errorMessage = e.message ?: "测试失败"
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isTesting = false, error = errorMessage, testResult = errorMessage)
                }
                
                // 发送测试失败事件
                _events.emit(SourceEvent.TestError(errorMessage))
            }
        }
    }
}
