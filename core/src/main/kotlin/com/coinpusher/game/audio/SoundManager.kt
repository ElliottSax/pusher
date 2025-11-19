package com.coinpusher.game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable

/**
 * Manages all game audio with pooling and volume controls
 * Optimized for mobile performance
 */
class SoundManager : Disposable {

    // Sound effects (loaded on demand since we'll use generated sounds)
    private var coinDropSound: Sound? = null
    private var coinCollectSound: Sound? = null
    private var platformSound: Sound? = null
    private var collisionSound: Sound? = null

    // Background music
    private var backgroundMusic: Music? = null

    // Volume settings
    var masterVolume = 1.0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateMusicVolume()
        }

    var sfxVolume = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    var musicVolume = 0.5f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateMusicVolume()
        }

    var soundEnabled = true
    var musicEnabled = true
        set(value) {
            field = value
            if (value) {
                backgroundMusic?.play()
            } else {
                backgroundMusic?.pause()
            }
        }

    // Sound cooldowns to prevent audio spam
    private var lastCoinDrop = 0f
    private var lastCollision = 0f
    private val soundCooldown = 0.05f // 50ms between sounds

    init {
        loadSounds()
        Gdx.app.log("SoundManager", "Sound system initialized")
    }

    private fun loadSounds() {
        // Try to load sound files, gracefully handle missing files
        try {
            if (Gdx.files.internal("sounds/coin_drop.ogg").exists()) {
                coinDropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_drop.ogg"))
                Gdx.app.log("SoundManager", "Loaded: coin_drop.ogg")
            }
        } catch (e: Exception) {
            Gdx.app.error("SoundManager", "Failed to load coin_drop.ogg: ${e.message}")
        }

        try {
            if (Gdx.files.internal("sounds/coin_collect.ogg").exists()) {
                coinCollectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_collect.ogg"))
                Gdx.app.log("SoundManager", "Loaded: coin_collect.ogg")
            }
        } catch (e: Exception) {
            Gdx.app.error("SoundManager", "Failed to load coin_collect.ogg: ${e.message}")
        }

        try {
            if (Gdx.files.internal("sounds/coin_collision.ogg").exists()) {
                collisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_collision.ogg"))
                Gdx.app.log("SoundManager", "Loaded: coin_collision.ogg")
            }
        } catch (e: Exception) {
            Gdx.app.error("SoundManager", "Failed to load coin_collision.ogg: ${e.message}")
        }

        try {
            if (Gdx.files.internal("sounds/big_win.ogg").exists()) {
                platformSound = Gdx.audio.newSound(Gdx.files.internal("sounds/big_win.ogg"))
                Gdx.app.log("SoundManager", "Loaded: big_win.ogg")
            }
        } catch (e: Exception) {
            Gdx.app.error("SoundManager", "Failed to load big_win.ogg: ${e.message}")
        }

        try {
            if (Gdx.files.internal("sounds/background_music.ogg").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music.ogg"))
                Gdx.app.log("SoundManager", "Loaded: background_music.ogg")
            }
        } catch (e: Exception) {
            Gdx.app.error("SoundManager", "Failed to load background_music.ogg: ${e.message}")
        }

        // Log status
        val loadedCount = listOf(coinDropSound, coinCollectSound, collisionSound, platformSound).count { it != null }
        if (loadedCount == 0) {
            Gdx.app.log("SoundManager", "No sound files found - running in silent mode. See assets/sounds/README.md")
        } else {
            Gdx.app.log("SoundManager", "Loaded $loadedCount/4 sound effects")
        }
    }

    fun playCoinDrop() {
        if (!soundEnabled) return

        val currentTime = System.currentTimeMillis() / 1000f
        if (currentTime - lastCoinDrop < soundCooldown) return
        lastCoinDrop = currentTime

        coinDropSound?.play(masterVolume * sfxVolume * 0.3f, 1.0f + (Math.random().toFloat() - 0.5f) * 0.2f, 0f)
            ?: playBeep(800f, 0.1f, 0.3f)
    }

    fun playCoinCollision(intensity: Float = 1.0f) {
        if (!soundEnabled) return

        val currentTime = System.currentTimeMillis() / 1000f
        if (currentTime - lastCollision < soundCooldown) return
        lastCollision = currentTime

        val volume = (intensity.coerceIn(0.1f, 1.0f) * masterVolume * sfxVolume * 0.2f)
        collisionSound?.play(volume, 1.0f + (Math.random().toFloat() - 0.5f) * 0.3f, 0f)
            ?: playBeep(600f, 0.05f, volume)
    }

    fun playCoinCollect() {
        if (!soundEnabled) return

        coinCollectSound?.play(masterVolume * sfxVolume * 0.6f, 1.2f, 0f)
            ?: playBeep(1200f, 0.2f, 0.6f)
    }

    fun playPlatformMove() {
        if (!soundEnabled) return
        platformSound?.play(masterVolume * sfxVolume * 0.1f, 0.9f, 0f)
    }

    fun playBigWin() {
        if (!soundEnabled) return

        // Use big_win sound if available, otherwise ascending beeps
        platformSound?.play(masterVolume * sfxVolume * 0.8f, 1.0f, 0f)
            ?: run {
                // Fallback: Play ascending notes for celebration
                playBeep(800f, 0.1f, 0.5f)
                playBeep(1000f, 0.1f, 0.5f)
                playBeep(1200f, 0.2f, 0.7f)
            }
    }

    /**
     * Fallback beep generator for when audio files aren't available
     * In production, replace with actual sound files
     */
    private fun playBeep(frequency: Float, duration: Float, volume: Float) {
        // This is a placeholder - libGDX doesn't have built-in beep generation
        // In a real implementation, you would:
        // 1. Use pre-recorded sound files from assets
        // 2. Or use an external library like TinySound for generation
        // For now, just log that sound would play
        if (Gdx.app.logLevel >= Gdx.app.LOG_DEBUG) {
            Gdx.app.debug("SoundManager", "Beep: ${frequency}Hz, ${duration}s, vol:$volume")
        }
    }

    fun playBackgroundMusic() {
        if (!musicEnabled) return
        backgroundMusic?.apply {
            isLooping = true
            volume = masterVolume * musicVolume
            play()
        }
    }

    fun stopBackgroundMusic() {
        backgroundMusic?.stop()
    }

    private fun updateMusicVolume() {
        backgroundMusic?.volume = masterVolume * musicVolume
    }

    fun pauseAll() {
        backgroundMusic?.pause()
    }

    fun resumeAll() {
        if (musicEnabled) {
            backgroundMusic?.play()
        }
    }

    override fun dispose() {
        coinDropSound?.dispose()
        coinCollectSound?.dispose()
        platformSound?.dispose()
        collisionSound?.dispose()
        backgroundMusic?.dispose()
        Gdx.app.log("SoundManager", "Sound system disposed")
    }

    companion object {
        /**
         * Instructions for adding sound files:
         *
         * 1. Create folder: android/assets/sounds/
         * 2. Add these files (WAV or OGG format):
         *    - coin_drop.wav (short metallic clink)
         *    - coin_collect.wav (reward chime)
         *    - coin_collision.wav (metallic jangle)
         *    - platform_move.wav (subtle mechanical sound)
         *    - background_music.ogg (looping arcade music)
         *
         * 3. Free sound resources:
         *    - OpenGameArt.org
         *    - Freesound.org
         *    - Mixkit.co
         *    - Zapsplat.com
         */
    }
}
