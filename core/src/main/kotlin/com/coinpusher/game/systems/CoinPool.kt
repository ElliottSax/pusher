package com.coinpusher.game.systems

import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.coinpusher.game.entities.Coin

/**
 * Object pool for coins to avoid garbage collection
 * Pre-allocates coins for optimal performance
 *
 * Based on research: Object pooling can improve performance from <2fps to 200+fps
 * for games with hundreds of objects
 */
class CoinPool(private val world: World, initialSize: Int = 500) {

    private val freeCoins = Array<Coin>(initialSize)
    private val activeCoins = Array<Coin>()

    companion object {
        const val MAX_COINS = 600 // Performance limit for mobile
    }

    init {
        // Pre-allocate coin objects
        for (i in 0 until initialSize) {
            freeCoins.add(Coin())
        }
    }

    /**
     * Get a coin from the pool or create a new one
     */
    fun obtain(x: Float, y: Float, type: Coin.CoinType = Coin.CoinType.GOLD): Coin? {
        // Check coin limit for performance
        if (activeCoins.size >= MAX_COINS) {
            return null
        }

        val coin = if (freeCoins.size > 0) {
            freeCoins.pop()
        } else {
            Coin()
        }

        coin.create(world, x, y, type)
        activeCoins.add(coin)
        return coin
    }

    /**
     * Return a coin to the pool
     */
    fun free(coin: Coin) {
        if (activeCoins.removeValue(coin, true)) {
            // Remove from physics world
            coin.body?.let { body ->
                world.destroyBody(body)
            }
            coin.reset()
            freeCoins.add(coin)
        }
    }

    /**
     * Free all collected coins
     */
    fun freeCollectedCoins(): Int {
        var count = 0
        val coinsToFree = Array<Coin>()

        for (coin in activeCoins) {
            if (coin.collected) {
                coinsToFree.add(coin)
                count++
            }
        }

        for (coin in coinsToFree) {
            free(coin)
        }

        return count
    }

    /**
     * Free coins that are out of bounds
     */
    fun freeOutOfBoundsCoins(minY: Float): Int {
        var count = 0
        val coinsToFree = Array<Coin>()

        for (coin in activeCoins) {
            val pos = coin.getPosition()
            if (pos != null && pos.y < minY) {
                coinsToFree.add(coin)
                count++
            }
        }

        for (coin in coinsToFree) {
            free(coin)
        }

        return count
    }

    fun getActiveCoins(): Array<Coin> = activeCoins

    fun getActiveCoinCount(): Int = activeCoins.size

    fun getFreeCoinCount(): Int = freeCoins.size

    fun dispose() {
        // Clear all coins
        for (coin in activeCoins) {
            coin.body?.let { body ->
                world.destroyBody(body)
            }
            coin.reset()
        }
        activeCoins.clear()
        freeCoins.clear()
    }
}
