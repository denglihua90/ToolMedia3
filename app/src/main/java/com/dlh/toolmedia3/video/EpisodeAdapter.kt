package com.dlh.toolmedia3.video

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dlh.toolmedia3.R

/**
 * 选集项数据模型
 */
data class EpisodeItem(
    val episode: Int,          // 集数
    var isSelected: Boolean    // 是否选中
)

/**
 * 选集适配器
 */
class EpisodeAdapter(data: MutableList<EpisodeItem>) : 
    BaseQuickAdapter<EpisodeItem, BaseViewHolder>(R.layout.item_episode, data) {

    // 选集选择回调
    var onEpisodeSelected: ((Int) -> Unit)? = null

    override fun convert(holder: BaseViewHolder, item: EpisodeItem) {
        // 设置集数文本
        holder.setText(R.id.tv_episode, "第${item.episode}集")

        // 设置选中状态
        if (item.isSelected) {
            holder.setBackgroundResource(R.id.tv_episode, R.drawable.rv_item_checked_bg)
            holder.setTextColor(R.id.tv_episode, context.resources.getColor(R.color.white))
        } else {
            holder.setBackgroundResource(R.id.tv_episode, R.drawable.rv_item_bg)
            holder.setTextColor(R.id.tv_episode, context.resources.getColor(R.color.white))
        }

        // 设置点击事件
        holder.itemView.setOnClickListener {
            // 处理选中状态
            data.forEach { episodeItem ->
                episodeItem.isSelected = episodeItem.episode == item.episode
            }
            notifyDataSetChanged()
            
            // 回调选中的集数
            onEpisodeSelected?.invoke(item.episode)
        }
    }
}
