package com.jdcoding.watertracker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    
    private val SPLASH_DELAY = 2500L // 2.5 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Navigate to next screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
            finish()
        }, SPLASH_DELAY)
    }
    
    private fun navigateToNextScreen() {
        // For now, we'll always navigate to the onboarding screen
        // In a future enhancement, we could check if the user is already logged in
        // and navigate to the main activity directly
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }
}
