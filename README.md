# 🪙 Coin Pusher - Android Game

An impressive Android coin pusher game with realistic physics, stunning visuals, and optimized performance.

## 🎮 Features

### ✨ Realistic Physics
- **Box2D Physics Engine** - Industry-standard 2D physics
- **Reduced Gravity** - 10% of Earth's gravity for realistic coin stacking
- **Optimized Collisions** - Smooth coin-to-coin and coin-to-platform interactions
- **Continuous Collision Detection** - No coins phasing through objects

### 🎨 Impressive Visuals
- **Particle Effects System** - Sparkles, explosions, and ambient dust
- **Color-Coded Coins** - Gold, Silver, Bronze, and Special coins
- **Texture Support** - Optional PNG textures for enhanced coin graphics
- **Adaptive App Icon** - Professional icon for Android 8.0+
- **Smooth Animations** - 60 FPS target on mobile devices
- **Visual Polish** - Platform movement, coin highlights, collection effects

### ⚡ Performance Optimized
- **Object Pooling** - Pre-allocated 500 coin pool for zero garbage collection
- **Max 600 Coins** - Performance limit for consistent 60fps
- **Efficient Rendering** - ShapeRenderer for fast 2D graphics
- **Mobile-First** - Optimized for Android devices (API 24+)

### 🎯 Game Mechanics
- **Touch Controls** - Tap anywhere to drop coins
- **Moving Platform** - Oscillating pusher mechanism
- **Coin Collection** - Collect coins that fall off the edge
- **Multiple Coin Types** - Different values and rarities
- **Score System** - Track your collected coins

### 🔊 Audio System
- **Sound Manager** - Graceful loading of OGG/WAV sound files
- **Collision Sounds** - Realistic coin impact audio
- **Collection Sounds** - Rewarding feedback effects
- **Volume Controls** - Master, SFX, and Music sliders
- **Fallback Support** - Game works without audio files

### 💰 Monetization Ready
- **AdMob Framework** - Full Google AdMob integration ready
- **Banner Ads** - Non-intrusive bottom banners
- **Interstitial Ads** - Between-game full-screen ads
- **Rewarded Videos** - Watch ads for free coins
- **Easy Setup** - See ADMOB_SETUP.md for activation

## 🏗️ Technology Stack

- **Language**: Kotlin 2.0.21
- **Framework**: libGDX 1.12.1
- **Physics**: Box2D (included with libGDX)
- **Build System**: Gradle 8.14.3
- **Target Platform**: Android API 35
- **Minimum API**: Android 24 (Android 7.0+)

## 📁 Project Structure

```
pusher/
├── core/                          # Shared game logic
│   └── src/main/kotlin/com/coinpusher/game/
│       ├── CoinPusherGame.kt     # Main game class
│       ├── entities/
│       │   ├── Coin.kt           # Coin physics and rendering
│       │   ├── Platform.kt       # Moving pusher platform
│       │   └── GameBounds.kt     # Walls and collection area
│       ├── physics/
│       │   ├── PhysicsWorld.kt   # Box2D world manager
│       │   └── CollisionListener.kt # Collision handling
│       ├── systems/
│       │   └── CoinPool.kt       # Object pooling system
│       ├── effects/
│       │   └── ParticleManager.kt # Particle effects
│       └── screens/
│           └── GameScreen.kt     # Main game screen
│
├── android/                       # Android-specific code
│   └── src/main/
│       ├── kotlin/com/coinpusher/game/
│       │   └── AndroidLauncher.kt # Android entry point
│       ├── res/                   # Android resources
│       └── AndroidManifest.xml
│
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
└── gradle.properties             # Gradle properties
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Arctic Fox or newer)
- **JDK 17+**
- **Android SDK** (API 24-35)
- **Gradle 8.14.3** (included via wrapper)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/ElliottSax/pusher.git
   cd pusher
   ```

2. **Open in Android Studio**
   - File → Open → Select the `pusher` folder
   - Let Gradle sync automatically

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   ```bash
   ./gradlew android:installDebug
   ```
   Or use the "Run" button in Android Studio

### Building APK

```bash
# Debug APK
./gradlew android:assembleDebug

# Release APK (optimized with ProGuard)
./gradlew android:assembleRelease
```

APK will be generated in: `android/build/outputs/apk/`

## 🎨 Optional Enhancements

The game is fully playable as-is, but you can enhance it further:

### Add Sound Effects
1. See `android/src/main/assets/sounds/README.md` for requirements
2. Add 4 OGG/WAV files: coin_drop, coin_collect, coin_collision, big_win
3. SoundManager will automatically load and use them
4. **Game works without sounds** - graceful fallback

