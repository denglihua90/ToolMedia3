package com.dlh.toolmedia3.video

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dlh.toolmedia3.R

/**
 * 倍速选项数据类
 */
data class SpeedItem(
    val speed: Float,
    val isSelected: Boolean
)

/**
 * 倍速选择适配器
 */
class SpeedAdapter(data: MutableList<SpeedItem>) : BaseQuickAdapter<SpeedItem, BaseViewHolder>(R.layout.item_speed, data) {
    
    // 倍速选择回调
    var onSpeedSelected: ((Float) -> Unit)? = null
    
    override fun convert(holder: BaseViewHolder, item: SpeedItem) {
        // 设置倍速文本
        holder.setText(R.id.tv_speed, "${item.speed}x")
        
        // 设置选中状态
        if (item.isSelected) {
            holder.setTextColor(R.id.tv_speed, holder.itemView.context.resources.getColor(R.color.white))
            holder.setBackgroundResource(R.id.tv_speed, R.drawable.bg_speed_selected)
        } else {
            holder.setTextColor(R.id.tv_speed, holder.itemView.context.resources.getColor(R.color.darkgray))
            holder.setBackgroundResource(R.id.tv_speed, 0)
        }
        
        // 设置点击事件
        holder.itemView.setOnClickListener {
            onSpeedSelected?.invoke(item.speed)
        }
    }
}