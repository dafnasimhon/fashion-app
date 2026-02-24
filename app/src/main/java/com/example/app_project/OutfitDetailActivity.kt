package com.example.app_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.app_project.models.AppConfig
import com.example.app_project.repository.OutfitRepository
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity detailing a specific outfit, providing metadata and deletion capabilities for owners.
 */
class OutfitDetailActivity : BaseActivity() {

    private val repository = OutfitRepository
    private val TAG = "StyleMate_Detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outfit_detail)

        applyEdgeToEdge(findViewById(R.id.main))

        val ivImage = findViewById<ImageView>(R.id.detail_IV_image)
        val tvUsername = findViewById<TextView>(R.id.detail_TV_username)
        val btnDelete = findViewById<ImageButton>(R.id.detail_BTN_delete)

        val outfitId = intent.getStringExtra(AppConfig.EXTRA_OUTFIT_ID)
        val imageUrl = intent.getStringExtra(AppConfig.EXTRA_IMAGE_URL)
        val userId = intent.getStringExtra(AppConfig.EXTRA_USER_ID)
        val vibe = intent.getStringExtra(AppConfig.EXTRA_VIBE)
        val isFromProfile = intent.getBooleanExtra(AppConfig.EXTRA_FROM_PROFILE, false)

        // Highlight the relevant tab in navigation
        val activeTab = if (isFromProfile) R.id.btn_profile else 0
        setupBottomNavigation(activeTab)

        // Populate metadata fields
        setupField(findViewById(R.id.detail_TV_vibe), "VIBE", vibe)
        setupField(findViewById(R.id.detail_TV_top), "TOP", intent.getStringExtra(AppConfig.EXTRA_TOP))
        setupField(findViewById(R.id.detail_TV_bottom), "BOTTOM", intent.getStringExtra(AppConfig.EXTRA_BOTTOM))
        setupField(findViewById(R.id.detail_TV_jacket), "JACKET", intent.getStringExtra(AppConfig.EXTRA_JACKET))
        setupField(findViewById(R.id.detail_TV_shoes), "SHOES", intent.getStringExtra(AppConfig.EXTRA_SHOES))
        setupField(findViewById(R.id.detail_TV_jewelry), "JEWELRY", intent.getStringExtra(AppConfig.EXTRA_JEWELRY))
        setupField(findViewById(R.id.detail_TV_sunglasses), "SUNGLASSES", intent.getStringExtra(AppConfig.EXTRA_SUNGLASSES))
        setupField(findViewById(R.id.detail_TV_bag), "BAG", intent.getStringExtra(AppConfig.EXTRA_BAG))

        // Only allow deletion if the current user is the owner and viewing from their profile
        val currentUserId = auth.currentUser?.uid
        btnDelete.visibility = if (currentUserId != null && currentUserId == userId && isFromProfile) View.VISIBLE else View.GONE

        // Load image and owner information
        Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_outfit).into(ivImage)
        fetchUsername(userId, tvUsername)

        btnDelete.setOnClickListener {
            if (outfitId != null && imageUrl != null) showDeleteConfirmation(outfitId, imageUrl)
        }
    }

    private fun setupField(textView: TextView, label: String, value: String?) {
        if (!value.isNullOrEmpty()) {
            textView.visibility = View.VISIBLE
            textView.text = "${label.uppercase()}   —   ${value.uppercase()}"
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun fetchUsername(userId: String?, textView: TextView) {
        if (userId == null) {
            textView.text = "CURATED BY STYLEMATE"
            return
        }
        FirebaseFirestore.getInstance().collection(AppConfig.COLL_USERS).document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("fullName")?.uppercase() ?: "UNKNOWN"
                textView.text = "CURATED BY $name"
            }
            .addOnFailureListener {
                Log.e(TAG, "Error fetching username: ${it.message}")
                textView.text = "CURATED BY STYLEMATE"
            }
    }

    private fun showDeleteConfirmation(outfitId: String, imageUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Outfit")
            .setMessage("Remove this look from your wardrobe?")
            .setPositiveButton("Delete") { _, _ ->
                repository.deleteOutfit(outfitId, imageUrl) { success ->
                    if (success) {
                        showToast("Outfit deleted")
                        finish()
                    } else {
                        showToast("Failed to delete")
                    }
                }
            }
            .setNegativeButton("Cancel", null).show()
    }
}