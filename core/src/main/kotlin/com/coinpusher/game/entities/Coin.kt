package com.coinpusher.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.coinpusher.game.CoinPusherGame

/**
 * Represents a coin in the game
 * Uses circular physics body for realistic rolling behavior
 */
class Coin {
    var body: Body? = null
    var active = false
    var collected = false
    var onPlatform = false
    var coinType = CoinType.GOLD
    var color = Color.GOLD

    companion object {
        const val COIN_RADIUS = 0.15f // meters
        const val COIN_DENSITY = 8.0f // High density for realistic weight
        const val COIN_FRICTION = 0.4f
        const val COIN_RESTITUTION = 0.2f // Low bounce
    }

    enum class CoinType(val value: Int, val color: Color) {
        GOLD(1, Color.GOLD),
        SILVER(5, Color.LIGHT_GRAY),
        BRONZE(10, Color.BROWN),
        SPECIAL(50, Color.CYAN)
    }

    fun create(world: World, x: Float, y: Float, type: CoinType = CoinType.GOLD) {
        coinType = type
        color = type.color

        // Body definition
        val bodyDef = BodyDef().apply {
            this.type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
            // Add slight random spin for variety
            angularVelocity = MathUtils.random(-2f, 2f)
        }

        body = world.createBody(bodyDef).apply {
            // Circular shape for realistic rolling
            val circle = CircleShape().apply {
                radius = COIN_RADIUS
            }

            val fixtureDef = FixtureDef().apply {
                shape = circle
                density = COIN_DENSITY
                friction = COIN_FRICTION
                restitution = COIN_RESTITUTION
            }

            createFixture(fixtureDef)
            circle.dispose()

            // Store reference to this coin
            userData = this@Coin
        }

        active = true
        collected = false
        onPlatform = false
    }

    fun reset() {
        body?.let { world ->
            // Will be properly disposed through world
        }
        body = null
        active = false
        collected = false
        onPlatform = false
    }

    fun render(renderer: ShapeRenderer, ppm: Float) {
        body?.let { b ->
            val pos = b.position
            val x = pos.x * ppm
            val y = pos.y * ppm
            val radius = COIN_RADIUS * ppm

            // Draw shadow for depth (offset, semi-transparent black)
            renderer.color = Color(0f, 0f, 0f, 0.3f)
            renderer.circle(x + 2f, y - 2f, radius, 18)

            // Draw base coin
            renderer.color = color
            renderer.circle(x, y, radius, 20)

            // Draw darker edge ring for 3D effect
            renderer.color = Color(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, 1f)
            renderer.circle(x, y, radius * 0.85f, 18)

            // Draw center highlight (brighter, smaller)
            val highlightR = Math.min(color.r * 1.4f, 1f)
            val highlightG = Math.min(color.g * 1.4f, 1f)
            val highlightB = Math.min(color.b * 1.4f, 1f)
            renderer.color = Color(highlightR, highlightG, highlightB, 1f)
            renderer.circle(x, y, radius * 0.5f, 12)
        }
    }

    fun getPosition(): Vector2? {
        return body?.position
    }

    fun getValue(): Int = coinType.value
}
