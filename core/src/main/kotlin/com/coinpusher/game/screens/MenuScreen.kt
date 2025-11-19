package com.coinpusher.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.coinpusher.game.CoinPusherGame
import com.coinpusher.game.data.GameData

/**
 * Main menu screen with play, settings, and quit options
 */
class MenuScreen(private val game: CoinPusherGame) : Screen, GestureDetector.GestureListener {

    private val camera: OrthographicCamera
    private val viewport: FitViewport
    private val gameData = GameData.getInstance()

    // UI elements
    private val playButton: Rectangle
    private val settingsButton: Rectangle
    private val quitButton: Rectangle

    private var selectedButton: Rectangle? = null

    init {
        camera = OrthographicCamera()
        camera.setToOrtho(false, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT)
        viewport = FitViewport(CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT, camera)

        // Create buttons (centered)
        val buttonWidth = 400f
        val buttonHeight = 80f
        val buttonX = CoinPusherGame.GAME_WIDTH / 2 - buttonWidth / 2

        playButton = Rectangle(buttonX, 600f, buttonWidth, buttonHeight)
        settingsButton = Rectangle(buttonX, 480f, buttonWidth, buttonHeight)
        quitButton = Rectangle(buttonX, 360f, buttonWidth, buttonHeight)

        Gdx.input.inputProcessor = GestureDetector(this)

        // Show daily reward if available
        if (gameData.isDailyRewardAvailable()) {
            showDailyReward()
        }

        Gdx.app.log("MenuScreen", "Menu initialized")
    }

    override fun show() {}

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        renderBackground()
        renderUI()
    }

    private fun renderBackground() {
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Gradient background effect (simple version)
        game.shapeRenderer.rect(0f, 0f, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT,
            Color(0.05f, 0.1f, 0.15f, 1f),
            Color(0.05f, 0.1f, 0.15f, 1f),
            Color(0.1f, 0.15f, 0.2f, 1f),
            Color(0.1f, 0.15f, 0.2f, 1f))

        game.shapeRenderer.end()
    }

    private fun renderUI() {
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Title background
        game.shapeRenderer.color = Color(0.2f, 0.3f, 0.4f, 0.8f)
        game.shapeRenderer.rect(100f, 850f, 520f, 150f)

        // Buttons
        renderButton(playButton, "PLAY", Color(0.2f, 0.8f, 0.3f, 1f))
        renderButton(settingsButton, "SETTINGS", Color(0.4f, 0.6f, 0.8f, 1f))
        renderButton(quitButton, "QUIT", Color(0.8f, 0.3f, 0.2f, 1f))

        // Stats panel
        game.shapeRenderer.color = Color(0.1f, 0.1f, 0.1f, 0.7f)
        game.shapeRenderer.rect(50f, 50f, 620f, 250f)

        game.shapeRenderer.end()

        // Text rendering
        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        // Title
        game.font.color = Color.GOLD
        game.font.data.setScale(2.5f)
        game.font.draw(game.batch, "COIN PUSHER", 150f, 950f)

        game.font.data.setScale(1.0f)
        game.font.color = Color.WHITE

        // Button labels
        drawCenteredText("PLAY", playButton.x + playButton.width / 2, playButton.y + 50f)
        drawCenteredText("SETTINGS", settingsButton.x + settingsButton.width / 2, settingsButton.y + 50f)
        drawCenteredText("QUIT", quitButton.x + quitButton.width / 2, quitButton.y + 50f)

        // Stats
        game.font.color = Color.WHITE
        game.font.data.setScale(0.9f)
        var statsY = 260f
        game.font.draw(game.batch, "STATISTICS", 80f, statsY)
        statsY -= 40f

        game.font.color = Color.LIGHT_GRAY
        game.font.data.setScale(0.8f)
        game.font.draw(game.batch, "High Score: ${gameData.highScore}", 80f, statsY)
        statsY -= 35f
        game.font.draw(game.batch, "Total Coins: ${gameData.totalCoinsCollected}", 80f, statsY)
        statsY -= 35f
        game.font.draw(game.batch, "Games Played: ${gameData.gamesPlayed}", 80f, statsY)
        statsY -= 35f
        game.font.draw(game.batch, "Daily Streak: ${gameData.dailyRewardStreak} days", 80f, statsY)

        // Currency display
        game.font.color = Color.GOLD
        game.font.data.setScale(1.0f)
        game.font.draw(game.batch, "Coins: ${gameData.coins}", 500f, 260f)

        game.font.data.setScale(1.0f)
        game.batch.end()
    }

    private fun renderButton(button: Rectangle, label: String, color: Color) {
        val isSelected = selectedButton == button

        // Button background
        game.shapeRenderer.color = if (isSelected) {
            Color(color.r * 1.2f, color.g * 1.2f, color.b * 1.2f, 1f)
        } else {
            color
        }
        game.shapeRenderer.rect(button.x, button.y, button.width, button.height)

        // Button border
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl.glLineWidth(3f)
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.rect(button.x, button.y, button.width, button.height)
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun drawCenteredText(text: String, centerX: Float, y: Float) {
        val layout = com.badlogic.gdx.graphics.g2d.GlyphLayout()
        layout.setText(game.font, text)
        game.font.draw(game.batch, text, centerX - layout.width / 2, y)
    }

    private fun showDailyReward() {
        val reward = gameData.claimDailyReward()
        Gdx.app.log("MenuScreen", "Daily reward claimed: $reward coins! Streak: ${gameData.dailyRewardStreak}")
        // TODO: Show popup dialog
    }

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        val worldPos = viewport.unproject(Vector2(x, y))

        when {
            playButton.contains(worldPos.x, worldPos.y) -> {
                selectedButton = playButton
                Gdx.app.log("MenuScreen", "Play button clicked")
                game.setScreen(GameScreen(game))
                dispose()
                return true
            }
            settingsButton.contains(worldPos.x, worldPos.y) -> {
                selectedButton = settingsButton
                Gdx.app.log("MenuScreen", "Settings button clicked")
                game.setScreen(SettingsScreen(game))
                dispose()
                return true
            }
            quitButton.contains(worldPos.x, worldPos.y) -> {
                selectedButton = quitButton
                Gdx.app.log("MenuScreen", "Quit button clicked")
                Gdx.app.exit()
                return true
            }
        }

        return false
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false
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
    override fun dispose() {}
}
