package com.jdcoding.watertracker.ui.main.tips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.model.HydrationTip

/**
 * Adapter for displaying hydration tips in a RecyclerView
 */
class TipsAdapter(private val onTipClicked: (HydrationTip) -> Unit) : 
    ListAdapter<HydrationTip, TipsAdapter.TipViewHolder>(TipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = getItem(position)
        holder.bind(tip)
    }

    inner class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tipTitle: TextView = itemView.findViewById(R.id.tip_title)
        private val tipContent: TextView = itemView.findViewById(R.id.tip_content)
        private val tipImage: ImageView? = itemView.findViewById(R.id.tip_image)
        private val tipCategory: TextView = itemView.findViewById(R.id.tip_category)

        fun bind(tip: HydrationTip) {
            tipTitle.text = tip.title
            tipContent.text = tip.content
            
            // Set category text
            tipCategory.text = when (tip.category) {
                com.jdcoding.watertracker.model.TipCategory.GENERAL -> "General"
                com.jdcoding.watertracker.model.TipCategory.HEALTH_BENEFITS -> "Health Benefits"
                com.jdcoding.watertracker.model.TipCategory.DAILY_HABITS -> "Daily Habits"
                com.jdcoding.watertracker.model.TipCategory.EXERCISE -> "Exercise"
                com.jdcoding.watertracker.model.TipCategory.NUTRITION -> "Nutrition"
            }
            
            // Set image if available
            tip.imageResource?.let { imageRes ->
                tipImage?.setImageResource(imageRes)
                tipImage?.visibility = View.VISIBLE
            } ?: run {
                tipImage?.visibility = View.GONE
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onTipClicked(tip)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    private class TipDiffCallback : DiffUtil.ItemCallback<HydrationTip>() {
        override fun areItemsTheSame(oldItem: HydrationTip, newItem: HydrationTip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HydrationTip, newItem: HydrationTip): Boolean {
            return oldItem == newItem
        }
    }
}
