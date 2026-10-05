package com.admobapp.rewardapp.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback

class AdMobManager(context: Context) {
    private val TAG = "AdMobManager"
    private val appContext = context.applicationContext
    private var rewardedAd: RewardedAd? = null
    private var onRewardedListener: ((rewardAmount: Int) -> Unit)? = null
    private var onAdLoadedListener: (() -> Unit)? = null
    private var onAdFailedListener: ((error: String) -> Unit)? = null

    init {
        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(appContext)
    }

    // Load Rewarded Ad
    fun loadRewardedAd(adUnitId: String) {
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()

        RewardedAd.load(appContext, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                this@AdMobManager.rewardedAd = rewardedAd
                Log.d(TAG, "Rewarded ad loaded successfully")
                onAdLoadedListener?.invoke()
                setupFullScreenContentCallback()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                this@AdMobManager.rewardedAd = null
                Log.e(TAG, "Failed to load rewarded ad: ${loadAdError.message}")
                onAdFailedListener?.invoke(loadAdError.message ?: "Unknown error")
            }
        })
    }

    // Show Rewarded Ad
    fun showRewardedAd(activity: android.app.Activity) {
        if (rewardedAd != null) {
            rewardedAd!!.show(activity) { rewardItem ->
                val rewardAmount = rewardItem.amount
                Log.d(TAG, "User earned reward: $rewardAmount ${rewardItem.type}")
                onRewardedListener?.invoke(rewardAmount)
            }
        } else {
            Log.w(TAG, "Rewarded ad is not loaded yet")
            onAdFailedListener?.invoke("Ad not loaded yet")
        }
    }

    // Check if ad is loaded
    fun isAdLoaded(): Boolean = rewardedAd != null

    // Set Reward Listener
    fun setOnRewardedListener(listener: (rewardAmount: Int) -> Unit) {
        this.onRewardedListener = listener
    }

    // Set Ad Loaded Listener
    fun setOnAdLoadedListener(listener: () -> Unit) {
        this.onAdLoadedListener = listener
    }

    // Set Ad Failed Listener
    fun setOnAdFailedListener(listener: (error: String) -> Unit) {
        this.onAdFailedListener = listener
    }

    // Setup full screen content callback
    private fun setupFullScreenContentCallback() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed by user")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                Log.e(TAG, "Ad failed to show: ${adError.message}")
                rewardedAd = null
            }

            override fun onAdImpression() {
                Log.d(TAG, "Ad impression occurred")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad was shown to user")
            }
        }
    }
}
