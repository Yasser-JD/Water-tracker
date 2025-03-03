package com.jdcoding.watertracker.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.ui.main.dashboard.DashboardFragment
import com.jdcoding.watertracker.ui.main.goals.GoalsFragment
import com.jdcoding.watertracker.ui.main.profile.ProfileFragment
import com.jdcoding.watertracker.ui.main.tips.TipsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        setupBottomNavigation()
        
        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_goals -> {
                    loadFragment(GoalsFragment())
                    true
                }
                R.id.navigation_tips -> {
                    loadFragment(TipsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
