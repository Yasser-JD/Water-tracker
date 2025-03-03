package com.jdcoding.watertracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.WaterTrackerApplication
import com.jdcoding.watertracker.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var registerText: TextView
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    
    private val authViewModel: AuthViewModel by viewModels { 
        val app = requireActivity().application as WaterTrackerApplication
        AuthViewModel.Factory(app.repository, app.sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        
        // Initialize ActivityResultLauncher for Google Sign In
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        
        // Initialize views
        emailLayout = view.findViewById(R.id.email_layout)
        emailEditText = view.findViewById(R.id.email_edit_text)
        passwordLayout = view.findViewById(R.id.password_layout)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        loginButton = view.findViewById(R.id.login_button)
        googleSignInButton = view.findViewById(R.id.google_sign_in_button)
        registerText = view.findViewById(R.id.register_now_text)
        
        // Setup observers
        setupObservers()
        
        // Setup click listeners
        setupListeners()
        
        return view
    }
    
    private fun setupObservers() {
        authViewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(context, R.string.auth_success, Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                is LoginResult.InvalidEmail -> {
                    emailLayout.error = getString(R.string.auth_invalid_email)
                }
                is LoginResult.InvalidPassword -> {
                    passwordLayout.error = getString(R.string.auth_invalid_password)
                }
                is LoginResult.InvalidCredentials -> {
                    Toast.makeText(context, R.string.auth_error, Toast.LENGTH_SHORT).show()
                }
                is LoginResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            if (validateInputs()) {
                attemptLogin()
            }
        }
        
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
        
        registerText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .commit()
        }
    }
    
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
    
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    // Create or update user in your database
                    authViewModel.handleGoogleSignIn(user?.email ?: "", user?.displayName ?: "")
                    navigateToMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        
        // Clear previous errors
        emailLayout.error = null
        passwordLayout.error = null
        
        if (email.isEmpty()) {
            emailLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        if (password.isEmpty()) {
            passwordLayout.error = getString(R.string.auth_field_required)
            isValid = false
        }
        
        return isValid
    }
    
    private fun attemptLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        
        authViewModel.loginUser(email, password)
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Make sure to close the auth activity
    }
}
