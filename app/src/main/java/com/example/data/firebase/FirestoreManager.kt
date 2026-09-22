package com.example.data.firebase

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class FirestoreManager {

    private val db by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firestore", e)
            null
        }
    }

    private var postsListener: ListenerRegistration? = null
    private val chatListeners = mutableMapOf<String, ListenerRegistration>()
    private val commentsListeners = mutableMapOf<String, ListenerRegistration>()
    private var notifListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null
    private var favoritesListener: ListenerRegistration? = null

    /**
     * Realtime listener for community posts.
     */
    fun startPostsListener(currentUserId: String? = null, onPostsUpdated: (List<Post>) -> Unit) {
        val firestore = db ?: return
        try {
            postsListener?.remove()
            postsListener = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Posts listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                val tagsList = (doc.get("tags") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                                val likedByList = (doc.get("likedUserIds") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                                val isLikedByMe = currentUserId != null && likedByList.contains(currentUserId)

                                Post(
                                    id = doc.id,
                                    authorId = doc.getString("authorId") ?: "u_guest",
                                    authorName = doc.getString("authorName") ?: "أوتاكو مجهول",
                                    authorAvatar = doc.getString("authorAvatar") ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                                    authorRole = doc.getString("authorRole") ?: "عضو",
                                    isVerified = doc.getBoolean("isVerified") ?: false,
                                    text = doc.getString("text") ?: "",
                                    mediaUrl = doc.getString("mediaUrl"),
                                    tags = tagsList,
                                    likesCount = doc.getLong("likesCount")?.toInt() ?: likedByList.size,
                                    commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0,
                                    sharesCount = doc.getLong("sharesCount")?.toInt() ?: 0,
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                    isLiked = isLikedByMe,
                                    animeRef = doc.getString("animeRef")
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (posts.isNotEmpty()) {
                            onPostsUpdated(posts)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start posts listener", e)
        }
    }

    /**
     * Publishes a new post to Firestore.
     */
    fun publishPost(post: Post) {
        val firestore = db ?: return
        val postMap = hashMapOf(
            "authorId" to post.authorId,
            "authorName" to post.authorName,
            "authorAvatar" to post.authorAvatar,
            "authorRole" to post.authorRole,
            "isVerified" to post.isVerified,
            "text" to post.text,
            "mediaUrl" to post.mediaUrl,
            "tags" to post.tags,
            "likesCount" to post.likesCount,
            "likedUserIds" to emptyList<String>(),
            "commentsCount" to post.commentsCount,
            "sharesCount" to post.sharesCount,
            "createdAt" to post.createdAt,
            "animeRef" to post.animeRef
        )

        firestore.collection("posts").document(post.id).set(postMap)
            .addOnSuccessListener {
                Log.d(TAG, "Post successfully published to Firestore: ${post.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing post to Firestore", e)
            }
    }

    /**
     * Toggles like atomically with user tracking.
     */
    fun togglePostLike(postId: String, userId: String, isCurrentlyLiked: Boolean, currentLikesCount: Int) {
        val firestore = db ?: return
        val postRef = firestore.collection("posts").document(postId)
        if (isCurrentlyLiked) {
            // Unlike
            postRef.update(
                "likedUserIds", FieldValue.arrayRemove(userId),
                "likesCount", maxOf(0, currentLikesCount - 1)
            )
        } else {
            // Like
            postRef.update(
                "likedUserIds", FieldValue.arrayUnion(userId),
                "likesCount", currentLikesCount + 1
            )
        }
    }

    /**
     * Publishes comment to Firestore subcollection.
     */
    fun publishComment(postId: String, comment: Comment) {
        val firestore = db ?: return
        val commentMap = hashMapOf(
            "id" to comment.id,
            "postId" to comment.postId,
            "authorId" to comment.authorId,
            "authorName" to comment.authorName,
            "authorAvatar" to comment.authorAvatar,
            "text" to comment.text,
            "createdAt" to comment.createdAt,
            "likesCount" to comment.likesCount
        )

        firestore.collection("posts").document(postId)
            .collection("comments").document(comment.id).set(commentMap)
            .addOnSuccessListener {
                // Increment commentsCount atomically
                firestore.collection("posts").document(postId)
                    .update("commentsCount", FieldValue.increment(1))
            }
            .addOnFailureListener { Log.w(TAG, "Error adding comment to Firestore", it) }
    }

    /**
     * Realtime listener for post comments.
     */
    fun startCommentsListener(postId: String, onCommentsUpdated: (List<Comment>) -> Unit) {
        val firestore = db ?: return
        try {
            commentsListeners[postId]?.remove()
            val listener = firestore.collection("posts").document(postId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null) {
                        val comments = snapshot.documents.mapNotNull { doc ->
                            try {
                                Comment(
                                    id = doc.id,
                                    postId = postId,
                                    authorId = doc.getString("authorId") ?: "",
                                    authorName = doc.getString("authorName") ?: "أوتاكو",
                                    authorAvatar = doc.getString("authorAvatar") ?: "",
                                    text = doc.getString("text") ?: "",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                    likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onCommentsUpdated(comments)
                    }
                }
            commentsListeners[postId] = listener
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start comments listener for $postId", e)
        }
    }

    /**
     * Listens to messages in a specific chat room or direct chat with proper isMe identification.
     */
    fun startChatListener(chatId: String, currentUserId: String?, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        val firestore = db ?: return
        try {
            chatListeners[chatId]?.remove()
            val listener = firestore.collection("chats").document(chatId).collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(100)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Chat listen failed for $chatId", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val messages = snapshot.documents.mapNotNull { doc ->
                            try {
                                val sId = doc.getString("senderId") ?: ""
                                ChatMessage(
                                    id = doc.id,
                                    senderId = sId,
                                    senderName = doc.getString("senderName") ?: "أوتاكو",
                                    senderAvatar = doc.getString("senderAvatar") ?: "",
                                    text = doc.getString("text") ?: "",
                                    mediaUrl = doc.getString("mediaUrl"),
                                    type = doc.getString("type") ?: "text",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                    isMe = (currentUserId != null && sId == currentUserId),
                                    isRead = doc.getBoolean("isRead") ?: true
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onMessagesUpdated(messages)
                    }
                }
            chatListeners[chatId] = listener
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start chat listener for $chatId", e)
        }
    }

    /**
     * Sends a chat message to Firestore and updates the chat document last message.
     */
    fun sendChatMessage(chatId: String, message: ChatMessage) {
        val firestore = db ?: return
        val msgMap = hashMapOf(
            "senderId" to message.senderId,
            "senderName" to message.senderName,
            "senderAvatar" to message.senderAvatar,
            "text" to message.text,
            "mediaUrl" to message.mediaUrl,
            "type" to message.type,
            "createdAt" to message.createdAt,
            "isRead" to false
        )

        firestore.collection("chats").document(chatId)
            .collection("messages").document(message.id).set(msgMap)
            .addOnSuccessListener {
                // Update parent conversation
                firestore.collection("chats").document(chatId).set(
                    hashMapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.createdAt
                    ),
                    SetOptions.merge()
                )
            }
            .addOnFailureListener { Log.w(TAG, "Error writing chat message to Firestore", it) }
    }

    /**
     * Real-time listener for global and user-directed notifications.
     */
    fun startNotificationsListener(userId: String? = null, onNotifsUpdated: (List<NotificationItem>) -> Unit) {
        val firestore = db ?: return
        try {
            notifListener?.remove()
            val query = firestore.collection("notifications")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(30)

            notifListener = query.addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            val targetUserId = doc.getString("targetUserId")
                            if (targetUserId == null || targetUserId == "all" || targetUserId == userId) {
                                NotificationItem(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "إشعار",
                                    body = doc.getString("body") ?: "",
                                    type = doc.getString("type") ?: "system",
                                    timeAgo = doc.getString("timeAgo") ?: "الآن",
                                    isRead = doc.getBoolean("isRead") ?: false,
                                    iconName = doc.getString("iconName") ?: "bell"
                                )
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (items.isNotEmpty()) {
                        onNotifsUpdated(items)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start notifications listener", e)
        }
    }

    /**
     * Publishes notification to Firestore.
     */
    fun publishNotification(notification: NotificationItem, targetUserId: String = "all") {
        val firestore = db ?: return
        val notifMap = hashMapOf(
            "title" to notification.title,
            "body" to notification.body,
            "type" to notification.type,
            "timeAgo" to notification.timeAgo,
            "createdAt" to System.currentTimeMillis(),
            "targetUserId" to targetUserId,
            "isRead" to notification.isRead,
            "iconName" to notification.iconName
        )
        firestore.collection("notifications").document(notification.id).set(notifMap)
            .addOnFailureListener { Log.w(TAG, "Failed to publish notification to Firestore", it) }
    }

    /**
     * Retrieves user profile from Firestore.
     */
    fun getUserProfile(userId: String, onResult: (User?) -> Unit) {
        val firestore = db ?: run {
            onResult(null)
            return
        }
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    try {
                        val badgesList = (doc.get("badges") as? List<*>)?.mapNotNull { it?.toString() } ?: listOf("عضو")
                        val user = User(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            username = doc.getString("username") ?: "",
                            email = doc.getString("email") ?: "",
                            avatar = doc.getString("avatar") ?: "",
                            bio = doc.getString("bio") ?: "",
                            role = doc.getString("role") ?: "عضو",
                            level = doc.getLong("level")?.toInt() ?: 1,
                            xp = doc.getLong("xp")?.toInt() ?: 0,
                            coins = doc.getLong("coins")?.toInt() ?: 380,
                            stars = doc.getLong("stars")?.toInt() ?: 16,
                            reputation = doc.getLong("reputation")?.toInt() ?: 100,
                            isVerified = doc.getBoolean("isVerified") ?: false,
                            frame = doc.getString("frame") ?: "fire_aura",
                            title = doc.getString("title") ?: "أوتاكو مبتدئ",
                            badges = badgesList
                        )
                        onResult(user)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse user", e)
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error fetching user $userId", it)
                onResult(null)
            }
    }

    /**
     * Saves user profile state to Firestore.
     */
    fun saveUserProfile(user: User) {
        val firestore = db ?: return
        val userMap = hashMapOf(
            "name" to user.name,
            "username" to user.username,
            "email" to user.email,
            "avatar" to user.avatar,
            "bio" to user.bio,
            "role" to user.role,
            "level" to user.level,
            "xp" to user.xp,
            "coins" to user.coins,
            "stars" to user.stars,
            "reputation" to user.reputation,
            "isVerified" to user.isVerified,
            "frame" to user.frame,
            "title" to user.title,
            "badges" to user.badges,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(user.id).set(userMap, SetOptions.merge())
            .addOnFailureListener { Log.w(TAG, "Failed to sync user to Firestore", it) }
    }

    /**
     * Realtime listener for user favorites stored in Firestore.
     */
    fun startFavoritesListener(userId: String, onFavoritesUpdated: (List<FavoriteItem>) -> Unit) {
        val firestore = db ?: return
        try {
            favoritesListener?.remove()
            favoritesListener = firestore.collection("users").document(userId)
                .collection("favorites")
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Favorites listen failed for $userId", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val favorites = snapshot.documents.mapNotNull { doc ->
                            try {
                                FavoriteItem(
                                    id = doc.id,
                                    userId = doc.getString("userId") ?: userId,
                                    targetId = doc.getString("targetId") ?: "",
                                    titleAr = doc.getString("titleAr") ?: "",
                                    titleEn = doc.getString("titleEn") ?: "",
                                    type = doc.getString("type") ?: "anime",
                                    bannerUrl = doc.getString("bannerUrl") ?: "",
                                    rating = doc.getDouble("rating") ?: 9.0,
                                    genre = doc.getString("genre") ?: "",
                                    status = doc.getString("status") ?: "مستمر",
                                    addedAt = doc.getLong("addedAt") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onFavoritesUpdated(favorites)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start favorites listener for $userId", e)
        }
    }

    /**
     * Saves a favorite item to Firestore under users/{userId}/favorites/{targetId}
     */
    fun saveFavorite(userId: String, favorite: FavoriteItem) {
        val firestore = db ?: return
        val map = hashMapOf(
            "userId" to userId,
            "targetId" to favorite.targetId,
            "titleAr" to favorite.titleAr,
            "titleEn" to favorite.titleEn,
            "type" to favorite.type,
            "bannerUrl" to favorite.bannerUrl,
            "rating" to favorite.rating,
            "genre" to favorite.genre,
            "status" to favorite.status,
            "addedAt" to favorite.addedAt
        )
        firestore.collection("users").document(userId)
            .collection("favorites").document(favorite.targetId).set(map, SetOptions.merge())
            .addOnFailureListener { Log.w(TAG, "Failed to save favorite to Firestore", it) }
    }

    /**
     * Deletes a favorite item from Firestore under users/{userId}/favorites/{targetId}
     */
    fun deleteFavorite(userId: String, targetId: String) {
        val firestore = db ?: return
        firestore.collection("users").document(userId)
            .collection("favorites").document(targetId).delete()
            .addOnFailureListener { Log.w(TAG, "Failed to delete favorite from Firestore", it) }
    }

    fun stopAllChatListeners() {
        chatListeners.values.forEach { it.remove() }
        chatListeners.clear()
    }

    fun stopNotificationsListener() {
        notifListener?.remove()
        notifListener = null
    }

    fun stopFavoritesListener() {
        favoritesListener?.remove()
        favoritesListener = null
    }

    fun cleanup() {
        postsListener?.remove()
        postsListener = null
        chatListeners.values.forEach { it.remove() }
        chatListeners.clear()
        commentsListeners.values.forEach { it.remove() }
        commentsListeners.clear()
        notifListener?.remove()
        notifListener = null
        userListener?.remove()
        userListener = null
        favoritesListener?.remove()
        favoritesListener = null
    }

    companion object {
        private const val TAG = "FirestoreManager"
    }
}
