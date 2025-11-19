# AdMob Integration Guide

This guide explains how to add Google AdMob monetization to the Coin Pusher game.

## Overview

AdMob provides three main ad formats:
1. **Banner Ads** - Small ads at top/bottom of screen
2. **Interstitial Ads** - Full-screen ads between gameplay sessions
3. **Rewarded Video Ads** - User watches video for in-game rewards

## Current Status

✅ **Framework Ready** - AdManager interface and AndroidAdManager implementation created
⚠️ **Not Yet Active** - Requires AdMob account and dependency configuration

## Step-by-Step Setup

### 1. Create AdMob Account

1. Go to **https://admob.google.com**
2. Sign in with Google account
3. Click "Get Started"
4. Create an account (publisher agreement)
5. Add payment information (for receiving ad revenue)

**First-time setup**: You'll need to verify your address to receive payments.

### 2. Register Your App

1. In AdMob console, click "Apps"
2. Click "Add App"
3. Select "Android"
4. Enter app details:
   - **App name**: Coin Pusher
   - **Package name**: com.coinpusher.game (must match AndroidManifest.xml)
5. Click "Add"

AdMob will generate an **App ID** like: `ca-app-pub-1234567890123456~0987654321`

### 3. Create Ad Units

Create three ad units (one for each ad type):

**Banner Ad**:
1. In your app dashboard, click "Ad units"
2. Click "Add ad unit"
3. Select "Banner"
4. Name it "Main Banner"
5. Click "Create ad unit"
6. Copy the **Ad unit ID**: `ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY`

**Interstitial Ad**:
1. Click "Add ad unit" again
2. Select "Interstitial"
3. Name it "Game Over Interstitial"
4. Click "Create ad unit"
5. Copy the Ad unit ID

**Rewarded Ad**:
1. Click "Add ad unit" again
2. Select "Rewarded"
3. Name it "Free Coins Reward"
4. Click "Create ad unit"
5. Copy the Ad unit ID

### 4. Add AdMob Dependency

Edit **`android/build.gradle.kts`**:

```kotlin
dependencies {
    // Existing dependencies...
    implementation("com.badlogic.gdxandroid:gdx-backend-android:1.12.1")

    // Add AdMob SDK
    implementation("com.google.android.gms:play-services-ads:22.6.0")

    // Rest of dependencies...
}
```

### 5. Update AndroidManifest.xml

Edit **`android/src/main/AndroidManifest.xml`**:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Add internet permission (should already exist) -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/GdxTheme">

        <!-- Add AdMob App ID -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>

        <activity
            android:name=".AndroidLauncher"
            android:exported="true"
            ...>
            <!-- Activity content -->
        </activity>
    </application>
</manifest>
```

**Important**: Replace `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY` with YOUR App ID from step 2!

### 6. Update AndroidAdManager.kt

Edit **`android/src/main/kotlin/com/coinpusher/game/AndroidAdManager.kt`**:

1. **Replace test ad unit IDs** with your real IDs from step 3:

```kotlin
private val BANNER_AD_ID = "ca-app-pub-YOUR_BANNER_ID"
private val INTERSTITIAL_AD_ID = "ca-app-pub-YOUR_INTERSTITIAL_ID"
private val REWARDED_AD_ID = "ca-app-pub-YOUR_REWARDED_ID"
```

2. **Uncomment all the ad implementation code**:
   - Remove `/*` and `*/` around all AdMob code
   - Add necessary imports at the top:

```kotlin
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
```

### 7. Initialize Ads in AndroidLauncher

Edit **`android/src/main/kotlin/com/coinpusher/game/AndroidLauncher.kt`**:

```kotlin
class AndroidLauncher : AndroidApplication() {
    private lateinit var adManager: AndroidAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ad manager
        adManager = AndroidAdManager(this)
        adManager.initialize()

        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
            hideStatusBar = true
            useImmersiveMode = true
        }

        val game = CoinPusherGame()
        // TODO: Pass adManager to game if needed for cross-platform access

        initialize(game, config)
    }

    override fun onDestroy() {
        adManager.dispose()
        super.onDestroy()
    }
}
```

### 8. Integrate Ads into Game

You can now use ads in your game screens. Example in **MenuScreen**:

```kotlin
// Show banner ad on menu
adManager.showBanner(true)

