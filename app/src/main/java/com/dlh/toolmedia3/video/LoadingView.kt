package com.dlh.toolmedia3.video

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.media3.common.Player
import com.dlh.toolmedia3.R

/**
 * 加载状态视图
 * 用于显示缓冲状态和错误状态
 */
class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val loadingContainer: View
    private val errorContainer: View
    private val progressBar: ProgressBar
    private val loadingText: TextView
    private val errorText: TextView
    private val retryButton: TextView
    private val backButton: View

    // 回调接口
    var onRetryListener: (() -> Unit)? = null
    var onBackListener: (() -> Unit)? = null

    init {
        // 加载布局
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_loading_view, this, true)
        
        // 初始化视图
        loadingContainer = rootView.findViewById(R.id.loading_container)
        errorContainer = rootView.findViewById(R.id.error_container)
        progressBar = rootView.findViewById(R.id.progress_bar)
        loadingText = rootView.findViewById(R.id.loading_text)
        errorText = rootView.findViewById(R.id.error_text)
        retryButton = rootView.findViewById(R.id.retry_button)
        backButton = rootView.findViewById(R.id.back_button)
        
        // 设置点击事件
        retryButton.setOnClickListener {
            onRetryListener?.invoke()
        }
        
        backButton.setOnClickListener {
            onBackListener?.invoke()
        }
        
        // 默认隐藏
        visibility = View.GONE
    }

    /**
     * 显示缓冲状态
     * @param bufferPercentage 缓冲百分比
     */
    fun showBuffering(bufferPercentage: Int) {
        visibility = View.VISIBLE
        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        
        progressBar.progress = bufferPercentage
        loadingText.text = "$bufferPercentage%"
    }

    /**
     * 显示错误状态
     * @param errorMessage 错误信息
     * @param showBackButton 是否显示返回按钮（横屏时）
     */
    fun showError(errorMessage: String, showBackButton: Boolean = false) {
        visibility = View.VISIBLE
        loadingContainer.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
        
        errorText.text = errorMessage
        backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
    }

    /**
     * 隐藏加载视图
     */
    fun hide() {
        visibility = View.GONE
    }

    /**
     * 根据播放器状态显示相应的视图
     * @param playState 播放器状态
     * @param bufferPercentage 缓冲百分比
     * @param errorMessage 错误信息
     * @param isLandscape 是否为横屏
     */
    fun updateState(playState: Int, bufferPercentage: Int = 0, errorMessage: String? = null, isLandscape: Boolean = false) {
        // 优先处理错误状态
        if (errorMessage != null) {
            showError(errorMessage, isLandscape)
        } else {
            when (playState) {
                Player.STATE_BUFFERING -> {
                    showBuffering(bufferPercentage)
                }
                else -> {
                    hide()
                }
            }
        }
    }
}
