package com.jdcoding.watertracker.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.WaterTrackerApplication
import com.jdcoding.watertracker.ui.main.MainActivity
import com.jdcoding.watertracker.ui.main.goals.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var birthdateLayout: TextInputLayout
    private lateinit var birthdateEditText: TextInputEditText
    private lateinit var dailyGoalLayout: TextInputLayout
    private lateinit var dailyGoalEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginText: TextView
    
    private var selectedDate: Date? = null
    
    private val authViewModel: AuthViewModel by viewModels { 
        val app = requireActivity().application as WaterTrackerApplication
        AuthViewModel.Factory(app.repository, app.sessionManager)
    }
    
    private val goalViewModel: GoalViewModel by viewModels {
        val app = requireActivity().application as WaterTrackerApplication
        GoalViewModel.Factory(app.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        
        // Initialize views
        usernameLayout = view.findViewById(R.id.username_layout)
        usernameEditText = view.findViewById(R.id.username_edit_text)
        emailLayout = view.findViewById(R.id.email_layout)
        emailEditText = view.findViewById(R.id.email_edit_text)
        passwordLayout = view.findViewById(R.id.password_layout)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_layout)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text)
        birthdateLayout = view.findViewById(R.id.date_of_birth_layout)
        birthdateEditText = view.findViewById(R.id.date_of_birth_edit_text)
        dailyGoalLayout = view.findViewById(R.id.daily_goal_layout)
        dailyGoalEditText = view.findViewById(R.id.daily_goal_edit_text)
        registerButton = view.findViewById(R.id.register_button)
        loginText = view.findViewById(R.id.login_now_text)
        
        // Set default daily goal
        dailyGoalEditText.setText("2000")
        
        // Setup observers
        setupObservers()
        
        // Setup click listeners
        setupListeners()
        
        return view
    }
    
    private fun setupObservers() {
        authViewModel.registrationStatus.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is RegistrationResult.Success -> {
                    // Create goal for user
                    val dailyGoal = dailyGoalEditText.text.toString().toIntOrNull() ?: 2000
                    goalViewModel.createGoal(result.userId, dailyGoal)
                    
                    Toast.makeText(context, R.string.auth_success, Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                is RegistrationResult.InvalidUsername -> {
                    usernameLayout.error = getString(R.string.auth_invalid_username)
                }
                is RegistrationResult.InvalidEmail -> {
                    emailLayout.error = getString(R.string.auth_invalid_email)
                }
                is RegistrationResult.InvalidPassword -> {
                    passwordLayout.error = getString(R.string.auth_invalid_password)
                }
                is RegistrationResult.PasswordsDoNotMatch -> {
                    confirmPasswordLayout.error = getString(R.string.auth_passwords_dont_match)
                }
                is RegistrationResult.EmailAlreadyExists -> {
                    emailLayout.error = getString(R.string.auth_email_exists)
                }
                is RegistrationResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    private fun setupListeners() {
        birthdateEditText.setOnClickListener {
            showDatePicker()
        }
        
        registerButton.setOnClickListener {
            if (validateInputs()) {
                attemptRegistration()
            }
        }
        
        loginText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        // Default to 18 years ago
        calendar.add(Calendar.YEAR, -18)
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = calendar.time
                
                // Format the date for display
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                birthdateEditText.setText(dateFormat.format(selectedDate!!))
            },
            year, month, day
        )
        
        // Set max date to today
        val today = Calendar.getInstance()
        datePickerDialog.datePicker.maxDate = today.timeInMillis
        
        datePickerDialog.show()
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val birthdate = birthdateEditText.text.toString()
        val dailyGoal = dailyGoalEditText.text.toString()
        
        // Clear previous errors
        usernameLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null
        birthdateLayout.error = null
        dailyGoalLayout.error = null
        
        if (username.isEmpty()) {
            usernameLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (email.isEmpty()) {
            emailLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (password.isEmpty()) {
            passwordLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (password != confirmPassword) {
            confirmPasswordLayout.error = getString(R.string.auth_passwords_dont_match)
            isValid = false
        }
        
        if (birthdate.isEmpty()) {
            birthdateLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (dailyGoal.isEmpty()) {
            dailyGoalLayout.error = getString(R.string.auth_field_required)
            isValid = false
        } else {
            try {
                val goal = dailyGoal.toInt()
                if (goal < 500 || goal > 5000) {
                    dailyGoalLayout.error = getString(R.string.auth_invalid_goal)
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                dailyGoalLayout.error = getString(R.string.auth_invalid_goal)
                isValid = false
            }
        }
        
        return isValid
    }
    
    private fun attemptRegistration() {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        
        selectedDate?.let { birthDate ->
            authViewModel.registerUser(
                username,
                email,
                password,
                confirmPassword,
                birthDate
            )
        } ?: run {
            birthdateLayout.error = getString(R.string.auth_field_required)
        }
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Make sure to close the auth activity
    }
}