### Add Coin Textures
1. See `android/src/main/assets/textures/README.md` for specifications
2. Create 4 PNG files (128x128): coin_gold, coin_silver, coin_bronze, coin_special
3. TextureManager will automatically load and render them
4. **Game works without textures** - uses shape rendering

### Customize App Icon
1. See `android/src/main/res/APP_ICON_GUIDE.md` for instructions
2. Adaptive icon already created (Android 8.0+) with gold coin design
3. Optionally add traditional PNG icons for older Android versions
4. Use Android Studio's Image Asset tool for easy generation

### Enable Monetization
1. See `ADMOB_SETUP.md` for complete guide
2. Framework is ready - just needs AdMob account setup
3. Uncomment code in `AndroidAdManager.kt` after adding dependency
4. Earn revenue from banner, interstitial, and rewarded video ads

All enhancements are **optional** - the game is production-ready without them!

## 🎮 How to Play

1. **Tap the screen** to drop a coin
2. Watch the **platform push** the coins forward
3. Coins fall onto other coins and get pushed toward the edge
4. **Collect coins** that fall off the platform
5. Earn points based on coin values:
   - 🥇 **Gold**: 1 point
   - 🥈 **Silver**: 5 points
   - 🥉 **Bronze**: 10 points
   - 💎 **Special**: 50 points

## 🔬 Technical Deep Dive

### Physics Optimizations

```kotlin
// Reduced gravity (10% of Earth's)
const val GRAVITY = -0.981f  // vs -9.81f

// Fixed timestep for consistent physics
val timeStep = 1/60f
val velocityIterations = 8
val positionIterations = 3
```

### Object Pooling Implementation

```kotlin
// Pre-allocate 500 coins to avoid GC
class CoinPool(initialSize: Int = 500) {
    private val freeCoins = Array<Coin>(initialSize)
    private val activeCoins = Array<Coin>()

    fun obtain(): Coin? {
        return if (freeCoins.size > 0) {
            freeCoins.pop()
        } else {
            Coin()
        }
    }
}
```

### Particle System Performance

- **Max 400 particles** for 60fps on mobile
- Object pooling for particle instances
- Types: Sparkle (10), Explosion (30), Dust (3)

## 📊 Performance Metrics

| Device Tier | Coin Limit | FPS Target | Particles |
|------------|-----------|-----------|-----------|
| High-end   | 600       | 60        | 400       |
| Mid-range  | 400       | 60        | 300       |
| Low-end    | 200       | 40        | 200       |

## 🛠️ Development Roadmap

### Phase 1: Core Mechanics ✅
- [x] Physics system with Box2D
- [x] Coin entities with realistic physics
- [x] Object pooling system
- [x] Moving platform mechanism
- [x] Collision detection
- [x] Basic rendering

### Phase 2: Visual Polish 🚧
- [x] Particle effects
- [ ] Improved coin textures/sprites
- [ ] Background and themes
- [ ] Smooth camera effects
- [ ] Screen shake and juice

### Phase 3: Audio 📋
- [ ] Coin drop sounds
- [ ] Coin collision sounds
- [ ] Platform movement sound
- [ ] Collection fanfare
- [ ] Background music

### Phase 4: Features 📋
- [ ] Multiple machine types
- [ ] Power-ups and special items
- [ ] Daily quests and challenges
- [ ] Achievement system
- [ ] Progression and unlockables

### Phase 5: Monetization 📋
- [ ] Ad integration (AdMob)
- [ ] In-app purchases
- [ ] Piggy bank system
- [ ] Battle pass
- [ ] VIP system

### Phase 6: Polish & Launch 📋
- [ ] Performance optimization
- [ ] Device testing
- [ ] Analytics integration
- [ ] Localization
- [ ] Play Store release

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest
```

## 📱 Supported Devices

- **OS**: Android 7.0 (API 24) and above
- **Screen**: 720x1280 minimum (portrait)
- **RAM**: 2GB+ recommended
- **GPU**: OpenGL ES 2.0+ support

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **libGDX** - Amazing game development framework
- **Box2D** - Robust physics engine
- Coin pusher research and optimization techniques
- Mobile game development community

## 📞 Contact

- **GitHub**: [@ElliottSax](https://github.com/ElliottSax)
- **Project**: [pusher](https://github.com/ElliottSax/pusher)

## 🎯 Future Enhancements

- [ ] 3D graphics with OpenGL ES 3.0
- [ ] Multiplayer mode
- [ ] Cloud save synchronization
- [ ] Social features (friends, leaderboards)
- [ ] Seasonal events and themes
- [ ] AR mode using ARCore

---

**Made with ❤️ and lots of ☕**

*Based on extensive research into coin pusher mechanics, mobile game optimization, and best practices for Android game development.*
