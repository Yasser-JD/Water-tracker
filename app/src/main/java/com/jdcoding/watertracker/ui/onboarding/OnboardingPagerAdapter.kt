package com.jdcoding.watertracker.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdcoding.watertracker.R

/**
 * Adapter for the ViewPager used in onboarding screens
 */
class OnboardingPagerAdapter : ListAdapter<OnboardingPage, OnboardingPagerAdapter.OnboardingViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_page, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.onboarding_image)
        private val titleTextView: TextView = itemView.findViewById(R.id.onboarding_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.onboarding_description)

        fun bind(onboardingPage: OnboardingPage) {
            imageView.setImageResource(onboardingPage.imageResId)
            titleTextView.text = onboardingPage.title
            descriptionTextView.text = onboardingPage.description
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OnboardingPage>() {
            override fun areItemsTheSame(oldItem: OnboardingPage, newItem: OnboardingPage): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: OnboardingPage, newItem: OnboardingPage): Boolean {
                return oldItem == newItem
            }
        }
    }
}
