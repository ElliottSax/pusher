package com.coinpusher.game.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Screen effects like shake, flash, slow motion
 * Adds juice and feel to the game
 */
class ScreenEffects(private val camera: OrthographicCamera) {

    private var shakeTime = 0f
    private var shakePower = 0f
    private val originalPosition = Vector2()

    private var slowMotionTime = 0f
    private var slowMotionFactor = 1.0f

    private var flashTime = 0f
    private var flashAlpha = 0f

    // Vibration
    private var vibrationEnabled = true

    init {
        originalPosition.set(camera.position.x, camera.position.y)
    }

    /**
     * Trigger camera shake
     * @param power Intensity of shake (0.1 to 2.0)
     * @param duration Duration in seconds
     */
    fun shake(power: Float = 0.5f, duration: Float = 0.3f) {
        shakePower = power
        shakeTime = duration
        Gdx.app.log("ScreenEffects", "Shake: power=$power, duration=$duration")
    }

    /**
     * Trigger slow motion effect
     * @param factor Speed multiplier (0.1 to 1.0, lower = slower)
     * @param duration Duration in seconds
     */
    fun slowMotion(factor: Float = 0.5f, duration: Float = 0.5f) {
        slowMotionFactor = factor.coerceIn(0.1f, 1.0f)
        slowMotionTime = duration
        Gdx.app.log("ScreenEffects", "SlowMo: factor=$factor, duration=$duration")
    }

    /**
     * Trigger screen flash
     * @param intensity Flash brightness (0.0 to 1.0)
     * @param duration Duration in seconds
     */
    fun flash(intensity: Float = 0.5f, duration: Float = 0.2f) {
        flashAlpha = intensity.coerceIn(0f, 1f)
        flashTime = duration
    }

    /**
     * Trigger vibration on supported devices
     * @param duration Duration in milliseconds
     */
    fun vibrate(duration: Int = 50) {
        if (vibrationEnabled) {
            try {
                Gdx.input.vibrate(duration)
            } catch (e: Exception) {
                Gdx.app.error("ScreenEffects", "Vibration not supported: ${e.message}")
            }
        }
    }

    /**
     * Update effects
     */
    fun update(delta: Float) {
        updateShake(delta)
        updateSlowMotion(delta)
        updateFlash(delta)
    }

    private fun updateShake(delta: Float) {
        if (shakeTime > 0) {
            // Random offset based on shake power
            val offsetX = MathUtils.random(-shakePower, shakePower)
            val offsetY = MathUtils.random(-shakePower, shakePower)

            camera.position.x = originalPosition.x + offsetX
            camera.position.y = originalPosition.y + offsetY

            shakeTime -= delta

            if (shakeTime <= 0) {
                // Reset to original position
                camera.position.x = originalPosition.x
                camera.position.y = originalPosition.y
            }
        }
    }

    private fun updateSlowMotion(delta: Float) {
        if (slowMotionTime > 0) {
            slowMotionTime -= delta
            if (slowMotionTime <= 0) {
                slowMotionFactor = 1.0f
            }
        }
    }

    private fun updateFlash(delta: Float) {
        if (flashTime > 0) {
            flashTime -= delta
            flashAlpha = (flashTime / 0.2f).coerceIn(0f, 1f)
        }
    }

    /**
     * Get current time scale for slow motion
     */
    fun getTimeScale(): Float {
        return slowMotionFactor
    }

    /**
     * Get flash alpha for rendering
     */
    fun getFlashAlpha(): Float {
        return flashAlpha
    }

    /**
     * Check if any effect is active
     */
    fun isActive(): Boolean {
        return shakeTime > 0 || slowMotionTime > 0 || flashTime > 0
    }

    fun setVibrationEnabled(enabled: Boolean) {
        vibrationEnabled = enabled
    }

    /**
     * Predefined effects for common game events
     */
    object Presets {
        fun coinDrop() = Pair(0.1f, 0.1f)  // Subtle shake
        fun coinCollect() = Pair(0.2f, 0.15f)  // Light shake
        fun bigWin() = Pair(0.8f, 0.5f)  // Strong shake
        fun cascade() = Pair(0.5f, 0.3f)  // Medium shake
    }
}
