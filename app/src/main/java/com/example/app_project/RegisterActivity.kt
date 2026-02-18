package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

/**
 * Activity responsible for creating new user accounts and uploading initial profile pictures.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var outfitRepository: OutfitRepository

    private var profileImageUri: Uri? = null
    private lateinit var ivProfile: ImageView
    private lateinit var progressBar: ProgressBar

    private val TAG = "StyleMate_Lifecycle" // Tag for filtering lifecycle and registration logs

    // Launcher to handle gallery image selection for the new user profile
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d(TAG, "RegisterActivity: Image selected from gallery")
            profileImageUri = it
            ivProfile.setImageURI(it)
            ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "RegisterActivity -> onCreate")

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
            Log.d(TAG, "RegisterActivity: Opening gallery to pick profile image")
            pickImageLauncher.launch("image/*")
        }

        // Logic for handling the registration form submission
        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val rePassword = etRePassword.text.toString().trim()

            // Basic client-side validation for empty fields
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verification that both password entries match
            if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "RegisterActivity: Attempting registration for: $email")
            setLoading(true)

            // Primary registration process through Firebase Authentication
            authRepository.register(email, password, fullName) { success, error ->
                if (success) {
                    Log.d(TAG, "RegisterActivity: Auth registration successful")
                    // If an image was selected, upload it before finishing registration
                    if (profileImageUri != null) {
                        Log.d(TAG, "RegisterActivity: Starting profile image upload")
                        uploadImageAndFinish(profileImageUri!!)
                    } else {
                        Log.d(TAG, "RegisterActivity: No profile image, finishing registration")
                        finishRegistration()
                    }
                } else {
                    setLoading(false)
                    Log.e(TAG, "RegisterActivity: Registration failed - $error")
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvGoToLogin.setOnClickListener {
            Log.d(TAG, "RegisterActivity: Navigating back to LoginActivity")
            finish()
        }
    }

    /**
     * Uploads the selected profile image to Firebase Storage and then proceeds to finish.
     */
    private fun uploadImageAndFinish(uri: Uri) {
        outfitRepository.uploadProfileImage(uri) { success, error ->
            setLoading(false)
            if (!success) {
                Log.e(TAG, "RegisterActivity: Profile image upload failed - $error")
                Toast.makeText(this, "Account created, but image failed: $error", Toast.LENGTH_LONG).show()
            } else {
                Log.d(TAG, "RegisterActivity: Profile image upload successful")
            }
            finishRegistration()
        }
    }

    /**
     * Finalizes the flow by directing the user back to the Login screen.
     */
    private fun finishRegistration() {
        Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Controls the visibility of the progress bar and enables/disables the register button.
     */
    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btn_register).isEnabled = !isLoading
    }

    // --- Lifecycle Methods for state monitoring ---

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "RegisterActivity -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "RegisterActivity -> onResume")
    }
}