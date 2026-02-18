package com.example.app_project

import android.os.Bundle
import com.example.app_project.adapters.OutfitAdapter
import com.example.app_project.models.AppConfig
import com.example.app_project.models.Outfit
import com.example.app_project.repository.OutfitRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MainActivity : BaseActivity() {

    private lateinit var adapter: OutfitAdapter
    private val outfitRepository = OutfitRepository()

    private var allOutfitsList: List<Outfit> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation(R.id.btn_home)
        setupRecyclerView()
        setupFilterChips()
        loadOutfitsFromFirebase()
    }

    private fun setupRecyclerView() {
        adapter = OutfitAdapter(
            outfits = emptyList(),
            showLikeButton = true
        ) { outfit ->
            navigateToDetail(outfit)
        }

        setupRecyclerView(R.id.main_RV_list, 2, adapter)
    }

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

        findViewById<Chip>(R.id.chip_all).setOnClickListener {
            filterOutfits(AppConfig.FILTER_ALL)
        }
    }

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

    private fun filterOutfits(vibe: String) {
        val filtered = if (vibe == AppConfig.FILTER_ALL) {
            allOutfitsList
        } else {
            allOutfitsList.filter { it.vibe.equals(vibe, ignoreCase = true) }
        }
        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}