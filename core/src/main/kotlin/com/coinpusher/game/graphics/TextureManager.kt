package com.coinpusher.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable

/**
 * Manages game textures with lazy loading and caching
 * Provides fallback to shape rendering if textures are missing
 */
class TextureManager : Disposable {

    // Coin textures
    var coinGoldTexture: Texture? = null
        private set
    var coinSilverTexture: Texture? = null
        private set
    var coinBronzeTexture: Texture? = null
        private set
    var coinSpecialTexture: Texture? = null
        private set

    // Texture availability flags
    val hasTextures: Boolean
        get() = coinGoldTexture != null

    init {
        loadTextures()
    }

    private fun loadTextures() {
        // Try to load coin textures with graceful fallback
        try {
            if (Gdx.files.internal("textures/coin_gold.png").exists()) {
                coinGoldTexture = Texture(Gdx.files.internal("textures/coin_gold.png"))
                coinGoldTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                Gdx.app.log("TextureManager", "Loaded: coin_gold.png")
            }
        } catch (e: Exception) {
            Gdx.app.error("TextureManager", "Failed to load coin_gold.png: ${e.message}")
        }

        try {
            if (Gdx.files.internal("textures/coin_silver.png").exists()) {
                coinSilverTexture = Texture(Gdx.files.internal("textures/coin_silver.png"))
                coinSilverTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                Gdx.app.log("TextureManager", "Loaded: coin_silver.png")
            }
        } catch (e: Exception) {
            Gdx.app.error("TextureManager", "Failed to load coin_silver.png: ${e.message}")
        }

        try {
            if (Gdx.files.internal("textures/coin_bronze.png").exists()) {
                coinBronzeTexture = Texture(Gdx.files.internal("textures/coin_bronze.png"))
                coinBronzeTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                Gdx.app.log("TextureManager", "Loaded: coin_bronze.png")
            }
        } catch (e: Exception) {
            Gdx.app.error("TextureManager", "Failed to load coin_bronze.png: ${e.message}")
        }

        try {
            if (Gdx.files.internal("textures/coin_special.png").exists()) {
                coinSpecialTexture = Texture(Gdx.files.internal("textures/coin_special.png"))
                coinSpecialTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                Gdx.app.log("TextureManager", "Loaded: coin_special.png")
            }
        } catch (e: Exception) {
            Gdx.app.error("TextureManager", "Failed to load coin_special.png: ${e.message}")
        }

        // Log status
        val loadedCount = listOf(
            coinGoldTexture,
            coinSilverTexture,
            coinBronzeTexture,
            coinSpecialTexture
        ).count { it != null }

        if (loadedCount == 0) {
            Gdx.app.log("TextureManager", "No textures found - using shape rendering. See assets/textures/README.md")
        } else if (loadedCount < 4) {
            Gdx.app.log("TextureManager", "Loaded $loadedCount/4 textures - some coins will use shapes")
        } else {
            Gdx.app.log("TextureManager", "All textures loaded successfully")
        }
    }

    /**
     * Get texture for a specific coin type
     */
    fun getCoinTexture(coinType: com.coinpusher.game.entities.Coin.CoinType): Texture? {
        return when (coinType) {
            com.coinpusher.game.entities.Coin.CoinType.GOLD -> coinGoldTexture
            com.coinpusher.game.entities.Coin.CoinType.SILVER -> coinSilverTexture
            com.coinpusher.game.entities.Coin.CoinType.BRONZE -> coinBronzeTexture
            com.coinpusher.game.entities.Coin.CoinType.SPECIAL -> coinSpecialTexture
        }
    }

    /**
     * Render a coin using its texture
     */
    fun renderCoinTexture(
        batch: SpriteBatch,
        coinType: com.coinpusher.game.entities.Coin.CoinType,
        x: Float,
        y: Float,
        radius: Float,
        rotation: Float = 0f
    ) {
        val texture = getCoinTexture(coinType) ?: return

        val size = radius * 2
        batch.draw(
            texture,
            x - radius,  // Center the texture
            y - radius,
            radius,      // Origin X (center)
            radius,      // Origin Y (center)
            size,        // Width
            size,        // Height
            1f,          // Scale X
            1f,          // Scale Y
            rotation,    // Rotation in degrees
            0,           // Source X
            0,           // Source Y
            texture.width,
            texture.height,
            false,
            false
        )
    }

    override fun dispose() {
        coinGoldTexture?.dispose()
        coinSilverTexture?.dispose()
        coinBronzeTexture?.dispose()
        coinSpecialTexture?.dispose()
        Gdx.app.log("TextureManager", "Textures disposed")
    }
}
