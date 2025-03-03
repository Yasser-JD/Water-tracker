package com.jdcoding.watertracker.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jdcoding.watertracker.R

class AuthActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var authPagerAdapter: AuthPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewPager = findViewById(R.id.auth_viewpager)
        tabLayout = findViewById(R.id.auth_tabs)

        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        authPagerAdapter = AuthPagerAdapter(this)
        viewPager.adapter = authPagerAdapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.auth_login)
                1 -> getString(R.string.auth_register)
                else -> null
            }
        }.attach()
    }
}
