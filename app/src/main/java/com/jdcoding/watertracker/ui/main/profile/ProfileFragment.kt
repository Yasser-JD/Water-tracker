package com.jdcoding.watertracker.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.model.User

/**
 * Profile Fragment - Shows user profile and settings
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views and setup any listeners
        setupViews(view)


    }

    private fun setupViews(view: View) {
        // TODO: Implement profile functionality
    }
}
