package com.example.app_project

import android.os.Bundle
import android.util.Log
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.repository.OutfitRepository

/**
 * Activity responsible for displaying the user's saved/favorited outfits in a grid.
 */
class FavoritesActivity : BaseActivity() {

    private lateinit var adapter: OutfitAdapter

    private val outfitRepository = OutfitRepository
    private val TAG = "StyleMate_Favorites"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        applyEdgeToEdge(findViewById(R.id.main))

        // Highlight the favorites icon in the bottom navigation bar
        setupBottomNavigation(R.id.btn_favorites)

        setupRecyclerView()
        loadFavoriteOutfits()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            loadFavoriteOutfits()
        }
    }

    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = true
        ) { outfit ->
            navigateToDetail(outfit)
        }
        setupRecyclerView(R.id.fav_RV_list, 2, adapter)
    }

    private fun loadFavoriteOutfits() {
        outfitRepository.getFavoriteOutfits { list, error ->
            if (list != null) {
                adapter.updateData(list)
                if (list.isEmpty()) {
                    showToast("Your wishlist is empty!")
                }
            } else {
                Log.e(TAG, "Error loading favorites: $error")
                showToast("Error: $error")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        outfitRepository.clearListeners()
    }
}