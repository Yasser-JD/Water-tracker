package com.jdcoding.watertracker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.ui.main.MainActivity
import com.jdcoding.watertracker.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    
    private val SPLASH_DELAY = 2000L // 2 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val currentUser = FirebaseAuth.getInstance().currentUser

        // Navigate to next screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                navigateToMainActivity()
            } else {
                navigateToNextScreen()
            }
            finish()
        }, SPLASH_DELAY)
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToNextScreen() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }
}
