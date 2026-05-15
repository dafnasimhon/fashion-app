package com.example.app_project.models
/**
 * Data model representing a single Outfit entity within the application.
 */
data class Outfit(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val top: String = "",
    val bottom: String = "",
    val jacket: String = "",
    val shoes: String = "",
    val jewelry: String = "",
    val sunglasses: String = "",
    val bag: String = "",
    val vibe: String = ""
)