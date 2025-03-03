package com.jdcoding.watertracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Session manager to handle user sessions and app preferences
 */
class SessionManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "WaterTrackerPrefs"
        
        // User session preferences
        const val IS_LOGGED_IN = "IsLoggedIn"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_EMAIL = "email"
        
        // App preferences
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val KEY_REMINDER_FREQUENCY = "reminder_frequency"
        private const val KEY_LAST_LOGIN = "last_login"
        
        // Theme options
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
        
        // Language options
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_SPANISH = "es"
        const val LANGUAGE_GERMAN = "de"
    }

    /**
     * Create login session
     */
    fun createLoginSession(userId: Long, username: String, email: String) {
        editor.apply {
            putBoolean(IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Get stored session data
     */
    fun getUserDetails(): HashMap<String, Any> {
        val user = HashMap<String, Any>()
        user[KEY_USER_ID] = pref.getLong(KEY_USER_ID, -1L)
        user[KEY_USERNAME] = pref.getString(KEY_USERNAME, "") ?: ""
        user[KEY_EMAIL] = pref.getString(KEY_EMAIL, "") ?: ""
        return user
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGGED_IN, false)
    }

    /**
     * Clear session and log out user
     */
    fun logout() {
        editor.apply {
            clear() // This will delete all session data
            // Keep some preferences after logout
            putBoolean(KEY_NOTIFICATIONS_ENABLED, areNotificationsEnabled())
            putString(KEY_LANGUAGE, getLanguage())
            putString(KEY_THEME, getTheme())
            apply()
        }
    }
    
    /**
     * Enable or disable notification setting
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        editor.apply()
    }
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return pref.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) // Default to true
    }
    
    /**
     * Set app language
     */
    fun setLanguage(language: String) {
        editor.putString(KEY_LANGUAGE, language)
        editor.apply()
    }
    
    /**
     * Get app language
     */
    fun getLanguage(): String {
        return pref.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }
    
    /**
     * Set app theme
     */
    fun setTheme(theme: String) {
        editor.putString(KEY_THEME, theme)
        editor.apply()
    }
    
    /**
     * Get app theme
     */
    fun getTheme(): String {
        return pref.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }
    
    /**
     * Set reminder frequency (in minutes)
     */
    fun setReminderFrequency(minutes: Int) {
        editor.putInt(KEY_REMINDER_FREQUENCY, minutes)
        editor.apply()
    }
    
    /**
     * Get reminder frequency (in minutes)
     */
    fun getReminderFrequency(): Int {
        return pref.getInt(KEY_REMINDER_FREQUENCY, 60) // Default to 1 hour
    }
    
    /**
     * Get last login timestamp
     */
    fun getLastLoginTime(): Long {
        return pref.getLong(KEY_LAST_LOGIN, 0L)
    }
    
    /**
     * Clear a specific preference
     */
    fun clearPreference(key: String) {
        editor.remove(key)
        editor.apply()
    }
    
    /**
     * Update user information
     */
    fun updateUserInfo(username: String, email: String) {
        editor.apply {
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            apply()
        }
    }
}
