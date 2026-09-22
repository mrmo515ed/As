package com.example.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

object FcmNotificationHelper {

    const val CHANNEL_NEW_EPISODES = "new_episodes_channel"
    const val CHANNEL_NAME = "تنبيهات الحلقات الجديدة"
    const val CHANNEL_DESC = "إشعارات فورية عند صدور حلقات جديدة من الأنمي ومتابعات الأوتاكو"
    const val TAG = "AnimeBlackFCM"

    /**
     * Initializes notification channels required for Android 8.0 (API 26) and above.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_NEW_EPISODES, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = 0xFF00F0FF.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel initialized: $CHANNEL_NEW_EPISODES")
        }
    }

    /**
     * Retrieves current FCM Registration Token.
     */
    fun getFcmToken(onResult: (String?) -> Unit) {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    onResult(null)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d(TAG, "Current FCM Token: $token")
                onResult(token)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obtaining FCM token", e)
            onResult(null)
        }
    }

    /**
     * Subscribes the device to global new episodes topic.
     */
    fun subscribeToNewEpisodesTopic(onComplete: (Boolean) -> Unit = {}) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("new_episodes")
                .addOnCompleteListener { task ->
                    val success = task.isSuccessful
                    Log.d(TAG, "Subscribed to topic 'new_episodes': $success")
                    onComplete(success)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to new_episodes topic", e)
            onComplete(false)
        }
    }

    /**
     * Unsubscribes the device from global new episodes topic.
     */
    fun unsubscribeFromNewEpisodesTopic(onComplete: (Boolean) -> Unit = {}) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("new_episodes")
                .addOnCompleteListener { task ->
                    val success = task.isSuccessful
                    Log.d(TAG, "Unsubscribed from topic 'new_episodes': $success")
                    onComplete(success)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from new_episodes topic", e)
            onComplete(false)
        }
    }

    /**
     * Subscribes to a specific anime's new episode notifications.
     */
    fun subscribeToAnimeEpisodes(animeId: String, onComplete: (Boolean) -> Unit = {}) {
        val topic = "anime_${animeId.replace(Regex("[^a-zA-Z0-9_-]"), "_")}"
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    Log.d(TAG, "Subscribed to anime topic '$topic': ${task.isSuccessful}")
                    onComplete(task.isSuccessful)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to anime topic $topic", e)
            onComplete(false)
        }
    }

    /**
     * Unsubscribes from a specific anime's new episode notifications.
     */
    fun unsubscribeFromAnimeEpisodes(animeId: String, onComplete: (Boolean) -> Unit = {}) {
        val topic = "anime_${animeId.replace(Regex("[^a-zA-Z0-9_-]"), "_")}"
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    Log.d(TAG, "Unsubscribed from anime topic '$topic': ${task.isSuccessful}")
                    onComplete(task.isSuccessful)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from anime topic $topic", e)
            onComplete(false)
        }
    }

    /**
     * Builds and displays a rich Native Push Notification for a new episode.
     */
    fun showEpisodeNotification(
        context: Context,
        animeTitle: String,
        episodeNumber: String,
        bannerUrl: String? = null,
        synopsis: String? = null
    ) {
        // Ensure channels are created
        createNotificationChannels(context)

        // Check POST_NOTIFICATIONS permission on Android 13+ (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Cannot show notification.")
                return
            }
        }

        val notificationId = (System.currentTimeMillis() % 100000).toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", "wiki")
            putExtra("anime_title", animeTitle)
            putExtra("episode_number", episodeNumber)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "🎬 صدرت حلقة جديدة: $animeTitle"
        val body = "تمت إضافة الحلقة رقم $episodeNumber الآن على أنمي بلاك! انقر للمشاهدة والتفاعل."

        CoroutineScope(Dispatchers.IO).launch {
            var bitmap: Bitmap? = null
            if (!bannerUrl.isNullOrBlank()) {
                try {
                    val url = URL(bannerUrl)
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e: Exception) {
                    Log.w(TAG, "Could not download notification banner image", e)
                }
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_NEW_EPISODES)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    if (bitmap != null) {
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .setBigContentTitle(title)
                            .setSummaryText(body)
                    } else {
                        NotificationCompat.BigTextStyle()
                            .bigText(if (!synopsis.isNullOrBlank()) "$body\n\nنبذة: $synopsis" else body)
                    }
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(0xFF00F0FF.toInt())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    android.R.drawable.ic_media_play,
                    "مشاهدة الحلقة الآن ▶️",
                    pendingIntent
                )

            try {
                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
                Log.d(TAG, "Episode notification displayed successfully for $animeTitle #$episodeNumber")
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException while posting notification", e)
            }
        }
    }
}
