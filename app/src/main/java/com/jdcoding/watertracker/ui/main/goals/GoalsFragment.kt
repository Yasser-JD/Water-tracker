package com.jdcoding.watertracker.ui.main.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jdcoding.watertracker.R

/**
 * Goals Fragment - Allows users to set and manage their hydration goals
 */
class GoalsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views and setup any listeners
        setupViews(view)
    }

    private fun setupViews(view: View) {
        // TODO: Implement goals functionality
    }
}
