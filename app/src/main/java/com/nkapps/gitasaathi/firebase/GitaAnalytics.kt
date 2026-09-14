package com.nkapps.gitasaathi.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * GitaAnalytics safely logs spiritual events, user engagements, screen views,
 * and subscription activities to Google Analytics for Firebase.
 */
object GitaAnalytics {
    private const val TAG = "GitaAnalytics"
    private var analytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d(TAG, "Firebase not configured on device. Analytics running in local mode.")
                return
            }
            analytics = FirebaseAnalytics.getInstance(context)
            Log.d(TAG, "Firebase Analytics successfully initialized.")
        } catch (e: Exception) {
            Log.w(TAG, "Unable to initialize Firebase Analytics: ${e.message}")
        }
    }

    fun logScreen(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logChapterOpened(chapterNumber: Int, chapterName: String) {
        val bundle = Bundle().apply {
            putInt("chapter_number", chapterNumber)
            putString("chapter_name", chapterName)
        }
        logEvent("chapter_opened", bundle)
    }

    fun logShlokaRead(chapterNumber: Int, verseNumber: Int) {
        val bundle = Bundle().apply {
            putInt("chapter_number", chapterNumber)
            putInt("verse_number", verseNumber)
        }
        logEvent("shloka_read", bundle)
    }

    fun logAudioAction(action: String, chapter: Int, verse: Int) {
        val bundle = Bundle().apply {
            putString("action_type", action)
            putInt("chapter_number", chapter)
            putInt("verse_number", verse)
        }
        logEvent("audio_action", bundle)
    }

    fun logQuizCompleted(score: Int, total: Int) {
        val bundle = Bundle().apply {
            putInt("quiz_score", score)
            putInt("quiz_total", total)
            putDouble("quiz_accuracy", if (total > 0) (score.toDouble() / total) * 100 else 0.0)
        }
        logEvent("quiz_completed", bundle)
    }

    fun logGoldViewed() {
        logEvent("gold_screen_viewed", null)
    }

    fun logGoldPurchase(plan: String) {
        val bundle = Bundle().apply {
            putString("plan_type", plan)
        }
        logEvent("gold_purchased", bundle)
    }

    fun logEvent(eventName: String, bundle: Bundle? = null) {
        val fa = analytics
        if (fa == null) {
            Log.d(TAG, "[Event]: $eventName -> $bundle")
            return
        }
        try {
            fa.logEvent(eventName, bundle)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log event $eventName: ${e.message}")
        }
    }
}
