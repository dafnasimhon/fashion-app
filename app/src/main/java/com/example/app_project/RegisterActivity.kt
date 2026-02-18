package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.app_project.repository.AuthRepository
import com.example.app_project.repository.OutfitRepository

class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var outfitRepository: OutfitRepository

    private var profileImageUri: Uri? = null
    private lateinit var ivProfile: ImageView
    private lateinit var progressBar: ProgressBar

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            ivProfile.setImageURI(it)
            ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        authRepository = AuthRepository()
        outfitRepository = OutfitRepository()


        ivProfile = findViewById(R.id.register_IV_profile)
        progressBar = findViewById(R.id.register_PB_loading)

        val etFullName = findViewById<EditText>(R.id.et_username)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etRePassword = findViewById<EditText>(R.id.et_retype_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val tvGoToLogin = findViewById<TextView>(R.id.tv_go_to_login)


        ivProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val rePassword = etRePassword.text.toString().trim()


            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            setLoading(true)


            authRepository.register(email, password, fullName) { success, error ->
                if (success) {
                    if (profileImageUri != null) {
                        uploadImageAndFinish(profileImageUri!!)
                    } else {
                        finishRegistration()
                    }
                } else {
                    setLoading(false)
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun uploadImageAndFinish(uri: Uri) {
        outfitRepository.uploadProfileImage(uri) { success, error ->
            setLoading(false)
            if (!success) {
                Toast.makeText(this, "Account created, but image failed: $error", Toast.LENGTH_LONG).show()
            }
            finishRegistration()
        }
    }


    private fun finishRegistration() {
        Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btn_register).isEnabled = !isLoading
    }
}