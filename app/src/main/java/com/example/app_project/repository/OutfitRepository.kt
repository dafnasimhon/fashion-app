package com.example.app_project.repository

import android.net.Uri
import com.example.app_project.models.AppConfig
import com.example.app_project.models.Outfit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class OutfitRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String? get() = auth.currentUser?.uid

    fun uploadOutfit(
        imageUri: Uri, top: String, bottom: String, jacket: String,
        shoes: String, jewelry: String, sunglasses: String, bag: String,
        vibe: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = currentUserId ?: return onResult(false, "User not logged in")
        val outfitId = UUID.randomUUID().toString()

        val fileRef = storage.reference.child("${AppConfig.PATH_OUTFITS}/$outfitId.jpg")

        fileRef.putFile(imageUri).continueWithTask {
            fileRef.downloadUrl
        }.addOnSuccessListener { uri ->
            val outfit = Outfit(
                id = outfitId,
                userId = userId,
                imageUrl = uri.toString(),
                timestamp = System.currentTimeMillis(),
                top = top,
                bottom = bottom,
                jacket = jacket,
                shoes = shoes,
                jewelry = jewelry,
                sunglasses = sunglasses,
                bag = bag,
                vibe = vibe
            )
            saveOutfitToFirestore(outfit, onResult)
        }.addOnFailureListener { onResult(false, it.message) }
    }

    private fun saveOutfitToFirestore(outfit: Outfit, onResult: (Boolean, String?) -> Unit) {
        db.collection(AppConfig.COLL_OUTFITS).document(outfit.id).set(outfit)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun deleteOutfit(outfitId: String, imageUrl: String, onResult: (Boolean) -> Unit) {
        db.collection(AppConfig.COLL_OUTFITS).document(outfitId).delete()
            .addOnSuccessListener {
                try {
                    val imageRef = storage.getReferenceFromUrl(imageUrl)
                    imageRef.delete()
                        .addOnSuccessListener { onResult(true) }
                        .addOnFailureListener { onResult(false) }
                } catch (e: Exception) {
                    onResult(true)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun getAllOutfits(onResult: (List<Outfit>?, String?) -> Unit) {
        db.collection(AppConfig.COLL_OUTFITS)
            .orderBy(AppConfig.FIELD_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener onResult(null, error.message)

                val list = value?.toObjects(Outfit::class.java) ?: emptyList()
                onResult(list.filter { it.userId != currentUserId }, null)
            }
    }

    fun getMyOutfits(onResult: (List<Outfit>?, String?) -> Unit) {
        val userId = currentUserId ?: return onResult(null, "User not logged in")

        db.collection(AppConfig.COLL_OUTFITS)
            .whereEqualTo(AppConfig.FIELD_USER_ID, userId)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener onResult(null, error.message)

                val list = value?.toObjects(Outfit::class.java) ?: emptyList()
                val sortedList = list.sortedByDescending { it.timestamp }
                onResult(sortedList, null)
            }
    }

    fun toggleLike(outfitId: String, isLiked: Boolean, onResult: (Boolean) -> Unit) {
        val userId = currentUserId ?: return onResult(false)

        val favRef = db.collection(AppConfig.COLL_USERS)
            .document(userId)
            .collection(AppConfig.COLL_FAVORITES)
            .document(outfitId)

        val task = if (isLiked) favRef.set(mapOf("likedAt" to System.currentTimeMillis())) else favRef.delete()
        task.addOnSuccessListener { onResult(true) }.addOnFailureListener { onResult(false) }
    }

    fun getFavoriteOutfits(onResult: (List<Outfit>?, String?) -> Unit) {
        val userId = currentUserId ?: return onResult(null, "User not logged in")

        db.collection(AppConfig.COLL_USERS)
            .document(userId)
            .collection(AppConfig.COLL_FAVORITES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener onResult(null, error.message)
                val favIds = snapshot?.documents?.map { it.id } ?: emptyList()
                if (favIds.isEmpty()) return@addSnapshotListener onResult(emptyList(), null)

                db.collection(AppConfig.COLL_OUTFITS)
                    .whereIn(FieldPath.documentId(), favIds)
                    .get()
                    .addOnSuccessListener { onResult(it.toObjects(Outfit::class.java), null) }
                    .addOnFailureListener { onResult(null, it.message) }
            }
    }

    fun isOutfitLiked(outfitId: String, onResult: (Boolean) -> Unit) {
        val userId = currentUserId ?: return onResult(false)
        db.collection(AppConfig.COLL_USERS)
            .document(userId)
            .collection(AppConfig.COLL_FAVORITES)
            .document(outfitId).get()
            .addOnSuccessListener { onResult(it.exists()) }
            .addOnFailureListener { onResult(false) }
    }

    fun uploadProfileImage(imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        val userId = currentUserId ?: return onResult(false, "User not logged in")
        val fileRef = storage.reference.child("${AppConfig.PATH_PROFILES}/$userId.jpg")

        fileRef.putFile(imageUri).continueWithTask { fileRef.downloadUrl }.addOnSuccessListener { uri ->
            db.collection(AppConfig.COLL_USERS).document(userId).update("profileImageUrl", uri.toString())
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, it.message) }
        }.addOnFailureListener { onResult(false, it.message) }
    }
}