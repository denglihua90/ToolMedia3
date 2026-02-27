package com.dlh.toolmedia3.video

import android.content.Context
import com.dlh.toolmedia3.R
import com.lxj.xpopup.core.DrawerPopupView


class SpeedListView : DrawerPopupView {
    // 倍速选项列表
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
//    val currentSpeed = viewModel.state.value.playbackSpeed

    constructor(context: Context) : super(context){

    }

    override fun getImplLayoutId(): Int {
        return   R.layout.layout_speed_selection
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化RecyclerView
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_speed)
        if (recyclerView != null) {
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            // 使用Epoxy
            val controller = object : com.airbnb.epoxy.EpoxyController() {
                override fun buildModels() {
//                    speeds.forEach { speed ->
//                        val model = object : com.airbnb.epoxy.EpoxyModel<android.view.View>() {
//                            override fun getDefaultLayout() = R.layout.item_speed
//
//                            var speedValue: Float = speed
//                            var isSelectedValue: Boolean = speed == currentSpeed
//                            var onSpeedSelectedCallback: ((Float) -> Unit)? = null
//
//                            override fun bind(view: android.view.View) {
//                                val speedText = view.findViewById<android.widget.TextView>(R.id.tv_speed)
//                                speedText.text = "${speedValue}x"
//
//                                if (isSelectedValue) {
//                                    speedText.setTextColor(view.context.resources.getColor(android.R.color.white))
//                                    speedText.setBackgroundResource(R.drawable.bg_speed_selected)
//                                } else {
//                                    speedText.setTextColor(view.context.resources.getColor(android.R.color.darker_gray))
//                                    speedText.setBackgroundResource(0)
//                                }
//
//                                view.setOnClickListener {
//                                    onSpeedSelectedCallback?.invoke(speedValue)
//                                }
//                            }
//                        }
//                        model.onSpeedSelectedCallback = { selectedSpeed ->
////                            viewModel.setPlaybackSpeed(selectedSpeed)
//                            dismiss()
//                        }
//                        model.id(speed)
//                        add(model)
//                    }
                }
            }
            recyclerView.adapter = controller.adapter
        }
    }
}