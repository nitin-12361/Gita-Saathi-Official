package com.nkapps.gitasaathi.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.nkapps.gitasaathi.BuildConfig

/**
 * GitaAppCheck protects backend resources and Firestore against bots, scraping,
 * and unauthorized modifications using Google Play Integrity in production
 * and DebugAppCheckProviderFactory during local development.
 */
object GitaAppCheck {
    private const val TAG = "GitaAppCheck"

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d(TAG, "Firebase not configured. App Check skipped.")
                return
            }

            val appCheck = FirebaseAppCheck.getInstance()
            if (BuildConfig.DEBUG) {
                try {
                    val debugClass = Class.forName("com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory")
                    val getInstanceMethod = debugClass.getMethod("getInstance")
                    val factory = getInstanceMethod.invoke(null) as com.google.firebase.appcheck.AppCheckProviderFactory
                    appCheck.installAppCheckProviderFactory(factory)
                    Log.d(TAG, "Firebase App Check initialized with DebugAppCheckProviderFactory.")
                } catch (e: Exception) {
                    Log.w(TAG, "DebugAppCheckProviderFactory not found, falling back: ${e.message}")
                }
            } else {
                appCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "Firebase App Check initialized with PlayIntegrityAppCheckProviderFactory.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to initialize Firebase App Check: ${e.message}")
        }
    }
}
