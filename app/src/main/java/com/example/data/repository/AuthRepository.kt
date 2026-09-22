package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val animeRepo: AnimeRepository,
    private val context: Context? = null
) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val TAG = "AuthRepository"

    init {
        // Real-time Auth state listener
        try {
            auth.addAuthStateListener { firebaseAuth ->
                val fbUser = firebaseAuth.currentUser
                if (fbUser != null) {
                    syncFirebaseUser(fbUser)
                } else {
                    animeRepo.setUser(null)
                }
            }
            // Initial check
            auth.currentUser?.let { syncFirebaseUser(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseAuth listener", e)
        }
    }

    private fun syncFirebaseUser(fbUser: FirebaseUser) {
        val isOwner = fbUser.email.equals("m774545471@gmail.com", ignoreCase = true)
        val defaultName = fbUser.displayName ?: if (isOwner) "مدير أنمي بلاك" else (fbUser.email?.substringBefore("@") ?: "أوتاكو بلاك")
        val defaultUsername = "@${fbUser.email?.substringBefore("@") ?: "otaku"}"

        animeRepo.firestoreManager.getUserProfile(fbUser.uid) { existingUser ->
            val user = if (existingUser != null) {
                if (isOwner && existingUser.role != "Owner") {
                    existingUser.copy(role = "Owner", isVerified = true)
                } else {
                    existingUser
                }
            } else {
                User(
                    id = fbUser.uid,
                    name = defaultName,
                    username = defaultUsername,
                    email = fbUser.email ?: "",
                    avatar = fbUser.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                    role = if (isOwner) "Owner" else "عضو",
                    level = if (isOwner) 99 else 1,
                    coins = if (isOwner) 99999 else 380,
                    stars = if (isOwner) 999 else 16,
                    isVerified = isOwner
                )
            }
            animeRepo.setUser(user)
            animeRepo.firestoreManager.saveUserProfile(user)
        }
    }

    suspend fun loginWithEmail(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val fbUser = authResult.user ?: throw Exception("تعذر استرجاع بيانات المستخدم")
            val isOwner = email.equals("m774545471@gmail.com", ignoreCase = true)
            val user = User(
                id = fbUser.uid,
                name = fbUser.displayName ?: if (isOwner) "مدير أنمي بلاك" else email.substringBefore("@"),
                username = "@${email.substringBefore("@")}",
                email = email,
                avatar = fbUser.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                role = if (isOwner) "Owner" else "عضو",
                isVerified = isOwner,
                level = if (isOwner) 99 else 1,
                coins = if (isOwner) 99999 else 380,
                stars = if (isOwner) 999 else 16
            )
            animeRepo.setUser(user)
            animeRepo.firestoreManager.saveUserProfile(user)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(name: String, email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val fbUser = authResult.user ?: throw Exception("تعذر إنشاء الحساب")
            val isOwner = email.equals("m774545471@gmail.com", ignoreCase = true)
            val user = User(
                id = fbUser.uid,
                name = name.ifBlank { if (isOwner) "مدير أنمي بلاك" else email.substringBefore("@") },
                username = "@${email.substringBefore("@")}",
                email = email,
                role = if (isOwner) "Owner" else "عضو",
                isVerified = isOwner,
                level = if (isOwner) 99 else 1,
                coins = if (isOwner) 99999 else 500,
                stars = if (isOwner) 999 else 10
            )
            animeRepo.setUser(user)
            animeRepo.firestoreManager.saveUserProfile(user)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Logout error", e)
        }
        animeRepo.setUser(null)
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser
}
