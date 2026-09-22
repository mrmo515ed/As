package com.example.service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AnimeBlackMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        // Store locally in SharedPreferences
        val prefs = applicationContext.getSharedPreferences("anime_black_fcm", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()

        // Auto-subscribe to default new episode announcements
        FcmNotificationHelper.subscribeToNewEpisodesTopic()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        val data = remoteMessage.data
        var animeTitle = data["anime_title"]
        var episodeNumber = data["episode_number"]
        val bannerUrl = data["banner_url"] ?: remoteMessage.notification?.imageUrl?.toString()
        val synopsis = data["synopsis"]

        // If not found in data payload, fallback to notification payload
        remoteMessage.notification?.let {
            if (animeTitle.isNullOrBlank()) {
                animeTitle = it.title ?: "أنمي بلاك"
            }
            if (episodeNumber.isNullOrBlank()) {
                episodeNumber = "جديدة"
            }
        }

        if (animeTitle.isNullOrBlank()) {
            animeTitle = "أنمي مميز"
        }
        if (episodeNumber.isNullOrBlank()) {
            episodeNumber = "الحالية"
        }

        Log.d(TAG, "Processing push notification for anime: $animeTitle Episode: $episodeNumber")

        // Display the native push notification
        FcmNotificationHelper.showEpisodeNotification(
            context = applicationContext,
            animeTitle = animeTitle!!,
            episodeNumber = episodeNumber!!,
            bannerUrl = bannerUrl,
            synopsis = synopsis
        )
    }

    companion object {
        private const val TAG = "AnimeBlackFCMService"
    }
}
