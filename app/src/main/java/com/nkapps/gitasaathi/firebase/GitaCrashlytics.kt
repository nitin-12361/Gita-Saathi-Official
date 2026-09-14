package com.nkapps.gitasaathi.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * GitaCrashlytics provides safe, crash-proof integration with Firebase Crashlytics.
 * Captures real-time stack traces, uncaught exceptions, and custom breadcrumbs
 * without causing any overhead or issues when Firebase is not configured.
 */
object GitaCrashlytics {
    private const val TAG = "GitaCrashlytics"
    private var isInitialized = false

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d(TAG, "Firebase not configured on device. Crashlytics running in local mode.")
                return
            }
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
            isInitialized = true
            Log.d(TAG, "Firebase Crashlytics successfully initialized.")
        } catch (e: Exception) {
            Log.w(TAG, "Unable to initialize Firebase Crashlytics: ${e.message}")
        }
    }

    fun log(message: String) {
        if (!isInitialized) {
            Log.d(TAG, "[Breadcrumb]: $message")
            return
        }
        try {
            FirebaseCrashlytics.getInstance().log(message)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log message to Crashlytics: ${e.message}")
        }
    }

    fun recordException(throwable: Throwable) {
        if (!isInitialized) {
            Log.e(TAG, "Exception recorded locally: ${throwable.message}", throwable)
            return
        }
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to record exception in Crashlytics: ${e.message}")
        }
    }

    fun setUserId(userId: String) {
        if (!isInitialized) return
        try {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set user ID: ${e.message}")
        }
    }

    fun setCustomKey(key: String, value: String) {
        if (!isInitialized) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set custom key: ${e.message}")
        }
    }

    fun setCustomKey(key: String, value: Boolean) {
        if (!isInitialized) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set custom key: ${e.message}")
        }
    }

    fun setCustomKey(key: String, value: Int) {
        if (!isInitialized) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set custom key: ${e.message}")
        }
    }
}
