package com.coinpusher.game.physics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.*
import com.coinpusher.game.CoinPusherGame
import com.coinpusher.game.entities.Coin

/**
 * Handles collision events between physics bodies
 * Manages coin-to-coin, coin-to-platform, and coin-to-wall collisions
 */
class CollisionListener(private val physicsWorld: PhysicsWorld) : ContactListener {

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        val userDataA = bodyA.userData
        val userDataB = bodyB.userData

        // Check for coin collisions
        when {
            userDataA is Coin && userDataB is Coin -> {
                handleCoinCollision(userDataA, userDataB, contact)
            }
            userDataA is Coin && userDataB is String -> {
                if (userDataB == "platform") handleCoinPlatformCollision(userDataA)
                if (userDataB == "collector") handleCoinCollected(userDataA)
            }
            userDataB is Coin && userDataA is String -> {
                if (userDataA == "platform") handleCoinPlatformCollision(userDataB)
                if (userDataA == "collector") handleCoinCollected(userDataB)
            }
        }
    }

    override fun endContact(contact: Contact) {
        // Handle separation if needed
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        // Modify collision behavior before solving
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        // Handle post-collision effects (sound, particles)
        // Safety check: ensure impulse array has elements
        if (impulse.normalImpulses.size == 0) return

        val impulseValue = impulse.normalImpulses[0]
        if (impulseValue > 0.5f) {
            // Play collision sound for significant impacts
            physicsWorld.soundManager?.playCoinCollision(impulseValue / 10f)

            // Create particle effects at collision point
            val worldManifold = contact.worldManifold
            if (worldManifold.numberOfContactPoints > 0) {
                val point = worldManifold.points[0]
                physicsWorld.particleManager?.createCoinDropEffect(
                    point.x * CoinPusherGame.PPM,
                    point.y * CoinPusherGame.PPM,
                    Color.GOLD
                )
            }
        }
    }

    private fun handleCoinCollision(coinA: Coin, coinB: Coin, contact: Contact) {
        // Coin-to-coin collision
        // Coins should have slight bounce and generate sound
    }

    private fun handleCoinPlatformCollision(coin: Coin) {
        // Coin landed on platform
        coin.onPlatform = true
    }

    private fun handleCoinCollected(coin: Coin) {
        // Coin fell into collection area
        coin.collected = true
    }
}
