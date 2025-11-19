package com.coinpusher.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.coinpusher.game.screens.GameScreen

/**
 * Main game class for Coin Pusher
 * Manages screens, global resources, and game state
 */
class CoinPusherGame : Game() {

    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var font: BitmapFont

    companion object {
        const val GAME_WIDTH = 720f
        const val GAME_HEIGHT = 1280f
        const val PPM = 100f // Pixels per meter for Box2D
    }

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        font = BitmapFont()

        Gdx.app.log("CoinPusher", "Game initialized")

        // Start with the main game screen
        setScreen(GameScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        font.dispose()
        screen?.dispose()
    }
}
