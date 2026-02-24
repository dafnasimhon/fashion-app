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

    private val authRepository = AuthRepository
    private val TAG = "StyleMate_Lifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "LoginActivity -> onCreate")

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvGoToRegister = findViewById<TextView>(R.id.tv_go_to_register)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "LoginActivity: Attempting login for user: $email")
            authRepository.login(email, password) { success, error ->
                if (success) {
                    Log.d(TAG, "LoginActivity: Login successful")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(TAG, "LoginActivity: Login failed with error: $error")
                    Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvGoToRegister.setOnClickListener {
            Log.d(TAG, "LoginActivity: Navigating to RegisterActivity")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginActivity -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "LoginActivity -> onResume")
    }
}