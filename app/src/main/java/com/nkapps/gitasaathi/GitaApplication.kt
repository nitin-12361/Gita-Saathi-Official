package com.nkapps.gitasaathi

import android.app.Application
import android.content.Context
import android.os.Build
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.data.GitaLifeApplicationData
import com.nkapps.gitasaathi.notifications.DailyShlokaScheduler

class GitaApplication : Application() {
    companion object {
        var instance: GitaApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        GitaData.init(this)
        GitaLifeApplicationData.init(this)
        com.nkapps.gitasaathi.data.GitaCacheManager.performStartupCleanup(this)
        DailyShlokaScheduler.createNotificationChannel(this)
        DailyShlokaScheduler.scheduleDailyNotification(this)
        try {
            val testConfig = com.google.android.gms.ads.RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR))
                .build()
            com.google.android.gms.ads.MobileAds.setRequestConfiguration(testConfig)
            com.google.android.gms.ads.MobileAds.initialize(this) { status ->
                android.util.Log.d("GitaApplication", "MobileAds initialized: ${status.adapterStatusMap}")
            }
        } catch (e: Exception) {
            android.util.Log.e("GitaApplication", "Error initializing MobileAds", e)
        }
        com.nkapps.gitasaathi.ads.GitaAdManager.init(this)
        com.nkapps.gitasaathi.billing.GitaBillingManager.init(this)

        // Initialize Firebase Suite (Crashlytics, Analytics, RemoteConfig, AppCheck, FCM)
        com.nkapps.gitasaathi.firebase.GitaAppCheck.init(this)
        com.nkapps.gitasaathi.firebase.GitaCrashlytics.init(this)
        com.nkapps.gitasaathi.firebase.GitaAnalytics.init(this)
        com.nkapps.gitasaathi.firebase.GitaRemoteConfig.init(this)
        com.nkapps.gitasaathi.notifications.GitaFirebaseMessagingService.initSubscription(this)
    }

    override fun attachBaseContext(base: Context) {
        val context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && base.attributionTag == null) {
            try {
                base.createAttributionContext("default")
            } catch (_: Exception) {
                base
            }
        } else {
            base
        }
        super.attachBaseContext(context)
    }
}