// Button to watch rewarded video for free coins
if (adManager.isRewardedVideoReady()) {
    // Show "Watch Ad for 100 Coins" button
    // On click:
    adManager.showRewardedVideo(
        onRewarded = { coins ->
            gameData.addCoins(coins)
            // Show success message
        },
        onFailed = {
            // Show "Ad not available" message
        }
    )
}
```

Example in **GameScreen** (game over):

```kotlin
private fun gameOver() {
    // Show interstitial ad every 3rd game
    if (gameData.gamesPlayed % 3 == 0 && adManager.isInterstitialReady()) {
        adManager.showInterstitial()
    }

    // Return to menu
    returnToMenu()
}
```

### 9. Test with Test Ads

**Important**: Use test ad unit IDs during development!

Google provides test IDs that show real ads without affecting your AdMob account:
- **Banner**: `ca-app-pub-3940256099942544/6300978111`
- **Interstitial**: `ca-app-pub-3940256099942544/1033173712`
- **Rewarded**: `ca-app-pub-3940256099942544/5224354917`

Or enable test mode for your device:

```kotlin
val testDeviceIds = listOf("YOUR_DEVICE_ID")
val configuration = RequestConfiguration.Builder()
    .setTestDeviceIds(testDeviceIds)
    .build()
MobileAds.setRequestConfiguration(configuration)
```

Find your device ID in logcat (search for "AdMob").

### 10. Build and Test

```bash
./gradlew android:assembleDebug
./gradlew android:installDebug
```

Test all three ad types:
- ✅ Banner appears at bottom
- ✅ Interstitial shows full-screen
- ✅ Rewarded video plays and grants coins

### 11. Go Live

Before publishing to Play Store:

1. **Switch to production ad unit IDs** (remove test IDs)
2. **Remove test device configuration**
3. **Test on real device** (not just emulator)
4. **Verify ads load and display**
5. **Check AdMob dashboard** for impressions

**Warning**: Do NOT click your own ads! This violates AdMob policy and can get you banned.

## Monetization Strategy

### Recommended Ad Placement:

1. **Banner Ads**:
   - ❌ Not in GameScreen (blocks gameplay)
   - ✅ MenuScreen (always visible)
   - ✅ SettingsScreen (static screen)

2. **Interstitial Ads**:
   - After every 3-5 games
   - On "Quit" from game
   - On "Continue" prompt
   - Maximum once per 2-3 minutes

3. **Rewarded Video Ads**:
   - "Free Coins" button (100 coins)
   - "Continue Playing" after game over
   - "2x Multiplier" power-up (5 minutes)
   - "Unlock Machine" (new themes)

### Best Practices:

✅ **Do**:
- Give users a choice (rewarded ads)
- Show ads at natural breaks
- Limit interstitial frequency
- Test ad placement for UX
- Monitor fill rates in AdMob console

❌ **Don't**:
- Show ads during gameplay
- Force ads too frequently
- Click your own ads
- Hide ad controls
- Show multiple interstitials in a row

## AdMob Earnings

### How It Works:

- **CPC (Cost Per Click)**: You earn when users click ads (typical: $0.05-$0.50)
- **CPM (Cost Per Mille)**: You earn per 1000 impressions (typical: $0.50-$5.00)
- **Rewarded**: Higher rates ($5-$20 CPM) since users actively engage

### Realistic Expectations:

- **100 daily active users**: $1-5/day
- **1,000 daily active users**: $10-50/day
- **10,000 daily active users**: $100-500/day

Rates vary by:
- User location (US/EU higher than others)
- Ad type (rewarded > interstitial > banner)
- Season (higher around holidays)
- Niche (games have moderate rates)

### Payment Threshold:

- Minimum payout: $100
- Paid monthly (Net-60 terms)
- Requires verified identity and tax info

## Troubleshooting

### Ads Not Loading?

1. Check internet connection
2. Verify ad unit IDs are correct
3. Check logcat for errors
4. Ensure AdMob App ID in manifest
5. Try test ad IDs first
6. Check AdMob account is approved

### Ad Request Failed?

- "No fill": Normal, ads not always available
- "Invalid request": Check ad unit ID
- "Network error": Internet connection issue
- "App ID missing": Check AndroidManifest.xml

### Banned/Suspended?

- Never click your own ads
- Follow AdMob policies strictly
- Provide age-appropriate content
- Don't encourage accidental clicks
- Read: https://support.google.com/admob/answer/6128543

## Alternative Ad Networks

If AdMob doesn't work for you:

1. **Unity Ads** - Good for games, easy integration
2. **Facebook Audience Network** - High fill rate
3. **AppLovin** - Mediation platform
4. **AdColony** - Video ads specialist
5. **Vungle** - Rewarded videos

You can use mediation to show ads from multiple networks automatically.

## Resources

- **AdMob Console**: https://admob.google.com
- **Documentation**: https://developers.google.com/admob/android/quick-start
- **Policy Center**: https://support.google.com/admob/answer/6128543
- **Community**: https://groups.google.com/g/google-admob-ads-sdk

## Summary

**Framework Status**: ✅ Ready to integrate
**Code Required**: ~50 lines (uncomment existing code)
**Time to Setup**: ~30-60 minutes
**Approval Time**: Usually instant (can take up to 24 hours)
**Revenue Potential**: Low-Medium (depends on user base)

The ad framework is fully implemented - just follow steps 1-11 above to enable monetization!
