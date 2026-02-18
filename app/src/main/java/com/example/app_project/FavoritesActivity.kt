package com.example.app_project

import android.os.Bundle
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.repository.OutfitRepository

/**
 * Activity responsible for displaying the user's saved/favorited outfits in a grid.
 */
class FavoritesActivity : BaseActivity() {

    private lateinit var adapter: OutfitAdapter
    private val outfitRepository = OutfitRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Highlight the favorites icon in the bottom navigation bar
        setupBottomNavigation(R.id.btn_favorites)

        setupRecyclerView()
        loadFavoriteOutfits()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list whenever the user returns to this screen to ensure data accuracy
        if (::adapter.isInitialized) {
            loadFavoriteOutfits()
        }
    }

    /**
     * Initializes the RecyclerView with a 2-column grid and defines the click behavior.
     */
    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = true
        ) { outfit ->
            // Navigate to the detail view when an outfit is clicked
            navigateToDetail(outfit)
        }
        // Utilizes the helper method from BaseActivity for standardized setup
        setupRecyclerView(R.id.fav_RV_list, 2, adapter)
    }

    /**
     * Fetches favorited outfits from the repository and updates the UI accordingly.
     */
    private fun loadFavoriteOutfits() {
        outfitRepository.getFavoriteOutfits { list, error ->
            if (list != null) {
                // Update the adapter with the fetched list of favorites
                adapter.updateData(list)

                if (list.isEmpty()) {
                    showToast("Your wishlist is empty!")
                }
            } else {
                showToast("Error: $error")
            }
        }
    }
}