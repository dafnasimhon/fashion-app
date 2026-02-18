package com.example.app_project.repository

import com.example.app_project.models.AppConfig
import com.example.app_project.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Creates a new user in Firebase Auth and then triggers the Firestore save process.
     */
    fun register(email: String, password: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val user = User(uid = uid, fullName = fullName, email = email)

                    // Proceed to save user profile data in Firestore after successful authentication
                    saveUserToFirestore(user, onResult)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Saves additional user information (like full name) to the 'users' collection in Firestore.
     */
    private fun saveUserToFirestore(user: User, onResult: (Boolean, String?) -> Unit) {
        db.collection(AppConfig.COLL_USERS).document(user.uid)
            .set(user)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    /**
     * Authenticates an existing user with email and password.
     */
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Checks if a user session is currently active.
     */
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Ends the current user session.
     */
    fun logout() {
        auth.signOut()
    }
}