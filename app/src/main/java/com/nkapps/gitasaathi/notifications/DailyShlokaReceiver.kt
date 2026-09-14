package com.nkapps.gitasaathi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nkapps.gitasaathi.MainActivity
import com.nkapps.gitasaathi.R
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaData

class DailyShlokaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != ACTION_DAILY_SHLOKA) {
            return
        }
        
        // Ensure channel exists
        DailyShlokaScheduler.createNotificationChannel(context)

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Device rebooted, reschedule alarm
            DailyShlokaScheduler.scheduleDailyNotification(context)
            return
        }

        // Show Daily Shloka Notification
        val shloka = GitaData.getShlokaOfTheDay()
        
        // Retrieve app language setting from SharedPrefs
        val prefs = context.getSharedPreferences("gita_saathi_prefs", Context.MODE_PRIVATE)
        val isHindi = prefs.getString("app_language", AppLanguage.ENGLISH.name) == AppLanguage.HINDI.name

        val title = if (isHindi) "ॐ श्रीमद्भगवद्गीता • आज का श्लोक" else "ॐ Srimad Bhagavad Gita • Daily Shloka"
        
        // Smartly truncate subtitle to avoid cutting words
        val rawSanskrit = shloka.shlokaSanskrit.replace("\n", " ").trim()
        val truncatedSanskrit = if (rawSanskrit.length > 60) {
            val lastSpace = rawSanskrit.take(60).lastIndexOfAny(listOf(" ", "।", "॥"))
            if (lastSpace > 40) rawSanskrit.take(lastSpace) + "..." else rawSanskrit.take(60) + "..."
        } else {
            rawSanskrit
        }
        
        val verseRef = if (isHindi) "अध्याय ${shloka.chapterId}, श्लोक ${shloka.verseId}" else "Chapter ${shloka.chapterId}, Verse ${shloka.verseId}"
        val subtitle = "$verseRef: $truncatedSanskrit"
        val bodyText = if (isHindi) {
            "$verseRef\n${shloka.shlokaSanskrit}\n\nअनुवाद: ${shloka.translationHindi}"
        } else {
            "$verseRef\n${shloka.shlokaSanskrit}\n\nTranslation: ${shloka.translationEnglish}"
        }

        // Intent to open MainActivity and show Shloka of the Day
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_SHLOKA, true)
            putExtra(EXTRA_CHAPTER_ID, shloka.chapterId)
            putExtra(EXTRA_VERSE_ID, shloka.verseId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            DAILY_SHLOKA_NOTIFICATION_ID,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, DailyShlokaScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(DAILY_SHLOKA_NOTIFICATION_ID, notificationBuilder.build())

        // Reschedule for tomorrow morning
        DailyShlokaScheduler.scheduleDailyNotification(context)
    }

    companion object {
        const val DAILY_SHLOKA_NOTIFICATION_ID = 1008
        const val EXTRA_OPEN_SHLOKA = "extra_open_shloka"
        const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        const val EXTRA_VERSE_ID = "extra_verse_id"
        const val ACTION_DAILY_SHLOKA = "com.nkapps.gitasaathi.ACTION_DAILY_SHLOKA"
    }
}
