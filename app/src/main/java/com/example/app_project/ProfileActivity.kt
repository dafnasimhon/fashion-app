package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.models.AppConfig
import com.example.app_project.repository.OutfitRepository
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity that displays the user's profile information and their personal wardrobe uploads.
 */
class ProfileActivity : BaseActivity() {

    private lateinit var adapter: OutfitAdapter

    private val repository = OutfitRepository
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "StyleMate_Profile"

    // Launcher to handle gallery image selection for profile picture updates
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d(TAG, "Image selected: $it")
            uploadImage(it)
        } ?: Log.w(TAG, "No image selected")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        applyEdgeToEdge(findViewById(R.id.main))

        setupBottomNavigation(R.id.btn_profile)

        setupRecyclerView()
        loadUserData()
        loadMyOutfits()

        val ivProfile = findViewById<ImageView>(R.id.profile_IV_user)
        ivProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val btnLogout = findViewById<ImageButton>(R.id.profile_BTN_logout)
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * Displays a confirmation dialog before calling the centralized logout.
     */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            loadMyOutfits()
        }
    }

    /**
     * Uploads the selected profile image using the Repository Singleton.
     */
    private fun uploadImage(uri: Uri) {
        showToast("Updating profile image...")
        repository.uploadProfileImage(uri) { success, error ->
            if (success) {
                showToast("Profile updated successfully!")
                loadUserData()
            } else {
                Log.e(TAG, "Upload failed: $error")
                showToast("Failed to upload: $error")
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = false
        ) { outfit ->
            navigateToDetail(outfit, true)
        }

        setupRecyclerView(R.id.profile_RV_my_outfits, 3, adapter)
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        val tvName = findViewById<TextView>(R.id.profile_TV_username)
        val ivProfile = findViewById<ImageView>(R.id.profile_IV_user)

        db.collection(AppConfig.COLL_USERS).document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    tvName.text = doc.getString("fullName") ?: "StyleMate User"
                    val profileUrl = doc.getString("profileImageUrl")
                    if (!profileUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_person)
                            .into(ivProfile)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error loading user data: ${e.message}")
            }
    }

    private fun loadMyOutfits() {
        repository.getMyOutfits { list, error ->
            if (list != null) {
                adapter.updateData(list)
            } else {
                Log.e(TAG, "Error loading outfits: $error")
                showToast("Error: $error")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.clearListeners()
    }
}