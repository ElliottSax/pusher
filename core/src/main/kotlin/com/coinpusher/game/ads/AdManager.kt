package com.coinpusher.game.ads

import com.badlogic.gdx.Gdx

/**
 * Manages advertisement display and rewarded video ads
 * Cross-platform abstraction for ad networks (AdMob, Unity Ads, etc.)
 *
 * This is a framework/interface - actual implementation is platform-specific
 * See AndroidAdManager for the Android implementation with AdMob
 */
interface AdManager {

    /**
     * Initialize the ad system
     * Call this once during app startup
     */
    fun initialize()

    /**
     * Check if banner ads are ready to display
     */
    fun isBannerReady(): Boolean

    /**
     * Show banner ad at the bottom of the screen
     * @param visible true to show, false to hide
     */
    fun showBanner(visible: Boolean)

    /**
     * Check if an interstitial ad is loaded and ready
     */
    fun isInterstitialReady(): Boolean

    /**
     * Load an interstitial ad for later display
     */
    fun loadInterstitial()

    /**
     * Show interstitial ad (full-screen ad between gameplay)
     * Only shows if ad is loaded and ready
     */
    fun showInterstitial()

    /**
     * Check if a rewarded video ad is loaded and ready
     */
    fun isRewardedVideoReady(): Boolean

    /**
     * Load a rewarded video ad for later display
     */
    fun loadRewardedVideo()

    /**
     * Show rewarded video ad (user watches for reward)
     * @param onRewarded callback when user completes watching
     * @param onFailed callback if ad fails to show
     */
    fun showRewardedVideo(onRewarded: (Int) -> Unit, onFailed: () -> Unit)

    /**
     * Hide all ads (for premium users or during gameplay)
     */
    fun hideAllAds()

    /**
     * Clean up ad resources
     * Call this when app is closing
     */
    fun dispose()

    companion object {
        // Reward amounts for watched videos
        const val REWARD_COINS = 100
        const val REWARD_MULTIPLIER = 2
        const val REWARD_CONTINUE = 1
    }
}

/**
 * No-op implementation for testing or platforms without ads
 */
class NoOpAdManager : AdManager {
    override fun initialize() {
        Gdx.app.log("AdManager", "No-op ad manager (ads disabled)")
    }

    override fun isBannerReady() = false
    override fun showBanner(visible: Boolean) {}
    override fun isInterstitialReady() = false
    override fun loadInterstitial() {}
    override fun showInterstitial() {}
    override fun isRewardedVideoReady() = false
    override fun loadRewardedVideo() {}
    override fun showRewardedVideo(onRewarded: (Int) -> Unit, onFailed: () -> Unit) {
        Gdx.app.log("AdManager", "No ads available - calling onFailed")
        onFailed()
    }
    override fun hideAllAds() {}
    override fun dispose() {}
}
