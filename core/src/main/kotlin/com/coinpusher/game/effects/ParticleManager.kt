package com.coinpusher.game.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Manages particle effects for visual polish
 * Optimized for mobile - keeps particle count under 400 for 60fps
 */
class ParticleManager {

    private val particles = Array<Particle>()
    private val particlePool = Array<Particle>(400)

    companion object {
        const val MAX_PARTICLES = 400 // Mobile performance limit
    }

    init {
        // Pre-allocate particles for pooling
        for (i in 0 until MAX_PARTICLES) {
            particlePool.add(Particle())
        }
    }

    /**
     * Create a sparkle effect when coin is dropped
     */
    fun createCoinDropEffect(x: Float, y: Float, color: Color) {
        for (i in 0 until 10) {
            spawnParticle(x, y, color, ParticleType.SPARKLE)
        }
    }

    /**
     * Create an explosion effect when coins are collected
     */
    fun createCollectionEffect(x: Float, y: Float, color: Color) {
        for (i in 0 until 30) {
            spawnParticle(x, y, color, ParticleType.EXPLOSION)
        }
    }

    /**
     * Create ambient dust particles
     */
    fun createAmbientDust(x: Float, y: Float) {
        if (particles.size < MAX_PARTICLES - 5) {
            for (i in 0 until 3) {
                spawnParticle(x, y, Color.LIGHT_GRAY, ParticleType.DUST)
            }
        }
    }

    private fun spawnParticle(x: Float, y: Float, color: Color, type: ParticleType) {
        if (particles.size >= MAX_PARTICLES) return

        val particle = if (particlePool.size > 0) {
            particlePool.pop()
        } else {
            return // No more particles available
        }

        particle.spawn(x, y, color, type)
        particles.add(particle)
    }

    fun update(delta: Float) {
        val deadParticles = Array<Particle>()

        for (particle in particles) {
            particle.update(delta)
            if (particle.isDead()) {
                deadParticles.add(particle)
            }
        }

        // Return dead particles to pool
        for (particle in deadParticles) {
            particles.removeValue(particle, true)
            particle.reset()
            particlePool.add(particle)
        }
    }

    fun render(renderer: ShapeRenderer) {
        for (particle in particles) {
            particle.render(renderer)
        }
    }

    fun getActiveParticleCount(): Int = particles.size

    fun dispose() {
        particles.clear()
        particlePool.clear()
    }
}

enum class ParticleType {
    SPARKLE,
    EXPLOSION,
    DUST
}

class Particle {
    private var position = Vector2()
    private var velocity = Vector2()
    private var color = Color.WHITE
    private var alpha = 1f
    private var life = 1f
    private var maxLife = 1f
    private var size = 2f
    private var active = false

    fun spawn(x: Float, y: Float, particleColor: Color, type: ParticleType) {
        position.set(x, y)
        color = particleColor.cpy()
        active = true

        when (type) {
            ParticleType.SPARKLE -> {
                velocity.set(
                    MathUtils.random(-50f, 50f),
                    MathUtils.random(20f, 100f)
                )
                maxLife = 0.5f
                size = MathUtils.random(2f, 5f)
            }
            ParticleType.EXPLOSION -> {
                val angle = MathUtils.random(0f, 360f)
                val speed = MathUtils.random(50f, 200f)
                velocity.set(
                    MathUtils.cosDeg(angle) * speed,
                    MathUtils.sinDeg(angle) * speed
                )
                maxLife = 1.0f
                size = MathUtils.random(3f, 8f)
            }
            ParticleType.DUST -> {
                velocity.set(
                    MathUtils.random(-10f, 10f),
                    MathUtils.random(10f, 30f)
                )
                maxLife = 2.0f
                size = MathUtils.random(1f, 3f)
                color.set(Color.LIGHT_GRAY)
            }
        }

        life = maxLife
        alpha = 1f
    }

    fun update(delta: Float) {
        if (!active) return

        // Update position
        position.add(velocity.x * delta, velocity.y * delta)

        // Apply gravity
        velocity.y -= 98f * delta

        // Fade out
        life -= delta
        alpha = (life / maxLife).coerceIn(0f, 1f)

        if (life <= 0) {
            active = false
        }
    }

    fun render(renderer: ShapeRenderer) {
        if (!active) return

        renderer.color = Color(color.r, color.g, color.b, alpha)
        renderer.circle(position.x, position.y, size, 6)
    }

    fun isDead(): Boolean = !active

    fun reset() {
        active = false
        life = 0f
        alpha = 0f
    }
}
