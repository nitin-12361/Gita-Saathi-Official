package com.nkapps.gitasaathi.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * GitaAdManager handles AdMob advertisement lifecycle and user premium (Gold / Ad-Free) state.
 * Uses official Google Sample Test Ad IDs during development.
 */
object GitaAdManager {
    private const val TAG = "GitaAdManager"
    private const val PREFS_NAME = "gita_subscription_prefs"
    private const val KEY_IS_GOLD_MEMBER = "is_gold_member"

    // TEMPORARY MASTER TOGGLE: Set to false to disable all ads (banners & interstitials) across the app
    const val ADS_ENABLED = false

    // Official Google AdMob Sample Test Ad Unit IDs
    const val TEST_BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"

    // Minimum cooldown between interstitial full-screen ads (5 minutes = 300,000 ms)
    private const val INTERSTITIAL_COOLDOWN_MS = 300_000L
    private var lastInterstitialShownTime: Long = 0L

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    private val _isPremiumUser = MutableStateFlow(false)
    val isPremiumUser: StateFlow<Boolean> = _isPremiumUser.asStateFlow()

    fun init(context: Context) {
        val prefs = getPrefs(context)
        _isPremiumUser.value = prefs.getBoolean(KEY_IS_GOLD_MEMBER, false)
        if (ADS_ENABLED && !_isPremiumUser.value) {
            loadInterstitial(context.applicationContext)
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Toggles or sets user premium (Gold) status.
     * When true, ads are instantly hidden and interstitials are bypassed.
     */
    fun setPremiumStatus(context: Context, isPremium: Boolean) {
        _isPremiumUser.value = isPremium
        getPrefs(context).edit().putBoolean(KEY_IS_GOLD_MEMBER, isPremium).apply()
        if (isPremium || !ADS_ENABLED) {
            interstitialAd = null
        } else {
            loadInterstitial(context.applicationContext)
        }
    }

    /**
     * Preloads an interstitial ad in the background.
     */
    fun loadInterstitial(context: Context) {
        if (!ADS_ENABLED || _isPremiumUser.value || isAdLoading || interstitialAd != null) return

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "Interstitial ad successfully loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.w(TAG, "Failed to load interstitial ad: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows an interstitial ad with rate-limiting/cooldown to protect spiritual reading peace.
     * If user is Gold or cooldown has not passed, calls [onDismissed] immediately without interrupting.
     */
    fun showInterstitialWithCooldown(activity: Activity, onDismissed: () -> Unit) {
        if (!ADS_ENABLED || _isPremiumUser.value) {
            onDismissed()
            return
        }

        val currentTime = SystemClock.elapsedRealtime()
        val timeSinceLastAd = currentTime - lastInterstitialShownTime

        if (timeSinceLastAd < INTERSTITIAL_COOLDOWN_MS || interstitialAd == null) {
            // Respect peaceful reading cooldown or no ad ready
            onDismissed()
            if (interstitialAd == null) {
                loadInterstitial(activity.applicationContext)
            }
            return
        }

        val ad = interstitialAd
        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastInterstitialShownTime = SystemClock.elapsedRealtime()
                onDismissed()
                loadInterstitial(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                onDismissed()
                loadInterstitial(activity.applicationContext)
            }
        }

        ad?.show(activity)
    }

    /**
     * Shows a full-screen interstitial ad immediately (e.g. after completing daily quiz, or shorts milestone).
     * Automatically skips for Gold members or when ad is not yet cached.
     */
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        if (!ADS_ENABLED || _isPremiumUser.value) {
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            onDismissed()
            loadInterstitial(activity.applicationContext)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastInterstitialShownTime = SystemClock.elapsedRealtime()
                onDismissed()
                loadInterstitial(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                onDismissed()
                loadInterstitial(activity.applicationContext)
            }
        }

        ad.show(activity)
    }

    /**
     * Shows an interstitial ad triggered after watching 6 video reels in Shorts.
     * Skips immediately if user is a Gold member.
     */
    fun showShortsInterstitial(activity: Activity, onDismissed: () -> Unit) {
        showInterstitial(activity, onDismissed)
    }
}
