package com.jdcoding.watertracker.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.User
import com.jdcoding.watertracker.utils.SessionManager
import com.jdcoding.watertracker.utils.ValidationUtils
import kotlinx.coroutines.launch

/**
 * ViewModel for the Profile Fragment
 */
class ProfileViewModel(
    private val repository: WaterTrackerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updatePasswordResult = MutableLiveData<UpdatePasswordResult>()
    val updatePasswordResult: LiveData<UpdatePasswordResult> = _updatePasswordResult
    
    /**
     * Load user data based on the ID from session
     */
    fun loadUserData() {
        viewModelScope.launch {
            val userId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID] as Long
            if (userId != -1L) {
                val userData = repository.getUserById(userId)
                _user.value = userData
            } else {
                _user.value = null
            }
        }
    }

    /**
     * Update user password
     */
    fun updatePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            val userId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID] as Long
            if (userId == -1L) {
                _updatePasswordResult.value = UpdatePasswordResult.Error("User not found")
                return@launch
            }

            // Validate input
            if (!ValidationUtils.isValidSimplePassword(newPassword)) {
                _updatePasswordResult.value = UpdatePasswordResult.InvalidPassword
                return@launch
            }

            if (newPassword != confirmPassword) {
                _updatePasswordResult.value = UpdatePasswordResult.PasswordsDoNotMatch
                return@launch
            }

            // Verify current password
            val user = repository.getUserById(userId)
            if (user == null || user.password != currentPassword) {
                _updatePasswordResult.value = UpdatePasswordResult.IncorrectCurrentPassword
                return@launch
            }

            // Update password
            try {
                repository.updatePassword(userId, newPassword)
                _updatePasswordResult.value = UpdatePasswordResult.Success
            } catch (e: Exception) {
                _updatePasswordResult.value = UpdatePasswordResult.Error(e.message ?: "Failed to update password")
            }
        }
    }
    
    /**
     * Toggle notifications setting
     */
    fun toggleNotifications(enabled: Boolean) {
        sessionManager.setNotificationsEnabled(enabled)
    }
    
    /**
     * Set app language
     */
    fun setLanguage(language: String) {
        sessionManager.setLanguage(language)
    }
    
    /**
     * Set app theme
     */
    fun setTheme(theme: String) {
        sessionManager.setTheme(theme)
    }
    
    /**
     * Get notification setting
     */
    fun areNotificationsEnabled(): Boolean {
        return sessionManager.areNotificationsEnabled()
    }
    
    /**
     * Get app language
     */
    fun getLanguage(): String {
        return sessionManager.getLanguage()
    }
    
    /**
     * Get app theme
     */
    fun getTheme(): String {
        return sessionManager.getTheme()
    }
    
    /**
     * Logout user
     */
    fun logout() {
        sessionManager.logout()
    }

    /**
     * Factory for creating the ViewModel
     */
    class Factory(
        private val repository: WaterTrackerRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

/**
 * Sealed class for password update results
 */
sealed class UpdatePasswordResult {
    object Success : UpdatePasswordResult()
    object InvalidPassword : UpdatePasswordResult()
    object PasswordsDoNotMatch : UpdatePasswordResult()
    object IncorrectCurrentPassword : UpdatePasswordResult()
    data class Error(val message: String) : UpdatePasswordResult()
}
