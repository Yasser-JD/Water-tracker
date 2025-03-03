package com.jdcoding.watertracker.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.ui.auth.AuthActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button
    private lateinit var indicatorsContainer: LinearLayout

    private val pagerAdapter = OnboardingPagerAdapter()
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateIndicators(position)
            updateButtonsVisibility(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewpager)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)
        indicatorsContainer = findViewById(R.id.indicator_container)

        setupViewPager()
        setupIndicators()
        setupButtons()

        // Register the page change callback
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onDestroy() {
        // Unregister the page change callback to avoid memory leaks
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    private fun setupViewPager() {
        val onboardingPages = listOf(
            OnboardingPage(
                imageResId = R.drawable.ic_water_drop,
                title = getString(R.string.onboarding_title_1),
                description = getString(R.string.onboarding_desc_1)
            ),
            OnboardingPage(
                imageResId = R.drawable.ic_water_drop,
                title = getString(R.string.onboarding_title_2),
                description = getString(R.string.onboarding_desc_2)
            ),
            OnboardingPage(
                imageResId = R.drawable.ic_water_drop,
                title = getString(R.string.onboarding_title_3),
                description = getString(R.string.onboarding_desc_3)
            )
        )
        
        pagerAdapter.submitList(onboardingPages)
        viewPager.adapter = pagerAdapter
    }

    private fun setupIndicators() {
        // Create indicators
        val indicators = Array(pagerAdapter.itemCount) { 
            View(this).apply {
                setBackgroundResource(R.drawable.onboarding_indicator_inactive)
                val params = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.indicator_size),
                    resources.getDimensionPixelSize(R.dimen.indicator_size)
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                layoutParams = params
            }
        }

        // Add indicators to container
        indicators.forEach { indicator ->
            indicatorsContainer.addView(indicator)
        }
        
        // Set the first indicator as active
        if (indicators.isNotEmpty()) {
            indicators[0].setBackgroundResource(R.drawable.onboarding_indicator_active)
        }
    }

    private fun updateIndicators(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val indicator = indicatorsContainer.getChildAt(i)
            indicator.setBackgroundResource(
                if (i == position) R.drawable.onboarding_indicator_active
                else R.drawable.onboarding_indicator_inactive
            )
        }
    }

    private fun setupButtons() {
        btnSkip.setOnClickListener {
            navigateToAuthActivity()
        }

        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < pagerAdapter.itemCount - 1) {
                viewPager.currentItem = currentItem + 1
            } else {
                navigateToAuthActivity()
            }
        }
    }

    private fun updateButtonsVisibility(position: Int) {
        if (position == pagerAdapter.itemCount - 1) {
            btnNext.text = getString(R.string.onboarding_get_started)
        } else {
            btnNext.text = getString(R.string.onboarding_next)
        }
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}
