package com.dlh.toolmedia3.video

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.dlh.toolmedia3.R
import com.lxj.xpopup.core.DrawerPopupView
import kotlin.math.min

class SelectEpisodeView : DrawerPopupView {
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