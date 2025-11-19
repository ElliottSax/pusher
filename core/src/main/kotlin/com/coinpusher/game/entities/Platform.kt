package com.coinpusher.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*

/**
 * The moving platform that pushes coins
 * Oscillates back and forth to create the classic coin pusher effect
 */
class Platform(private val world: World) {

    var body: Body? = null
    private var movingForward = true
    private var currentPosition = 0f

    companion object {
        const val PLATFORM_WIDTH = 4.0f
        const val PLATFORM_HEIGHT = 0.2f
        const val PLATFORM_Y = 2.0f
        const val MOVEMENT_RANGE = 1.5f // How far the platform moves
        const val MOVEMENT_SPEED = 0.8f // Speed of movement
    }

    init {
        createPlatform()
    }

    private fun createPlatform() {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.KinematicBody
            position.set(3.6f, PLATFORM_Y)
        }

        body = world.createBody(bodyDef).apply {
            val box = PolygonShape().apply {
                setAsBox(PLATFORM_WIDTH / 2, PLATFORM_HEIGHT / 2)
            }

            val fixtureDef = FixtureDef().apply {
                shape = box
                density = 1.0f
                friction = 0.6f
            }

            createFixture(fixtureDef)
            box.dispose()

            userData = "platform"
        }
    }

    fun update(delta: Float) {
        body?.let { b ->
            // Oscillating movement
            if (movingForward) {
                currentPosition += MOVEMENT_SPEED * delta
                if (currentPosition >= MOVEMENT_RANGE) {
                    currentPosition = MOVEMENT_RANGE
                    movingForward = false
                }
            } else {
                currentPosition -= MOVEMENT_SPEED * delta
                if (currentPosition <= 0f) {
                    currentPosition = 0f
                    movingForward = true
                }
            }

            // Set velocity for smooth movement
            val targetY = PLATFORM_Y
            val velocity = if (movingForward) MOVEMENT_SPEED else -MOVEMENT_SPEED
            b.linearVelocity.set(velocity, 0f)
        }
    }

    fun render(renderer: ShapeRenderer, ppm: Float) {
        body?.let { b ->
            val pos = b.position

            renderer.color = Color.DARK_GRAY
            renderer.rect(
                (pos.x - PLATFORM_WIDTH / 2) * ppm,
                (pos.y - PLATFORM_HEIGHT / 2) * ppm,
                PLATFORM_WIDTH * ppm,
                PLATFORM_HEIGHT * ppm
            )

            // Add visual detail - stripes
            renderer.color = Color.GRAY
            for (i in 0..8) {
                val x = (pos.x - PLATFORM_WIDTH / 2 + i * 0.4f) * ppm
                renderer.rectLine(
                    x,
                    (pos.y - PLATFORM_HEIGHT / 2) * ppm,
                    x,
                    (pos.y + PLATFORM_HEIGHT / 2) * ppm,
                    2f
                )
            }
        }
    }

    fun dispose() {
        body?.let { world.destroyBody(it) }
    }
}
