package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.models.AppConfig
import com.example.app_project.models.Outfit
import com.google.firebase.auth.FirebaseAuth

/**
 * Parent class for all activities to share common logic like authentication and navigation.
 */
open class BaseActivity : AppCompatActivity() {

    protected val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()
        // Ensure user is authenticated as soon as the activity becomes visible
        checkUserStatus()
    }

    override fun onResume() {
        super.onResume()
        // Re-verify auth status if the user returns to the app
        checkUserStatus()
    }

    /**
     * Helper to initialize a RecyclerView with a specific grid layout.
     */
    protected fun setupRecyclerView(rvId: Int, spanCount: Int, adapter: OutfitAdapter): RecyclerView {
        val rv = findViewById<RecyclerView>(rvId)
        rv.layoutManager = GridLayoutManager(this, spanCount)
        rv.adapter = adapter
        return rv
    }

    protected fun showToast(message: String?) {
        Toast.makeText(this, message ?: "An error occurred", Toast.LENGTH_SHORT).show()
    }

    /**
     * Redirects unauthenticated users to the Login screen if the current page requires auth.
     */
    private fun checkUserStatus() {
        if (auth.currentUser == null && isAuthRequired()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Excludes Login and Register screens from the automatic authentication check.
     */
    private fun isAuthRequired(): Boolean {
        val currentClass = this::class.java.simpleName
        return currentClass != LoginActivity::class.java.simpleName &&
                currentClass != RegisterActivity::class.java.simpleName
    }

    /**
     * Sets up the bottom navigation bar and highlights the active screen.
     */
    protected fun setupBottomNavigation(activeButtonId: Int) {
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnAdd = findViewById<ImageButton>(R.id.btn_add_outfit)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)
        val btnFavorites = findViewById<ImageButton>(R.id.btn_favorites)

        val buttons = listOf(btnHome, btnAdd, btnProfile, btnFavorites)

        buttons.forEach { button ->
            if (button?.id == activeButtonId) {
                button.setBackgroundResource(R.drawable.bg_nav_active)
                button.setPadding(16, 16, 16, 16)
            } else {
                button?.background = null
                button?.setPadding(0, 0, 0, 0)
            }
        }

        btnHome?.setOnClickListener { navigateTo(MainActivity::class.java) }
        btnAdd?.setOnClickListener { navigateTo(UploadOutfitActivity::class.java) }
        btnFavorites?.setOnClickListener { navigateTo(FavoritesActivity::class.java) }
        btnProfile?.setOnClickListener { navigateTo(ProfileActivity::class.java) }
    }

    /**
     * Passes all outfit metadata via Intent to the Detail screen.
     */
    protected fun navigateToDetail(outfit: Outfit, isFromProfile: Boolean = false) {
        val intent = Intent(this, OutfitDetailActivity::class.java).apply {
            putExtra(AppConfig.EXTRA_OUTFIT_ID, outfit.id)
            putExtra(AppConfig.EXTRA_IMAGE_URL, outfit.imageUrl)
            putExtra(AppConfig.EXTRA_USER_ID, outfit.userId)
            putExtra(AppConfig.EXTRA_FROM_PROFILE, isFromProfile)
            putExtra(AppConfig.EXTRA_VIBE, outfit.vibe)
            putExtra(AppConfig.EXTRA_TOP, outfit.top)
            putExtra(AppConfig.EXTRA_BOTTOM, outfit.bottom)
            putExtra(AppConfig.EXTRA_JACKET, outfit.jacket)
            putExtra(AppConfig.EXTRA_SHOES, outfit.shoes)
            putExtra(AppConfig.EXTRA_JEWELRY, outfit.jewelry)
            putExtra(AppConfig.EXTRA_SUNGLASSES, outfit.sunglasses)
            putExtra(AppConfig.EXTRA_BAG, outfit.bag)
        }
        startActivity(intent)
    }

    /**
     * Standard navigation helper to avoid re-opening the same activity.
     */
    private fun navigateTo(destination: Class<*>) {
        if (this.javaClass != destination) {
            val intent = Intent(this, destination)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }
    }
}