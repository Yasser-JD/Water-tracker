package com.jdcoding.watertracker.ui.main.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.model.Goal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying goal history items in a RecyclerView
 */
class GoalAdapter(private val onGoalClicked: (Goal) -> Unit) :
    ListAdapter<Goal, GoalAdapter.GoalViewHolder>(GoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_history, parent, false)
        return GoalViewHolder(view, onGoalClicked)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoalViewHolder(
        itemView: View,
        private val onGoalClicked: (Goal) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val goalValue: TextView = itemView.findViewById(R.id.goal_value)
        private val goalDate: TextView = itemView.findViewById(R.id.goal_date)
        private val goalStatus: TextView = itemView.findViewById(R.id.goal_status)
        
        fun bind(goal: Goal) {
            // Format goal amount
            goalValue.text = "${goal.targetAmount} ml"
            
            // Format date range
            val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val startDate = goal.createdAt
            
            // If this is the current goal, show "Since" text
            if (goal.isActive) {
                goalDate.text = "Set on ${dateFormat.format(startDate)}"
                goalStatus.text = "Current"
                goalStatus.setBackgroundResource(R.drawable.bg_status_current)
            } else {
                // For previous goals, try to find the end date (or use "unknown")
                goalDate.text = "From ${dateFormat.format(startDate)}"
                goalStatus.text = "Previous"
                goalStatus.setBackgroundResource(R.drawable.bg_status_previous)
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onGoalClicked(goal)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    private class GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem == newItem
        }
    }
}
