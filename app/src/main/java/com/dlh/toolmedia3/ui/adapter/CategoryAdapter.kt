package com.dlh.toolmedia3.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dlh.toolmedia3.R
import com.dlh.toolmedia3.data.model.CategoryItem

/**
 * 分类适配器
 */
class CategoryAdapter : BaseQuickAdapter<CategoryItem, BaseViewHolder>(R.layout.item_category) {

    override fun convert(holder: BaseViewHolder, item: CategoryItem) {
        // 设置分类ID
        holder.setText(R.id.tv_type_id, "ID: ${item.typeId}")
        // 设置分类名称
        holder.setText(R.id.tv_type_name, item.typeName)
    }

    /**
     * 更新数据，使用 DiffUtil 优化
     */
    fun updateData(newData: List<CategoryItem>) {
        setList(newData.toMutableList())
    }
}
