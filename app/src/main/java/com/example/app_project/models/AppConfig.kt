package com.example.app_project.models

object AppConfig {
    const val COLL_USERS = "users"
    const val COLL_OUTFITS = "outfits"
    const val COLL_FAVORITES = "favorites"

    const val PATH_OUTFITS = "outfits"
    const val PATH_PROFILES = "profile_images"

    const val EXTRA_OUTFIT_ID = "OUTFIT_ID"
    const val EXTRA_IMAGE_URL = "IMAGE_URL"
    const val EXTRA_USER_ID = "USER_ID"
    const val EXTRA_FROM_PROFILE = "FROM_PROFILE"
    const val EXTRA_VIBE = "VIBE"
    const val EXTRA_TOP = "TOP"
    const val EXTRA_BOTTOM = "BOTTOM"
    const val EXTRA_JACKET = "JACKET"
    const val EXTRA_SHOES = "SHOES"
    const val EXTRA_JEWELRY = "JEWELRY"
    const val EXTRA_SUNGLASSES = "SUNGLASSES"
    const val EXTRA_BAG = "BAG"

    const val FIELD_VIBE = "vibe"
    const val FIELD_TOP = "top"
    const val FIELD_BOTTOM = "bottom"
    const val FIELD_JACKET = "jacket"
    const val FIELD_SHOES = "shoes"
    const val FIELD_JEWELRY = "jewelry"
    const val FIELD_SUNGLASSES = "sunglasses"
    const val FIELD_BAG = "bag"
    const val FIELD_IMAGE_URL = "imageUrl"
    const val FIELD_USER_ID = "userId"
    const val FIELD_TIMESTAMP = "timestamp"

    const val FILTER_ALL = "ALL"

    val REQUIRED_FIELDS = listOf(FIELD_TOP, FIELD_BOTTOM, FIELD_VIBE)
}