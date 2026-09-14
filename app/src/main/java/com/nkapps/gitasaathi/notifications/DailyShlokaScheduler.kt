package com.nkapps.gitasaathi.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaData
import java.util.Calendar

object DailyShlokaScheduler {

    const val CHANNEL_ID = "daily_shloka_channel"
    private const val PREF_KEY_NOTIF_ENABLED = "daily_shloka_notif_enabled"
    private const val PREFS_NAME = "gita_saathi_prefs"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Shloka Notifications"
            val descriptionText = "Daily spiritual quotes and verses from Srimad Bhagavad Gita"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_KEY_NOTIF_ENABLED, true)
    }

    fun setNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_KEY_NOTIF_ENABLED, enabled).apply()

        if (enabled) {
            scheduleDailyNotification(context)
        } else {
            cancelDailyNotification(context)
        }
    }

    fun scheduleDailyNotification(context: Context, hour: Int = 8, minute: Int = 0) {
        if (!isNotificationEnabled(context)) return

        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyShlokaReceiver::class.java).apply {
            action = DailyShlokaReceiver.ACTION_DAILY_SHLOKA
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DailyShlokaReceiver.DAILY_SHLOKA_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set target time (e.g., 8:00 AM)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            android.util.Log.w("DailyShlokaScheduler", "Failed to schedule alarm with AlarmManager", e)
        }
    }

    fun cancelDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyShlokaReceiver::class.java).apply {
            action = DailyShlokaReceiver.ACTION_DAILY_SHLOKA
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DailyShlokaReceiver.DAILY_SHLOKA_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showImmediateNotification(context: Context) {
        val intent = Intent(context, DailyShlokaReceiver::class.java).apply {
            action = DailyShlokaReceiver.ACTION_DAILY_SHLOKA
        }
        context.sendBroadcast(intent)
    }
}
