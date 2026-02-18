package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    private val repository = OutfitRepository()
    private val db = FirebaseFirestore.getInstance()

    // Launcher to handle gallery image selection for profile picture updates
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Highlight the profile tab in the bottom navigation
        setupBottomNavigation(R.id.btn_profile)

        setupRecyclerView()
        loadUserData()
        loadMyOutfits()

        val ivProfile = findViewById<ImageView>(R.id.profile_IV_user)
        ivProfile.setOnClickListener {
            // Trigger the gallery picker
            pickImageLauncher.launch("image/*")
        }

        val btnLogout = findViewById<ImageButton>(R.id.profile_BTN_logout)
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * Displays a confirmation dialog to prevent accidental logouts.
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

    /**
     * Terminates the Firebase session and clears the activity stack to return to Login.
     */
    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java).apply {
            // Flags to clear the task so the user cannot navigate back to the profile
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the personal wardrobe feed whenever the user returns to this screen
        if (::adapter.isInitialized) {
            loadMyOutfits()
        }
    }

    /**
     * Uploads the selected profile image to Firebase Storage and updates the Firestore profile.
     */
    private fun uploadImage(uri: Uri) {
        showToast("Updating profile image...")
        repository.uploadProfileImage(uri) { success, error ->
            if (success) {
                showToast("Profile updated successfully!")
                loadUserData() // Refresh UI with the new image URL
            } else {
                showToast("Failed to upload: $error")
            }
        }
    }

    /**
     * Configures a 3-column grid RecyclerView to display personal outfit posts.
     */
    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = false // Likes are hidden on the personal profile view
        ) { outfit ->
            // Pass true to indicate we are navigating from the profile context
            navigateToDetail(outfit, true)
        }

        // Standardized setup using the BaseActivity helper
        setupRecyclerView(R.id.profile_RV_my_outfits, 3, adapter)
    }

    /**
     * Fetches the user's display name and profile image URL from Firestore.
     */
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
    }

    /**
     * Loads the specific list of outfits uploaded by the current user.
     */
    private fun loadMyOutfits() {
        repository.getMyOutfits { list, error ->
            if (list != null) {
                adapter.updateData(list)
            } else {
                showToast("Error: $error")
            }
        }
    }
}