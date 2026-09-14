package com.nkapps.gitasaathi.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nkapps.gitasaathi.MainActivity
import com.nkapps.gitasaathi.R
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.notifications.DailyShlokaReceiver

class DailyShlokaWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val shloka = GitaData.getShlokaOfTheDay()
            val prefs = context.getSharedPreferences("gita_saathi_prefs", Context.MODE_PRIVATE)
            val isHindi = prefs.getString("app_language", AppLanguage.HINDI.name) == AppLanguage.HINDI.name

            val views = RemoteViews(context.packageName, R.layout.widget_daily_shloka)

            val title = if (isHindi) "गीता साथी • आज का श्लोक" else "Gita Saathi • Daily Shloka"
            val verseRef = "${shloka.chapterId}.${shloka.verseId}"
            val meaning = if (isHindi) shloka.translationHindi else shloka.translationEnglish

            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_verse_ref, verseRef)
            views.setTextViewText(R.id.widget_shloka_text, shloka.shlokaSanskrit)
            views.setTextViewText(R.id.widget_meaning_text, meaning)

            // Intent to open MainActivity directly to this shloka
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(DailyShlokaReceiver.EXTRA_OPEN_SHLOKA, true)
                putExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID, shloka.chapterId)
                putExtra(DailyShlokaReceiver.EXTRA_VERSE_ID, shloka.verseId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, DailyShlokaWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                for (id in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            } catch (_: Exception) {}
        }
    }
}
