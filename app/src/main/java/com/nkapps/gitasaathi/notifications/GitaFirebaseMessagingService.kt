package com.nkapps.gitasaathi.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nkapps.gitasaathi.MainActivity
import com.nkapps.gitasaathi.R

/**
 * GitaFirebaseMessagingService handles incoming Firebase Cloud Messaging (FCM) push notifications
 * for daily shlokas, festival greetings, and spiritual updates.
 */
class GitaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "🌸 पावन गीता ज्ञान"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "श्रीमद्भगवद्गीता का आज का दिव्य संदेश पढ़ें।"

        val chapter = remoteMessage.data["chapter"]?.toIntOrNull()
        val verse = remoteMessage.data["verse"]?.toIntOrNull()

        sendNotification(title, body, chapter, verse)
    }

    @Suppress("DEPRECATION")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token: $token")
        // Token can be saved to Firestore or SharedPreferences if individual targeting is needed.
        val prefs = getSharedPreferences("gita_saathi_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }

    private fun sendNotification(title: String, messageBody: String, chapter: Int?, verse: Int?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (chapter != null) {
                putExtra(DailyShlokaReceiver.EXTRA_OPEN_SHLOKA, true)
                putExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID, chapter)
                putExtra("target_chapter", chapter)
                if (verse != null) {
                    putExtra(DailyShlokaReceiver.EXTRA_VERSE_ID, verse)
                    putExtra("target_verse", verse)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, DailyShlokaScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        DailyShlokaScheduler.createNotificationChannel(this)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "GitaFCM"
        private const val NOTIFICATION_ID = 1008
        const val TOPIC_DAILY_SHLOKA = "daily_shloka"

        /**
         * Automatically subscribes the app to the "daily_shloka" broadcast topic.
         * Allows sending notifications to all users from the Firebase Console without individual tokens.
         */
        fun initSubscription(context: Context) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    Log.d(TAG, "Firebase not configured. Skipping FCM topic subscription.")
                    return
                }
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_DAILY_SHLOKA)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Successfully subscribed to topic: $TOPIC_DAILY_SHLOKA")
                        } else {
                            Log.w(TAG, "Failed to subscribe to topic: $TOPIC_DAILY_SHLOKA", task.exception)
                        }
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Error in initSubscription: ${e.message}")
            }
        }
    }
}
