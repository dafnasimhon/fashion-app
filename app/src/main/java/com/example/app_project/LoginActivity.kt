package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_project.repository.AuthRepository

/**
 * Activity responsible for authenticating existing users and granting access to the app.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private val TAG = "StyleMate_Lifecycle" // Tag used for debugging and filtering logs in Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "LoginActivity -> onCreate")

        authRepository = AuthRepository()

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvGoToRegister = findViewById<TextView>(R.id.tv_go_to_register)

        // Handles the login process by validating input and communicating with Firebase
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Client-side validation to ensure all required fields are provided
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "LoginActivity: Attempting login for user: $email")

            // Execute the login request via the AuthRepository
            authRepository.login(email, password) { success, error ->
                if (success) {
                    Log.d(TAG, "LoginActivity: Login successful")
                    // Redirect the user to the main feed activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity so the user cannot navigate back to it
                } else {
                    Log.e(TAG, "LoginActivity: Login failed with error: $error")
                    Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Navigate the user to the registration screen if they don't have an account
        tvGoToRegister.setOnClickListener {
            Log.d(TAG, "LoginActivity: Navigating to RegisterActivity")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // --- Lifecycle Methods for state monitoring and debugging ---

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginActivity -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "LoginActivity -> onResume")
    }
}