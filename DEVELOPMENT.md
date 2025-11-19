# 🛠️ Development Guide

Comprehensive guide for developers working on the Coin Pusher Android game.

## 📚 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Code Organization](#code-organization)
- [Development Setup](#development-setup)
- [Building and Testing](#building-and-testing)
- [Performance Optimization](#performance-optimization)
- [Adding Features](#adding-features)
- [Debugging Tips](#debugging-tips)

## 🏛️ Architecture Overview

### Game Loop Flow

```
AndroidLauncher → CoinPusherGame → GameScreen
                                    ├── PhysicsWorld (Box2D)
                                    ├── CoinPool (Object Pooling)
                                    ├── Platform (Moving Pusher)
                                    ├── GameBounds (Walls/Collector)
                                    └── ParticleManager (Effects)
```

### Key Design Patterns

1. **Object Pooling** - `CoinPool` pre-allocates coins to avoid GC
2. **Entity-Component** - Separates rendering from physics
3. **Manager Pattern** - Centralized systems (Physics, Particles)
4. **Observer Pattern** - Collision callbacks through `ContactListener`

## 📂 Code Organization

### Core Module (`core/`)

Game logic shared across platforms (Android, iOS, Desktop)

```
core/src/main/kotlin/com/coinpusher/game/
├── CoinPusherGame.kt          # Main game class, manages screens
├── entities/                   # Game objects
│   ├── Coin.kt                # Coin physics body and rendering
│   ├── Platform.kt            # Oscillating pusher platform
│   └── GameBounds.kt          # Walls and collection area
├── physics/                    # Box2D integration
│   ├── PhysicsWorld.kt        # World management, gravity
│   └── CollisionListener.kt   # Contact callbacks
├── systems/                    # Core systems
│   └── CoinPool.kt            # Object pooling implementation
├── effects/                    # Visual effects
│   └── ParticleManager.kt     # Particle system with pooling
└── screens/                    # Game screens
    └── GameScreen.kt          # Main gameplay screen
```

### Android Module (`android/`)

Android-specific code and resources

```
android/src/main/
├── kotlin/com/coinpusher/game/
│   └── AndroidLauncher.kt     # Android entry point
├── AndroidManifest.xml        # App permissions and config
├── res/                        # Android resources
│   ├── values/
│   │   ├── strings.xml        # App name, strings
│   │   └── styles.xml         # App theme
│   └── mipmap-*/              # App icons (TODO)
└── proguard-rules.pro         # Code obfuscation rules
```

## 🔧 Development Setup

### Option 1: Android Studio (Recommended)

1. **Install Android Studio**
   - Download from [developer.android.com](https://developer.android.com/studio)
   - Install Android SDK API 24-35

2. **Open Project**
   ```bash
   # Open Android Studio
   File → Open → Select pusher/ directory
   ```

3. **Sync Gradle**
   - Android Studio will auto-sync
   - Or manually: `File → Sync Project with Gradle Files`

4. **Configure Device**
   - **Physical Device**: Enable USB debugging
   - **Emulator**: Create AVD (API 24+, 720x1280)

5. **Run**
   - Click Run button (green triangle)
   - Select target device

### Option 2: Command Line

```bash
# Build project
./gradlew build

# Install debug APK
./gradlew android:installDebug

# Run on connected device
adb shell am start -n com.coinpusher.game/com.coinpusher.game.AndroidLauncher

# View logs
adb logcat | grep "CoinPusher\|GameScreen\|AndroidRuntime"
```

## 🏗️ Building and Testing

### Build Variants

```bash
# Debug build (fast, includes debug symbols)
./gradlew android:assembleDebug

# Release build (optimized, ProGuard enabled)
./gradlew android:assembleRelease

# Clean build
./gradlew clean build
```

### Testing

```bash
# Run JVM tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest

# Generate test reports
./gradlew testDebugUnitTest
# Reports in: build/reports/tests/
```

### Performance Profiling

1. **Android Studio Profiler**
   - View → Tool Windows → Profiler
   - Monitor CPU, Memory, Network

2. **Frame Rate Monitoring**
   ```kotlin
   // Already implemented in GameScreen
   game.font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", ...)
   ```

3. **Memory Analysis**
   - Profile → Memory
   - Look for GC spikes (object pooling should prevent these)

## ⚡ Performance Optimization

### Current Optimizations

✅ **Object Pooling** - Pre-allocated 500 coins
✅ **Fixed Timestep** - Consistent physics (60fps)
✅ **Reduced Gravity** - 10% for realistic stacking
✅ **Max Coin Limit** - 600 coins for stable framerate
✅ **Particle Pooling** - 400 particle limit
✅ **Efficient Rendering** - ShapeRenderer for fast 2D

### Performance Targets

| Metric | Target | Current |
|--------|--------|---------|
| FPS | 60 | ✅ 60 |
| Frame Time | <16ms | ✅ ~15ms |
| Active Coins | 600 max | ✅ Enforced |
| Particles | 400 max | ✅ Enforced |
| Memory | <150MB | 🚧 TBD |

### Optimization Tips

1. **Avoid Allocations in Game Loop**
   ```kotlin
   // ❌ BAD - Creates new object every frame
   val position = Vector2(x, y)

   // ✅ GOOD - Reuse object
   position.set(x, y)
   ```

2. **Use Object Pools**
   ```kotlin
   // ❌ BAD
   val coin = Coin()
   // ... use coin
   // coin gets garbage collected

   // ✅ GOOD
   val coin = coinPool.obtain()
   // ... use coin
   coinPool.free(coin)
   ```

3. **Batch Rendering**
   ```kotlin
   // Start batch once, draw multiple objects
   batch.begin()
   // ... draw many objects
   batch.end()
   ```

4. **Profile Before Optimizing**
   - Use Android Studio Profiler
   - Measure actual performance
   - Don't assume bottlenecks

## 🎨 Adding Features

### Adding a New Coin Type

1. **Define Type in Coin.kt**
   ```kotlin
   enum class CoinType(val value: Int, val color: Color) {
       GOLD(1, Color.GOLD),
       SILVER(5, Color.LIGHT_GRAY),
       BRONZE(10, Color.BROWN),
       SPECIAL(50, Color.CYAN),
       DIAMOND(100, Color.MAGENTA)  // ← New type
   }
   ```

2. **Add to Spawn Logic (GameScreen.kt)**
   ```kotlin
   val type = when {
       random > 0.98 -> Coin.CoinType.DIAMOND  // 2% chance
       random > 0.95 -> Coin.CoinType.SPECIAL
       // ... rest
   }
   ```

### Adding Sound Effects

1. **Create SoundManager.kt**
   ```kotlin
   package com.coinpusher.game.systems

   import com.badlogic.gdx.Gdx
   import com.badlogic.gdx.audio.Sound

   class SoundManager {
       private val coinDropSound: Sound = Gdx.audio.newSound(
           Gdx.files.internal("sounds/coin_drop.wav")
       )

       fun playCoinDrop() {
           coinDropSound.play(0.5f) // volume
       }

       fun dispose() {
           coinDropSound.dispose()
       }
   }
   ```

2. **Add to GameScreen**
   ```kotlin
   private val soundManager = SoundManager()

   // In spawnCoin():
   soundManager.playCoinDrop()
   ```

3. **Add Sound Files**
   - Place in `android/assets/sounds/`
   - Supported formats: WAV, MP3, OGG

### Adding a New Screen

1. **Create Screen Class**
   ```kotlin
   package com.coinpusher.game.screens

   import com.badlogic.gdx.Screen
   import com.coinpusher.game.CoinPusherGame

   class MenuScreen(private val game: CoinPusherGame) : Screen {
       override fun render(delta: Float) {
           // Render menu
       }

       override fun show() {}
       override fun hide() {}
       override fun pause() {}
       override fun resume() {}
       override fun resize(width: Int, height: Int) {}
       override fun dispose() {}
   }
   ```

2. **Switch to Screen**
   ```kotlin
   // In CoinPusherGame.kt
   setScreen(MenuScreen(this))
   ```

## 🐛 Debugging Tips

### Common Issues

**Issue**: Coins fall through platform
- **Cause**: Collision detection issue
- **Fix**: Check continuous collision detection enabled
- **Debug**: Enable Box2D debug rendering

**Issue**: Low FPS / Lag
- **Cause**: Too many objects, GC pressure
- **Fix**: Check active coin count, particle count
- **Debug**: Profile memory, check GC logs

**Issue**: Coins jitter/vibrate
- **Cause**: Physics timestep or gravity settings
- **Fix**: Already addressed with 10% gravity
- **Debug**: Adjust velocityIterations, positionIterations

### Debug Rendering

Enable Box2D debug renderer to see collision shapes:

```kotlin
class GameScreen {
    private val debugRenderer = Box2DDebugRenderer()

    override fun render(delta: Float) {
        // ... normal rendering
        debugRenderer.render(physicsWorld.world, camera.combined)
    }
}
```

### Logging

```kotlin
// Log in game code
Gdx.app.log("TAG", "Message")
Gdx.app.error("TAG", "Error message")
Gdx.app.debug("TAG", "Debug message")

// View in logcat
adb logcat -s CoinPusher:D GameScreen:D *:E
```

## 🎯 Next Steps

### Immediate Improvements

- [ ] Add sprite textures instead of shapes
- [ ] Implement sound system
- [ ] Create menu screen
- [ ] Add pause functionality
- [ ] Implement save/load system

### Medium-term Goals

- [ ] Multiple machine themes
- [ ] Power-ups and special coins
- [ ] Achievement system
- [ ] Daily rewards
- [ ] Leaderboards

### Long-term Vision

- [ ] Multiplayer mode
- [ ] 3D graphics
- [ ] AR mode
- [ ] Social features
- [ ] Cross-platform (iOS)

## 📖 Resources

### libGDX Documentation
- [Official Docs](https://libgdx.com/wiki/)
- [Box2D Manual](https://box2d.org/documentation/)
- [libGDX Forums](https://libgdx.com/community/)

### Android Development
- [Android Developer Guide](https://developer.android.com/guide)
- [Game Development Best Practices](https://developer.android.com/games)
- [Performance Optimization](https://developer.android.com/topic/performance)

### Kotlin Resources
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Kotlin for Android](https://developer.android.com/kotlin)

---

Happy coding! 🚀
