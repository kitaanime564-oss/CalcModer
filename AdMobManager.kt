package com.calculator.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobManager(private val context: Context) {

    companion object {
        private const val TAG = "AdMobManager"
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2864823906339269/6907335419"
        const val BANNER_AD_UNIT_ID = "ca-app-pub-2864823906339269/7290478793"
    }

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    init {
        loadInterstitialAd()
    }

    fun loadInterstitialAd() {
        if (isLoading || interstitialAd != null) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "AdMob Interstitial Ad successfully loaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.w(TAG, "AdMob Interstitial failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: (() -> Unit)? = null) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd() // Pre-load next ad
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                    onAdDismissed?.invoke()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd()
            onAdDismissed?.invoke()
        }
    }
}