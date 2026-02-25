package com.dlh.toolmedia3.video

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlh.toolmedia3.R

/**
 * 清晰度选择对话框
 */
class QualityDialog(
    context: Context,
    private val qualities: List<String>,
    private val currentQuality: String,
    private val onQualitySelected: (String) -> Unit
) : Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth) {
    
    init {
        setContentView(R.layout.dialog_quality)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        
        val recyclerView = findViewById<RecyclerView>(R.id.quality_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = QualityAdapter(qualities, currentQuality) {
            onQualitySelected(it)
            dismiss()
        }
    }
    
    /**
     * 清晰度适配器
     */
    private class QualityAdapter(
        private val qualities: List<String>,
        private val currentQuality: String,
        private val onQualitySelected: (String) -> Unit
    ) : RecyclerView.Adapter<QualityAdapter.ViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quality, parent, false)
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val quality = qualities[position]
            holder.qualityText.text = quality
            holder.isCurrent.visibility = if (quality == currentQuality) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener {
                onQualitySelected(quality)
            }
        }
        
        override fun getItemCount(): Int {
            return qualities.size
        }
        
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val qualityText: TextView = itemView.findViewById(R.id.quality_text)
            val isCurrent: TextView = itemView.findViewById(R.id.is_current)
        }
    }
}
