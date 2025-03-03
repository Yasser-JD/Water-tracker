package com.jdcoding.watertracker.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Utility class for validation logic
 */
object ValidationUtils {
    
    private const val MIN_PASSWORD_LENGTH = 8
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    )
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password - at least 8 characters with at least:
     * - 1 digit
     * - 1 lowercase letter
     * - 1 uppercase letter
     * - 1 special character
     * - No whitespace
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH && 
               PASSWORD_PATTERN.matcher(password).matches()
    }

    /**
     * Validate simple password - at least 8 characters
     * Less strict for testing purposes
     */
    fun isValidSimplePassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    /**
     * Validate username - 3 to 30 characters, alphanumeric and underscore only
     */
    fun isValidUsername(username: String): Boolean {
        if (username.isEmpty() || username.length < 3 || username.length > 30) {
            return false
        }
        
        return username.matches(Regex("^[a-zA-Z0-9_]+$"))
    }
    
    /**
     * Validate that the two passwords match
     */
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
    
    /**
     * Validate water intake amount
     */
    fun isValidWaterIntake(amount: Int): Boolean {
        return amount > 0 && amount <= 5000 // Maximum 5 liters at once
    }
    
    /**
     * Validate daily water goal
     */
    fun isValidDailyGoal(goal: Int): Boolean {
        return goal >= 1000 && goal <= 10000 // 1 to 10 liters
    }
}
