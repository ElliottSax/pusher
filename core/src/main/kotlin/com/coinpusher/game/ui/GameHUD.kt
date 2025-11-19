package com.coinpusher.game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import com.coinpusher.game.CoinPusherGame

/**
 * Heads-Up Display for the game screen
 * Shows score, coins, stats, and controls
 */
class GameHUD(private val game: CoinPusherGame) : Disposable {

    private val font: BitmapFont = BitmapFont()
    private val largeFontScale = 1.5f
    private val normalFontScale = 1.0f
    private val smallFontScale = 0.8f

    private var score = 0
    private var coins = 100
    private var highScore = 0
    private var activeCoins = 0
    private var activeParticles = 0
    private var fps = 60

    // UI positioning
    private val padding = 20f
    private val topY = CoinPusherGame.GAME_HEIGHT - padding

    // Colors
    private val goldColor = Color(1f, 0.84f, 0f, 1f)
    private val whiteColor = Color.WHITE
    private val grayColor = Color.LIGHT_GRAY

    init {
        font.color = whiteColor
        font.data.setScale(normalFontScale)
    }

    fun update(score: Int, coins: Int, activeCoins: Int, activeParticles: Int, fps: Int) {
        this.score = score
        this.coins = coins
        this.activeCoins = activeCoins
        this.activeParticles = activeParticles
        this.fps = fps

        if (score > highScore) {
            highScore = score
        }
    }

    fun render(batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        // Draw background panels
        renderBackgroundPanels(shapeRenderer)

        // Draw text
        batch.begin()
        renderTopBar(batch)
        renderBottomInstructions(batch)
        renderStats(batch)
        batch.end()
    }

    private fun renderBackgroundPanels(renderer: ShapeRenderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        // Top bar background
        renderer.color = Color(0f, 0f, 0f, 0.7f)
        renderer.rect(0f, topY - 100f, CoinPusherGame.GAME_WIDTH, 120f)

        // Bottom instructions background
        renderer.color = Color(0f, 0f, 0f, 0.5f)
        renderer.rect(0f, 0f, CoinPusherGame.GAME_WIDTH, 150f)

        // Stats panel background (right side)
        renderer.color = Color(0f, 0f, 0f, 0.3f)
        renderer.rect(CoinPusherGame.GAME_WIDTH - 200f,
                     CoinPusherGame.GAME_HEIGHT / 2 - 100f,
                     180f, 200f)

        renderer.end()
    }

    private fun renderTopBar(batch: SpriteBatch) {
        var y = topY

        // Score (large, gold)
        font.color = goldColor
        font.data.setScale(largeFontScale)
        font.draw(batch, "SCORE", padding, y)
        font.draw(batch, score.toString(), padding, y - 35f)

        // High Score (smaller, white)
        font.color = whiteColor
        font.data.setScale(smallFontScale)
        font.draw(batch, "Best: $highScore", padding, y - 70f)

        // Coins available (right side)
        font.data.setScale(normalFontScale)
        font.color = whiteColor
        val coinsText = "Coins: $coins"
        val coinsWidth = getTextWidth(coinsText)
        font.draw(batch, coinsText, CoinPusherGame.GAME_WIDTH - padding - coinsWidth, y)

        // Coin icon representation
        font.color = goldColor
        font.draw(batch, "🪙", CoinPusherGame.GAME_WIDTH - padding - coinsWidth - 30f, y)

        font.data.setScale(normalFontScale)
    }

    private fun renderBottomInstructions(batch: SpriteBatch) {
        font.color = whiteColor
        font.data.setScale(normalFontScale)

        val instructionY = 100f

        // Main instruction
        font.color = whiteColor
        val mainText = "TAP TO DROP COINS"
        val mainWidth = getTextWidth(mainText)
        font.draw(batch, mainText, CoinPusherGame.GAME_WIDTH / 2 - mainWidth / 2, instructionY)

        // Tips
        font.color = grayColor
        font.data.setScale(smallFontScale)
        val tipText = "Collect coins that fall off the edge!"
        val tipWidth = getTextWidth(tipText)
        font.draw(batch, tipText, CoinPusherGame.GAME_WIDTH / 2 - tipWidth / 2, instructionY - 30f)

        // Coin values
        font.color = Color.GOLD
        font.draw(batch, "Gold=1", padding + 10f, 50f)
        font.color = Color.LIGHT_GRAY
        font.draw(batch, "Silver=5", padding + 100f, 50f)
        font.color = Color.BROWN
        font.draw(batch, "Bronze=10", padding + 200f, 50f)
        font.color = Color.CYAN
        font.draw(batch, "Special=50", padding + 310f, 50f)

        font.data.setScale(normalFontScale)
    }

    private fun renderStats(batch: SpriteBatch) {
        font.color = grayColor
        font.data.setScale(smallFontScale)

        val x = CoinPusherGame.GAME_WIDTH - 190f
        var y = CoinPusherGame.GAME_HEIGHT / 2 + 80f

        font.draw(batch, "STATS", x + 60f, y)
        y -= 30f

        font.draw(batch, "FPS: $fps", x, y)
        y -= 25f

        font.draw(batch, "Active Coins: $activeCoins", x, y)
        y -= 25f

        font.draw(batch, "Particles: $activeParticles", x, y)
        y -= 25f

        // Performance indicator
        val perfColor = when {
            fps >= 55 -> Color.GREEN
            fps >= 40 -> Color.YELLOW
            else -> Color.RED
        }
        font.color = perfColor
        val perfText = when {
            fps >= 55 -> "Excellent"
            fps >= 40 -> "Good"
            else -> "Low"
        }
        font.draw(batch, "Perf: $perfText", x, y)

        font.data.setScale(normalFontScale)
    }

    private fun getTextWidth(text: String): Float {
        val layout = com.badlogic.gdx.graphics.g2d.GlyphLayout()
        layout.setText(font, text)
        return layout.width
    }

    fun setHighScore(score: Int) {
        highScore = score
    }

    override fun dispose() {
        font.dispose()
    }
}
