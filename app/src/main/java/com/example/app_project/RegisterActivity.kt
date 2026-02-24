package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.app_project.repository.AuthRepository
import com.example.app_project.repository.OutfitRepository

/**
 * Activity responsible for creating new user accounts and uploading initial profile pictures.
 */
class RegisterActivity : AppCompatActivity() {

    // שימוש ב-Singletons עבור ה-Repositories כפי שהגדרנו בעבר
    private val authRepository = AuthRepository
    private val outfitRepository = OutfitRepository

    private var profileImageUri: Uri? = null
    private lateinit var ivProfile: ImageView
    private lateinit var animationView: LottieAnimationView // הוחלף מ-ProgressBar

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

        // ניהול ה-Padding למניעת חסימה על ידי המקלדת
        val scrollView = findViewById<View>(R.id.register_scroll_view)
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = if (ime.bottom > systemBars.bottom) ime.bottom else systemBars.bottom
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        ivProfile = findViewById(R.id.register_IV_profile)
        // אתחול האנימציה לפי ה-ID החדש מה-XML המעודכן
        animationView = findViewById(R.id.register_animation)

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
                showToast("Please fill all fields")
                return@setOnClickListener
            }

            if (password != rePassword) {
                showToast("Passwords do not match")
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
                    showToast("Error: $error")
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
                showToast("Account created, but image failed: $error")
            }
            finishRegistration()
        }
    }

    private fun finishRegistration() {
        showToast("Account created! Please login.")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * ניהול מצב הטעינה באמצעות ה-Lottie Animation החדש
     */
    private fun setLoading(isLoading: Boolean) {
        val btnRegister = findViewById<Button>(R.id.btn_register)

        if (isLoading) {
            animationView.visibility = View.VISIBLE
            animationView.playAnimation() // הפעלת האנימציה בזמן הרישום
            btnRegister.isEnabled = false
            btnRegister.alpha = 0.5f
        } else {
            animationView.visibility = View.GONE
            animationView.pauseAnimation()
            btnRegister.isEnabled = true
            btnRegister.alpha = 1.0f
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}