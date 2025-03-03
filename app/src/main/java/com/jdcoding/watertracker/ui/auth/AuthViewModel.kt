package com.jdcoding.watertracker.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.User
import com.jdcoding.watertracker.utils.SessionManager
import com.jdcoding.watertracker.utils.ValidationUtils
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

/**
 * ViewModel for handling authentication operations
 */
class AuthViewModel(
    private val repository: WaterTrackerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registrationStatus = MutableLiveData<RegistrationResult>()
    val registrationStatus: LiveData<RegistrationResult> = _registrationStatus

    private val _loginStatus = MutableLiveData<LoginResult>()
    val loginStatus: LiveData<LoginResult> = _loginStatus

    /**
     * Attempts to register a new user
     */
    fun registerUser(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        dateOfBirth: Date
    ) {
        viewModelScope.launch {
            // Validate input
            when {
                !ValidationUtils.isValidUsername(username) -> {
                    _registrationStatus.value = RegistrationResult.InvalidUsername
                    return@launch
                }
                !ValidationUtils.isValidEmail(email) -> {
                    _registrationStatus.value = RegistrationResult.InvalidEmail
                    return@launch
                }
                !ValidationUtils.isValidSimplePassword(password) -> {
                    _registrationStatus.value = RegistrationResult.InvalidPassword
                    return@launch
                }
                !ValidationUtils.doPasswordsMatch(password, confirmPassword) -> {
                    _registrationStatus.value = RegistrationResult.PasswordsDoNotMatch
                    return@launch
                }
            }

            // Check if email already exists
            val emailExists = repository.checkEmailExists(email)
            if (emailExists) {
                _registrationStatus.value = RegistrationResult.EmailAlreadyExists
                return@launch
            }

            // Create and insert new user
            val newUser = User(
                username = username,
                email = email,
                password = password, // In a real app, hash this before storage
                dateOfBirth = dateOfBirth,
                dailyWaterGoal = 2000, // Default: 2000ml
                createdAt = Date()
            )

            try {
                val userId = repository.insertUser(newUser)
                
                // Auto-login after successful registration
                sessionManager.createLoginSession(userId, username, email)
                
                _registrationStatus.value = RegistrationResult.Success(userId)
            } catch (e: Exception) {
                _registrationStatus.value = RegistrationResult.Error(e.message ?: "Registration failed")
            }
        }
    }

    /**
     * Attempts to log in a user
     */
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            // Validate input
            when {
                !ValidationUtils.isValidEmail(email) -> {
                    _loginStatus.value = LoginResult.InvalidEmail
                    return@launch
                }
                password.isEmpty() -> {
                    _loginStatus.value = LoginResult.InvalidPassword
                    return@launch
                }
            }

            try {
                // Attempt to find user with matching credentials
                val user = repository.getUserByEmailAndPassword(email, password)
                
                if (user != null) {
                    // Create session
                    sessionManager.createLoginSession(user.id, user.username, user.email)
                    _loginStatus.value = LoginResult.Success(user.id)
                } else {
                    _loginStatus.value = LoginResult.InvalidCredentials
                }
            } catch (e: Exception) {
                _loginStatus.value = LoginResult.Error(e.message ?: "Login failed")
            }
        }
    }

    fun handleGoogleSignIn(email: String, displayName: String) {
        viewModelScope.launch {
            try {
                // Check if user exists
                var user = repository.getUserByEmail(email)
                if (user == null) {
                    // Create new user
                    user = User(
                        id = UUID.randomUUID().toString(),
                        email = email,
                        password = "", // No password for OAuth users
                        username = displayName,
                        dateOfBirth = Date(),
                        dailyWaterGoal = 2000 // Default daily goal
                    )
                    repository.insertUser(user)
                }
                // Save user session
                sessionManager.createLoginSession(user.id, user.username, user.email)
                _loginStatus.value = LoginResult.Success(user.id)
            } catch (e: Exception) {
                _loginStatus.value = LoginResult.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Logs out the current user
     */
    fun logout() {
        sessionManager.logout()
    }

    /**
     * Check if a user is currently logged in
     */
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * ViewModel Factory
     */
    class Factory(
        private val repository: WaterTrackerRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(repository, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

/**
 * Sealed class to represent the result of a registration attempt
 */
sealed class RegistrationResult {
    data class Success(val userId: Long) : RegistrationResult()
    data class Error(val message: String) : RegistrationResult()
    object InvalidUsername : RegistrationResult()
    object InvalidEmail : RegistrationResult()
    object InvalidPassword : RegistrationResult()
    object PasswordsDoNotMatch : RegistrationResult()
    object EmailAlreadyExists : RegistrationResult()
}

/**
 * Sealed class to represent the result of a login attempt
 */
sealed class LoginResult {
    data class Success(val userId: Long) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object InvalidEmail : LoginResult()
    object InvalidPassword : LoginResult()
    object InvalidCredentials : LoginResult()
}
