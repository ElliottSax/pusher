package com.coinpusher.game.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.coinpusher.game.CoinPusherGame

/**
 * Manages the Box2D physics world
 * Uses reduced gravity (10% of Earth's) for realistic coin behavior
 */
class PhysicsWorld {

    val world: World
    private val timeStep = 1/60f
    private val velocityIterations = 8
    private val positionIterations = 3

    companion object {
        // 10% of Earth's gravity for realistic coin stacking (based on research)
        const val GRAVITY = -0.981f
    }

    init {
        world = World(Vector2(0f, GRAVITY), true)
        setupContactListener()
    }

    private fun setupContactListener() {
        world.setContactListener(CollisionListener())
    }

    fun update(delta: Float) {
        // Fixed timestep for consistent physics
        world.step(timeStep, velocityIterations, positionIterations)
    }

    fun dispose() {
        world.dispose()
    }
}
