package com.coinpusher.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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
import com.coinpusher.game.audio.SoundManager
import com.coinpusher.game.data.GameData
import com.coinpusher.game.effects.ParticleManager
import com.coinpusher.game.effects.ScreenEffects
import com.coinpusher.game.entities.Coin
import com.coinpusher.game.entities.GameBounds
import com.coinpusher.game.entities.Platform
import com.coinpusher.game.physics.PhysicsWorld
import com.coinpusher.game.systems.CoinPool
import com.coinpusher.game.ui.GameHUD

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

    // New systems
    private val soundManager: SoundManager
    private val particleManager: ParticleManager
    private val screenEffects: ScreenEffects
    private val hud: GameHUD
    private val gameData: GameData

    private var score = 0
    private var sessionStartTime = 0L
    private var lastCoinSpawn = 0f
    private val coinSpawnDelay = 0.5f
    private var lastCollectedValue = 0

    init {
        // Setup camera
        camera = OrthographicCamera()
        camera.setToOrtho(false, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT)
        viewport = FitViewport(CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT, camera)

        // Initialize systems
        gameData = GameData.getInstance()
        soundManager = SoundManager()
        particleManager = ParticleManager()
        screenEffects = ScreenEffects(camera)
        hud = GameHUD(game)

        // Apply saved settings
        soundManager.masterVolume = gameData.masterVolume
        soundManager.sfxVolume = gameData.sfxVolume
        soundManager.musicVolume = gameData.musicVolume
        soundManager.soundEnabled = gameData.soundEnabled
        soundManager.musicEnabled = gameData.musicEnabled
        screenEffects.setVibrationEnabled(gameData.vibrationEnabled)

        // Initialize physics
        physicsWorld = PhysicsWorld()
        physicsWorld.soundManager = soundManager
        physicsWorld.particleManager = particleManager
        physicsWorld.screenEffects = screenEffects

        // Create game objects
        gameBounds = GameBounds(physicsWorld.world)
        platform = Platform(physicsWorld.world)
        coinPool = CoinPool(physicsWorld.world)

        // Setup input
        Gdx.input.inputProcessor = GestureDetector(this)
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        // Start session
        sessionStartTime = System.currentTimeMillis()
        gameData.gamesPlayed++

        // Set HUD high score
        hud.setHighScore(gameData.highScore)

        Gdx.app.log("GameScreen", "Initialized - Ready to play!")
    }

    override fun show() {}

    override fun render(delta: Float) {
        // Handle back button
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            returnToMenu()
            return
        }

        // Update with time scale from screen effects
        val scaledDelta = delta * screenEffects.getTimeScale()
        update(scaledDelta)

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera
        camera.update()

        // Render game
        renderGame()

        // Render particles
        renderParticles()

        // Render UI
        renderUI()

        // Render flash effect
        renderFlash()
    }

    private fun update(delta: Float) {
        // Update systems
        screenEffects.update(delta)
        particleManager.update(delta)

        // Update physics
        physicsWorld.update(delta)

        // Update platform
        platform.update(delta)

        // Collect coins and update score
        val collectedCoins = mutableListOf<Coin>()
        for (coin in coinPool.getActiveCoins()) {
            if (coin.collected) {
                collectedCoins.add(coin)
                score += coin.getValue()
                lastCollectedValue = coin.getValue()

                // Effects for collection
                val pos = coin.getPosition()
                if (pos != null) {
                    particleManager.createCollectionEffect(pos.x * CoinPusherGame.PPM, pos.y * CoinPusherGame.PPM, coin.color)
                    soundManager.playCoinCollect()

                    if (coin.getValue() >= 50) {
                        // Big win effects
                        screenEffects.shake(0.8f, 0.5f)
                        screenEffects.flash(0.5f, 0.3f)
                        screenEffects.vibrate(100)
                        soundManager.playBigWin()
                    } else {
                        screenEffects.shake(0.2f, 0.15f)
                        screenEffects.vibrate(30)
                    }
                }
            }
        }

        // Free collected coins
        for (coin in collectedCoins) {
            coinPool.free(coin)
            gameData.addCoins(1)
        }

        // Update high score
        if (score > 0) {
            gameData.updateHighScore(score)
        }

        // Remove coins that fell off screen
        coinPool.freeOutOfBoundsCoins(-1f)

        // Update HUD
        hud.update(score, gameData.coins, coinPool.getActiveCoinCount(),
                   particleManager.getActiveParticleCount(), Gdx.graphics.framesPerSecond)
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

    private fun renderParticles() {
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        particleManager.render(game.shapeRenderer)
        game.shapeRenderer.end()
    }

    private fun renderUI() {
        hud.render(game.batch, game.shapeRenderer)
    }

    private fun renderFlash() {
        val flashAlpha = screenEffects.getFlashAlpha()
        if (flashAlpha > 0f) {
            game.shapeRenderer.projectionMatrix = camera.combined
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            game.shapeRenderer.color = Color(1f, 1f, 1f, flashAlpha)
            game.shapeRenderer.rect(0f, 0f, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT)
            game.shapeRenderer.end()
        }
    }

    private fun spawnCoin(x: Float) {
        if (gameData.coins <= 0) {
            Gdx.app.log("GameScreen", "No coins left!")
            return
        }

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
            gameData.spendCoins(1)

            // Effects for dropping
            soundManager.playCoinDrop()
            particleManager.createCoinDropEffect(x, worldY * CoinPusherGame.PPM, type.color)
            screenEffects.shake(0.1f, 0.1f)
            screenEffects.vibrate(20)

            Gdx.app.log("GameScreen", "Spawned ${type.name} coin at $worldX, $worldY")
        } else {
            Gdx.app.log("GameScreen", "Failed to spawn coin - pool limit reached")
        }
    }

    private fun returnToMenu() {
        // Save session time
        val sessionTime = (System.currentTimeMillis() - sessionStartTime) / 1000
        gameData.totalPlayTime += sessionTime

        Gdx.app.log("GameScreen", "Returning to menu. Final score: $score")
        game.setScreen(MenuScreen(game))
        dispose()
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
        soundManager.dispose()
        particleManager.dispose()
        hud.dispose()
        Gdx.app.log("GameScreen", "Screen disposed")
    }
}
