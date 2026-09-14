package com.nkapps.gitasaathi.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nkapps.gitasaathi.BuildConfig

/**
 * GitaRemoteConfig manages dynamic configuration and remote parameters from the Firebase Console
 * without requiring app updates on Google Play Store.
 */
object GitaRemoteConfig {
    private const val TAG = "GitaRemoteConfig"

    // Parameter keys
    const val KEY_SHOW_FESTIVAL_BANNER = "show_festival_banner"
    const val KEY_FESTIVAL_BANNER_TITLE = "festival_banner_title"
    const val KEY_FESTIVAL_BANNER_SUBTITLE = "festival_banner_subtitle"
    const val KEY_DAILY_QUIZ_ENABLED = "daily_quiz_enabled"
    const val KEY_GOLD_PROMO_DISCOUNT = "gold_promo_discount"
    const val KEY_MIN_SUPPORTED_VERSION = "min_supported_version"

    // Default values
    private val DEFAULTS: Map<String, Any> = mapOf(
        KEY_SHOW_FESTIVAL_BANNER to false,
        KEY_FESTIVAL_BANNER_TITLE to "🌸 पावन गीता ज्ञान",
        KEY_FESTIVAL_BANNER_SUBTITLE to "श्री कृष्ण के उपदेशों से अपने जीवन को आलोकित करें",
        KEY_DAILY_QUIZ_ENABLED to true,
        KEY_GOLD_PROMO_DISCOUNT to "30%",
        KEY_MIN_SUPPORTED_VERSION to 1L
    )

    private var remoteConfig: FirebaseRemoteConfig? = null

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d(TAG, "Firebase not configured. Remote Config using in-memory defaults.")
                return
            }

            val config = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
                .build()

            config.setConfigSettingsAsync(configSettings)
            config.setDefaultsAsync(DEFAULTS)

            config.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Remote config fetched and activated successfully. Updated: ${task.result}")
                    } else {
                        Log.w(TAG, "Remote config fetch failed: ${task.exception?.message}")
                    }
                }

            remoteConfig = config
            Log.d(TAG, "GitaRemoteConfig initialized.")
        } catch (e: Exception) {
            Log.w(TAG, "Unable to initialize Remote Config: ${e.message}")
        }
    }

    fun isFestivalBannerEnabled(): Boolean {
        return remoteConfig?.getBoolean(KEY_SHOW_FESTIVAL_BANNER)
            ?: (DEFAULTS[KEY_SHOW_FESTIVAL_BANNER] as Boolean)
    }

    fun getFestivalBannerTitle(): String {
        return remoteConfig?.getString(KEY_FESTIVAL_BANNER_TITLE)
            ?.takeIf { it.isNotEmpty() }
            ?: (DEFAULTS[KEY_FESTIVAL_BANNER_TITLE] as String)
    }

    fun getFestivalBannerSubtitle(): String {
        return remoteConfig?.getString(KEY_FESTIVAL_BANNER_SUBTITLE)
            ?.takeIf { it.isNotEmpty() }
            ?: (DEFAULTS[KEY_FESTIVAL_BANNER_SUBTITLE] as String)
    }

    fun isDailyQuizEnabled(): Boolean {
        return remoteConfig?.getBoolean(KEY_DAILY_QUIZ_ENABLED)
            ?: (DEFAULTS[KEY_DAILY_QUIZ_ENABLED] as Boolean)
    }

    fun getGoldPromoDiscount(): String {
        return remoteConfig?.getString(KEY_GOLD_PROMO_DISCOUNT)
            ?.takeIf { it.isNotEmpty() }
            ?: (DEFAULTS[KEY_GOLD_PROMO_DISCOUNT] as String)
    }

    fun getMinSupportedVersion(): Long {
        return remoteConfig?.getLong(KEY_MIN_SUPPORTED_VERSION)
            ?: (DEFAULTS[KEY_MIN_SUPPORTED_VERSION] as Long)
    }
}
