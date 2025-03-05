package com.jdcoding.watertracker.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.User
import com.jdcoding.watertracker.utils.SessionManager
import com.jdcoding.watertracker.utils.ValidationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/**
 * ViewModel for handling authentication operations using Firebase Auth
 */
class AuthViewModel(
    private val repository: WaterTrackerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    
    private val _registrationStatus = MutableLiveData<RegistrationResult>()
    val registrationStatus: LiveData<RegistrationResult> = _registrationStatus

    private val _loginStatus = MutableLiveData<LoginResult>()
    val loginStatus: LiveData<LoginResult> = _loginStatus

    /**
     * Attempts to register a new user using Firebase Auth
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

            try {
                // Create Firebase Auth user
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                
                if (firebaseUser != null) {
                    // Create and insert new user in local database
                    val newUser = User(
                        id = firebaseUser.uid.hashCode().toLong(),
                        username = username,
                        email = email,
                        password = "", // Don't store password locally
                        dateOfBirth = dateOfBirth,
                        dailyWaterGoal = 2000, // Default: 2000ml
                        createdAt = Date()
                    )

                    repository.insertUser(newUser)
                    
                    // Create session
                    sessionManager.createLoginSession(newUser.id, username, email)
                    
                    _registrationStatus.value = RegistrationResult.Success(newUser.id)
                } else {
                    _registrationStatus.value = RegistrationResult.Error("Failed to create user")
                }
            } catch (e: Exception) {
                _registrationStatus.value = RegistrationResult.Error(e.message ?: "Registration failed")
            }
        }
    }

    /**
     * Attempts to log in a user using Firebase Auth
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
                // Sign in with Firebase
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                
                if (firebaseUser != null) {
                    // Get user from local database
                    val user = repository.getUserByEmail(email)
                    
                    if (user != null) {
                        // Create session
                        sessionManager.createLoginSession(user.id, user.username, user.email)
                        _loginStatus.value = LoginResult.Success(user.id)
                    } else {
                        _loginStatus.value = LoginResult.Error("User not found in local database")
                    }
                } else {
                    _loginStatus.value = LoginResult.InvalidCredentials
                }
            } catch (e: Exception) {
                _loginStatus.value = LoginResult.Error(e.message ?: "Login failed")
            }
        }
    }

    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            try {
                // Get Firebase credentials from Google ID token
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                
                // Sign in with Firebase
                val authResult = auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                
                if (firebaseUser != null) {
                    // Check if user exists in local database
                    var user = repository.getUserByEmail(firebaseUser.email ?: "")
                    
                    if (user == null) {
                        // Create new user in local database
                        user = User(
                            id = firebaseUser.uid.hashCode().toLong(),
                            email = firebaseUser.email ?: "",
                            password = "", // No password for OAuth users
                            username = firebaseUser.displayName ?: "User",
                            dateOfBirth = Date(),
                            dailyWaterGoal = 2000 // Default daily goal
                        )
                        repository.insertUser(user)
                    }
                    
                    // Save user session
                    sessionManager.createLoginSession(user.id, user.username, user.email)
                    _loginStatus.value = LoginResult.Success(user.id)
                } else {
                    _loginStatus.value = LoginResult.Error("Google Sign-In failed")
                }
            } catch (e: Exception) {
                _loginStatus.value = LoginResult.Error(e.message ?: "Google Sign-In failed")
            }
        }
    }

    /**
     * Logs out the current user
     */
    fun logout() {
        auth.signOut()
        sessionManager.logout()
    }

    /**
     * Check if a user is currently logged in
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && sessionManager.isLoggedIn()
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
