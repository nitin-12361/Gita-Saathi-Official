package com.nkapps.gitasaathi.ads

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Jetpack Compose AdmobBanner composable.
 * Automatically checks GitaAdManager.isPremiumUser.
 * If user is in Gold (Ad-Free) mode, renders nothing.
 * Uses Anchored Adaptive Banner sizing for pixel-perfect device fitting.
 */
@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    customAdSize: AdSize? = null
) {
    val isPremium by GitaAdManager.isPremiumUser.collectAsState()

    if (isPremium) {
        // Zero-height / completely hidden for Gold subscribers
        return
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.coerceAtLeast(320)

    val resolvedAdSize = remember(customAdSize, screenWidthDp) {
        customAdSize ?: AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, screenWidthDp)
    }

    val adHeightDp = remember(resolvedAdSize) {
        if (resolvedAdSize.height > 0) resolvedAdSize.height.dp else 50.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(adHeightDp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1F1610).copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adHeightDp),
                factory = { ctx ->
                    AdView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setAdSize(resolvedAdSize)
                        adUnitId = GitaAdManager.TEST_BANNER_AD_ID
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                Log.d("AdmobBanner", "✅ Test Banner Ad loaded successfully!")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                Log.e(
                                    "AdmobBanner",
                                    "❌ Test Banner Ad failed to load: ${error.message} (code: ${error.code}, domain: ${error.domain})"
                                )
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                },
                onRelease = { adView ->
                    adView.destroy()
                }
            )
        }
    }
}
