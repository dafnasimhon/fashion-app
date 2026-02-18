package com.example.app_project

import android.os.Bundle
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.models.AppConfig
import com.example.app_project.models.Outfit
import com.example.app_project.repository.OutfitRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * The primary activity serving as the community feed where users can discover outfit inspirations.
 */
class MainActivity : BaseActivity() {

    private lateinit var adapter: OutfitAdapter
    private val outfitRepository = OutfitRepository()

    // Local cache of the feed to enable instant client-side filtering
    private var allOutfitsList: List<Outfit> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components and shared navigation
        setupBottomNavigation(R.id.btn_home)
        setupRecyclerView()
        setupFilterChips()

        // Initial data fetch from Firestore
        loadOutfitsFromFirebase()
    }

    /**
     * Configures the grid-based RecyclerView and its click-to-detail behavior.
     */
    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = true
        ) { outfit ->
            // Open the detail screen for the selected outfit
            navigateToDetail(outfit)
        }

        // Standard 2-column grid layout
        setupRecyclerView(R.id.main_RV_list, 2, adapter)
    }

    /**
     * Dynamically generates filter chips based on the vibe categories defined in resources.
     */
    private fun setupFilterChips() {
        val chipGroup = findViewById<ChipGroup>(R.id.main_CG_vibes)
        val vibes = resources.getStringArray(R.array.outfit_vibes)

        vibes.forEach { vibeName ->
            val chip = Chip(this).apply {
                text = vibeName.uppercase()
                isCheckable = true
                setChipBackgroundColorResource(android.R.color.white)
                setOnClickListener {
                    filterOutfits(vibeName)
                }
            }
            chipGroup.addView(chip)
        }

        // Default filter to show all community content
        findViewById<Chip>(R.id.chip_all).setOnClickListener {
            filterOutfits(AppConfig.FILTER_ALL)
        }
    }

    /**
     * Retrieves the community feed from Firestore and updates the local cache.
     */
    private fun loadOutfitsFromFirebase() {
        outfitRepository.getAllOutfits { list, error ->
            if (list != null) {
                allOutfitsList = list
                adapter.updateData(list)

                if (list.isEmpty()) {
                    showToast("No inspirations found.")
                }
            } else {
                showToast("Error: $error")
            }
        }
    }

    /**
     * Performs instantaneous client-side filtering based on the selected "Vibe".
     */
    private fun filterOutfits(vibe: String) {
        val filtered = if (vibe == AppConfig.FILTER_ALL) {
            allOutfitsList
        } else {
            // Case-insensitive filtering against the local list
            allOutfitsList.filter { it.vibe.equals(vibe, ignoreCase = true) }
        }
        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        // Ensure UI reflects any state changes (like new likes) when returning to the feed
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}