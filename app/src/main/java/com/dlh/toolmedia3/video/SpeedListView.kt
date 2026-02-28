package com.dlh.toolmedia3.video

import android.content.Context
import android.view.LayoutInflater
import com.dlh.toolmedia3.R
import com.lxj.xpopup.core.DrawerPopupView
import kotlin.math.min


class SpeedListView : DrawerPopupView {
    // 倍速选项列表
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    // 当前选中的倍速
    var currentSpeed: Float = 1.0f

    private var mHeight: Int = 0

    // 倍速选择回调
    var onSpeedSelectedCallback: ((Float) -> Unit)? = null

    constructor(context: Context, height: Int = 0) : super(context) {
        mHeight = height
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_speed_selection
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化RecyclerView
        val recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_speed)
        if (recyclerView != null) {
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            // 准备倍速数据
            val speedItems = mutableListOf<SpeedItem>()
            speeds.forEach {
                speedItems.add(SpeedItem(it, it == currentSpeed))
            }

            // 使用BaseRecyclerViewAdapterHelper
            val adapter = SpeedAdapter(speedItems)
            adapter.onSpeedSelected = { selectedSpeed ->
                currentSpeed = selectedSpeed
                onSpeedSelectedCallback?.invoke(selectedSpeed)
                dismiss()
            }
            recyclerView.adapter = adapter
        }
    }

    override fun addInnerContent() {
        val contentView = LayoutInflater.from(context)
            .inflate(implLayoutId, drawerContentContainer, false)
        drawerContentContainer.addView(contentView)
        val params = contentView.getLayoutParams()
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