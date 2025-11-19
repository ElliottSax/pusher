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
        // For now, we'll use programmatically generated sounds
        // In production, load from assets folder
        generateSounds()
        Gdx.app.log("SoundManager", "Sound system initialized")
    }

    private fun generateSounds() {
        // Note: libGDX doesn't support procedural sound generation easily
        // These would normally be loaded from assets:
        // coinDropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_drop.wav"))
        // For now, we'll use a fallback system without actual sounds
        Gdx.app.log("SoundManager", "Sound generation placeholder - add WAV files to assets/sounds/")
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
        // Play ascending notes for celebration
        playBeep(800f, 0.1f, 0.5f)
        playBeep(1000f, 0.1f, 0.5f)
        playBeep(1200f, 0.2f, 0.7f)
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
