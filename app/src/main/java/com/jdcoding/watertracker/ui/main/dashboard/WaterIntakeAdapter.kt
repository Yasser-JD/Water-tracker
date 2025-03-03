package com.jdcoding.watertracker.ui.main.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.model.WaterIntake
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying water intake items in a RecyclerView
 */
class WaterIntakeAdapter(private val onDeleteClicked: (WaterIntake) -> Unit) : 
    ListAdapter<WaterIntake, WaterIntakeAdapter.WaterIntakeViewHolder>(WaterIntakeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterIntakeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_intake, parent, false)
        return WaterIntakeViewHolder(view, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: WaterIntakeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WaterIntakeViewHolder(
        itemView: View,
        private val onDeleteClicked: (WaterIntake) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val intakeIcon: ImageView = itemView.findViewById(R.id.intake_icon)
        private val intakeAmount: TextView = itemView.findViewById(R.id.intake_amount)
        private val intakeTime: TextView = itemView.findViewById(R.id.intake_time)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        
        fun bind(waterIntake: WaterIntake) {
            intakeIcon.setImageResource(R.drawable.ic_water_glass)
            
            // Format amount
            intakeAmount.text = "${waterIntake.amount} ml"
            
            // Format time
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            intakeTime.text = sdf.format(waterIntake.timestamp)
            
            // Set delete listener
            deleteButton.setOnClickListener {
                onDeleteClicked(waterIntake)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    private class WaterIntakeDiffCallback : DiffUtil.ItemCallback<WaterIntake>() {
        override fun areItemsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem == newItem
        }
    }
}
