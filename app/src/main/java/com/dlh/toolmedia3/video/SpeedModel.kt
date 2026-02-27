package com.dlh.toolmedia3.video

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dlh.toolmedia3.R

open class SpeedModel : EpoxyModelWithHolder<SpeedModel.Holder>() {
    
    @EpoxyAttribute
    open var speed: Float = 0f
    
    @EpoxyAttribute
    open var isSelected: Boolean = false
    
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    open var onSpeedSelected: ((Float) -> Unit)? = null
    
    override fun getDefaultLayout() = R.layout.item_speed
    
    override fun createNewHolder(parent: android.view.ViewParent): Holder {
        return Holder()
    }
    
    override fun bind(holder: Holder) {
        holder.speedText.text = "${speed}x"
        
        if (isSelected) {
            holder.speedText.setTextColor(holder.itemView.context.resources.getColor(android.R.color.white))
            holder.speedText.setBackgroundResource(R.drawable.bg_speed_selected)
        } else {
            holder.speedText.setTextColor(holder.itemView.context.resources.getColor(android.R.color.darker_gray))
            holder.speedText.setBackgroundResource(0)
        }
        
        holder.itemView.setOnClickListener {
            onSpeedSelected?.invoke(speed)
        }
    }
    
    class Holder : com.airbnb.epoxy.EpoxyHolder() {
        lateinit var speedText: TextView
        lateinit var itemView: View
        
        override fun bindView(itemView: View) {
            this.itemView = itemView
            speedText = itemView.findViewById(R.id.tv_speed)
        }
    }
}