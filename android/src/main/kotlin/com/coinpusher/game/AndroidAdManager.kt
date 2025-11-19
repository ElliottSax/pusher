package com.coinpusher.game

import android.app.Activity
import com.badlogic.gdx.Gdx
import com.coinpusher.game.ads.AdManager

/**
 * Android implementation of AdManager using Google AdMob
 *
 * SETUP REQUIRED:
 * 1. Add AdMob dependency to android/build.gradle.kts:
 *    implementation("com.google.android.gms:play-services-ads:22.6.0")
 *
 * 2. Add AdMob App ID to AndroidManifest.xml:
 *    <meta-data
 *        android:name="com.google.android.gms.ads.APPLICATION_ID"
 *        android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>
 *
 * 3. Get your AdMob account at: https://admob.google.com
 *
 * 4. Replace test ad IDs below with your real ad unit IDs
 *
 * See ADMOB_SETUP.md for complete instructions
 */
class AndroidAdManager(private val activity: Activity) : AdManager {

    // TODO: Replace with your AdMob ad unit IDs from AdMob console
    private val BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111" // Test ID
    private val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712" // Test ID
    private val REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917" // Test ID

    // Ad instances (commented out until AdMob dependency is added)
    // private var bannerAd: AdView? = null
    // private var interstitialAd: InterstitialAd? = null
    // private var rewardedAd: RewardedAd? = null

    private var initialized = false
    private var bannerVisible = false

    override fun initialize() {
        if (initialized) return

        activity.runOnUiThread {
            try {
                // Uncomment when AdMob dependency is added:
                /*
                // Initialize the Mobile Ads SDK
                MobileAds.initialize(activity) { initializationStatus ->
                    Gdx.app.log("AdMob", "Initialized: ${initializationStatus.adapterStatusMap}")
                }

                // Set up banner ad
                setupBanner()

                // Load first interstitial
                loadInterstitial()

                // Load first rewarded video
                loadRewardedVideo()
                */

                initialized = true
                Gdx.app.log("AdMob", "Ad system initialized (commented out - add dependency)")
            } catch (e: Exception) {
                Gdx.app.error("AdMob", "Failed to initialize: ${e.message}")
            }
        }
    }

    private fun setupBanner() {
        // Uncomment when AdMob dependency is added:
        /*
        bannerAd = AdView(activity).apply {
            adUnitId = BANNER_AD_ID
            setAdSize(AdSize.BANNER)

            // Load the ad
            loadAd(AdRequest.Builder().build())

            // Add to layout
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }

            (activity.findViewById<ViewGroup>(android.R.id.content)).addView(this, layoutParams)
            visibility = View.GONE
        }
        */
    }

    override fun isBannerReady(): Boolean {
        // return bannerAd != null
        return false
    }

    override fun showBanner(visible: Boolean) {
        bannerVisible = visible
        activity.runOnUiThread {
            // bannerAd?.visibility = if (visible) View.VISIBLE else View.GONE
            Gdx.app.log("AdMob", "Banner ${if (visible) "shown" else "hidden"} (not implemented)")
        }
    }

    override fun isInterstitialReady(): Boolean {
        // return interstitialAd != null
        return false
    }

    override fun loadInterstitial() {
        activity.runOnUiThread {
            // Uncomment when AdMob dependency is added:
            /*
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                INTERSTITIAL_AD_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Gdx.app.log("AdMob", "Interstitial ad loaded")

                        // Set callback for when ad is shown/closed
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Gdx.app.log("AdMob", "Interstitial ad dismissed")
                                interstitialAd = null
                                loadInterstitial() // Load next one
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                Gdx.app.error("AdMob", "Interstitial failed: ${error.message}")
                                interstitialAd = null
                            }
                        }
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Gdx.app.error("AdMob", "Interstitial load failed: ${error.message}")
                        interstitialAd = null
                    }
                }
            )
            */
        }
    }

    override fun showInterstitial() {
        activity.runOnUiThread {
            // interstitialAd?.show(activity) ?: Gdx.app.log("AdMob", "Interstitial not ready")
            Gdx.app.log("AdMob", "Interstitial show requested (not implemented)")
        }
    }

    override fun isRewardedVideoReady(): Boolean {
        // return rewardedAd != null
        return false
    }

    override fun loadRewardedVideo() {
        activity.runOnUiThread {
            // Uncomment when AdMob dependency is added:
            /*
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                activity,
                REWARDED_AD_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Gdx.app.log("AdMob", "Rewarded ad loaded")

                        // Set callback
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Gdx.app.log("AdMob", "Rewarded ad dismissed")
                                rewardedAd = null
                                loadRewardedVideo() // Load next one
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                Gdx.app.error("AdMob", "Rewarded failed: ${error.message}")
                                rewardedAd = null
                            }
                        }
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Gdx.app.error("AdMob", "Rewarded load failed: ${error.message}")
                        rewardedAd = null
                    }
                }
            )
            */
        }
    }

    override fun showRewardedVideo(onRewarded: (Int) -> Unit, onFailed: () -> Unit) {
        activity.runOnUiThread {
            // Uncomment when AdMob dependency is added:
            /*
            rewardedAd?.let { ad ->
                ad.show(activity) { rewardItem ->
                    // User earned reward
                    Gdx.app.log("AdMob", "User earned reward: ${rewardItem.amount}")
                    Gdx.app.postRunnable {
                        onRewarded(AdManager.REWARD_COINS)
                    }
                }
            } ?: run {
                Gdx.app.log("AdMob", "Rewarded video not ready")
                Gdx.app.postRunnable { onFailed() }
            }
            */

            // Temporary: Always fail (no ads loaded)
            Gdx.app.postRunnable { onFailed() }
        }
    }

    override fun hideAllAds() {
        showBanner(false)
    }

    override fun dispose() {
        activity.runOnUiThread {
            // bannerAd?.destroy()
            Gdx.app.log("AdMob", "Ad system disposed")
        }
    }
}
