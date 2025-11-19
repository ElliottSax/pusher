package com.coinpusher.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.input.GestureDetector.GestureListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.coinpusher.game.CoinPusherGame
import com.coinpusher.game.entities.Coin
import com.coinpusher.game.entities.GameBounds
import com.coinpusher.game.entities.Platform
import com.coinpusher.game.physics.PhysicsWorld
import com.coinpusher.game.systems.CoinPool
import kotlin.math.min

/**
 * Main game screen where the coin pusher action happens
 */
class GameScreen(private val game: CoinPusherGame) : Screen, GestureListener {

    private val camera: OrthographicCamera
    private val viewport: FitViewport
    private val physicsWorld: PhysicsWorld
    private val coinPool: CoinPool
    private val platform: Platform
    private val gameBounds: GameBounds

    private var score = 0
    private var coinCount = 100
    private var lastCoinSpawn = 0f
    private val coinSpawnDelay = 0.5f // Seconds between auto-spawns

    init {
        // Setup camera
        camera = OrthographicCamera()
        camera.setToOrtho(false, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT)

        viewport = FitViewport(CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT, camera)

        // Initialize physics
        physicsWorld = PhysicsWorld()

        // Create game objects
        gameBounds = GameBounds(physicsWorld.world)
        platform = Platform(physicsWorld.world)
        coinPool = CoinPool(physicsWorld.world)

        // Setup input
        Gdx.input.inputProcessor = GestureDetector(this)

        Gdx.app.log("GameScreen", "Initialized - Ready to play!")
    }

    override fun show() {}

    override fun render(delta: Float) {
        // Update
        update(delta)

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera
        camera.update()

        // Render game
        renderGame()

        // Render UI
        renderUI()
    }

    private fun update(delta: Float) {
        // Update physics
        physicsWorld.update(delta)

        // Update platform
        platform.update(delta)

        // Auto-spawn coins for testing (optional)
        lastCoinSpawn += delta
        if (lastCoinSpawn >= coinSpawnDelay && coinCount > 0) {
            // Uncomment for auto-spawn:
            // spawnCoin(3.6f)
            // lastCoinSpawn = 0f
        }

        // Collect coins and update score
        val collectedCount = coinPool.freeCollectedCoins()
        if (collectedCount > 0) {
            score += collectedCount
            Gdx.app.log("GameScreen", "Collected $collectedCount coins! Score: $score")
        }

        // Remove coins that fell off screen
        coinPool.freeOutOfBoundsCoins(-1f)
    }

    private fun renderGame() {
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Render game bounds
        gameBounds.render(game.shapeRenderer, CoinPusherGame.PPM)

        // Render platform
        platform.render(game.shapeRenderer, CoinPusherGame.PPM)

        // Render coins
        for (coin in coinPool.getActiveCoins()) {
            coin.render(game.shapeRenderer, CoinPusherGame.PPM)
        }

        game.shapeRenderer.end()
    }

    private fun renderUI() {
        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        // Display score
        game.font.color = Color.WHITE
        game.font.draw(game.batch, "Score: $score", 20f, CoinPusherGame.GAME_HEIGHT - 20f)
        game.font.draw(game.batch, "Coins: $coinCount", 20f, CoinPusherGame.GAME_HEIGHT - 50f)
        game.font.draw(game.batch, "Active: ${coinPool.getActiveCoinCount()}", 20f, CoinPusherGame.GAME_HEIGHT - 80f)
        game.font.draw(game.batch, "FPS: ${Gdx.graphics.framesPerSecond}", 20f, CoinPusherGame.GAME_HEIGHT - 110f)

        // Instructions
        game.font.draw(game.batch, "Tap to drop coins!", CoinPusherGame.GAME_WIDTH / 2 - 80f, 100f)

        game.batch.end()
    }

    private fun spawnCoin(x: Float) {
        if (coinCount <= 0) return

        val worldX = x / CoinPusherGame.PPM
        val worldY = 11f // Spawn near top

        // Random coin type
        val random = Math.random()
        val type = when {
            random > 0.95 -> Coin.CoinType.SPECIAL
            random > 0.80 -> Coin.CoinType.BRONZE
            random > 0.50 -> Coin.CoinType.SILVER
            else -> Coin.CoinType.GOLD
        }

        val coin = coinPool.obtain(worldX, worldY, type)
        if (coin != null) {
            coinCount--
            Gdx.app.log("GameScreen", "Spawned ${type.name} coin at $worldX, $worldY")
        } else {
            Gdx.app.log("GameScreen", "Failed to spawn coin - pool limit reached")
        }
    }

    // Gesture handling
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        // Spawn coin at touch position
        spawnCoin(x)
        return true
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        spawnCoin(x)
        return true
    }

    override fun longPress(x: Float, y: Float): Boolean = false
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false
    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false
    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false
    override fun zoom(initialDistance: Float, distance: Float): Boolean = false
    override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = false
    override fun pinchStop() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        physicsWorld.dispose()
        coinPool.dispose()
        platform.dispose()
        gameBounds.dispose()
    }
}
