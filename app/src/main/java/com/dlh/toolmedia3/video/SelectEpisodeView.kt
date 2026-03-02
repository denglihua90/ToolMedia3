package com.dlh.toolmedia3.video

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlh.toolmedia3.R
import com.lxj.xpopup.core.DrawerPopupView
import kotlin.math.min

class SelectEpisodeView : DrawerPopupView {
    // 选集数据
    private val episodes = 1..500 // 示例：20集
    
    // 当前选中的集数
    var currentEpisode: Int = 1

    // 选集选择回调
    var onEpisodeSelectedCallback: ((Int) -> Unit)? = null

    private var mHeight: Int = 0

    constructor(context: Context, vararg height: Int) : super(context) {
        mHeight = height.firstOrNull() ?: 0
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_select_episode_view
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化RecyclerView
        val recyclerView =
            findViewById<RecyclerView>(R.id.recycler_view_episode)
        if (recyclerView != null) {
            // 设置网格布局管理器，4列
            val layoutManager = GridLayoutManager(context, 3)
            recyclerView.layoutManager = layoutManager

            // 准备选集数据
            val episodeItems = mutableListOf<EpisodeItem>()
            episodes.forEach {
                episodeItems.add(EpisodeItem(it, it == currentEpisode))
            }

            // 使用BaseRecyclerViewAdapterHelper
            val adapter = EpisodeAdapter(episodeItems)
            adapter.onEpisodeSelected = { selectedEpisode ->
                currentEpisode = selectedEpisode
                onEpisodeSelectedCallback?.invoke(selectedEpisode)
                dismiss()
            }
            recyclerView.adapter = adapter
        }
    }

    override fun addInnerContent() {
        val contentView = LayoutInflater.from(context)
            .inflate(implLayoutId, drawerContentContainer, false)
        drawerContentContainer.addView(contentView)
        val params = contentView.layoutParams
        if (popupInfo != null) {
            if (mHeight == 0) {
                params.height = LayoutParams.MATCH_PARENT
            } else {
                params.height = mHeight
            }

            if (popupWidth > 0) params.width = popupWidth
            if (maxWidth > 0) params.width = min(params.width, maxWidth)
            contentView.setLayoutParams(params)
        }
    }

    override fun doMeasure() {
        val contentView = drawerContentContainer.getChildAt(0) ?: return
        val params = contentView.layoutParams
        if (popupInfo != null) {
            if (mHeight == 0) {
                params.height = LayoutParams.MATCH_PARENT
            } else {
                params.height = mHeight
            }
            if (popupWidth > 0) params.width = popupWidth
            if (maxWidth > 0) params.width = min(params.width, getMaxWidth())
            contentView.setLayoutParams(params)
        }
    }



}
