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
 * Settings screen for audio, graphics, and game options
 */
class SettingsScreen(private val game: CoinPusherGame) : Screen, GestureDetector.GestureListener {

    private val camera: OrthographicCamera
    private val viewport: FitViewport
    private val gameData = GameData.getInstance()

    // UI elements
    private val backButton: Rectangle
    private val soundToggle: Rectangle
    private val musicToggle: Rectangle
    private val vibrationToggle: Rectangle
    private val resetDataButton: Rectangle

    // Volume sliders
    private val masterVolumeSlider: Rectangle
    private val sfxVolumeSlider: Rectangle
    private val musicVolumeSlider: Rectangle

    private var isDraggingMaster = false
    private var isDraggingSfx = false
    private var isDraggingMusic = false

    init {
        camera = OrthographicCamera()
        camera.setToOrtho(false, CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT)
        viewport = FitViewport(CoinPusherGame.GAME_WIDTH, CoinPusherGame.GAME_HEIGHT, camera)

        // Create UI elements
        backButton = Rectangle(50f, 50f, 200f, 70f)

        val toggleWidth = 200f
        val toggleHeight = 60f
        val toggleX = 450f

        soundToggle = Rectangle(toggleX, 900f, toggleWidth, toggleHeight)
        musicToggle = Rectangle(toggleX, 800f, toggleWidth, toggleHeight)
        vibrationToggle = Rectangle(toggleX, 700f, toggleWidth, toggleHeight)

        // Sliders
        val sliderWidth = 400f
        val sliderHeight = 30f
        val sliderX = 250f

        masterVolumeSlider = Rectangle(sliderX, 580f, sliderWidth, sliderHeight)
        sfxVolumeSlider = Rectangle(sliderX, 480f, sliderWidth, sliderHeight)
        musicVolumeSlider = Rectangle(sliderX, 380f, sliderWidth, sliderHeight)

        resetDataButton = Rectangle(200f, 200f, 320f, 70f)

        Gdx.input.inputProcessor = GestureDetector(this)
        Gdx.app.log("SettingsScreen", "Settings initialized")
    }

    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        renderUI()
    }

    private fun renderUI() {
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Title background
        game.shapeRenderer.color = Color(0.2f, 0.3f, 0.4f, 0.8f)
        game.shapeRenderer.rect(50f, 1000f, 620f, 120f)

        // Toggles
        renderToggle(soundToggle, gameData.soundEnabled, "Sound")
        renderToggle(musicToggle, gameData.musicEnabled, "Music")
        renderToggle(vibrationToggle, gameData.vibrationEnabled, "Vibration")

        // Sliders
        renderSlider(masterVolumeSlider, gameData.masterVolume, "Master Volume")
        renderSlider(sfxVolumeSlider, gameData.sfxVolume, "SFX Volume")
        renderSlider(musicVolumeSlider, gameData.musicVolume, "Music Volume")

        // Buttons
        renderButton(backButton, Color(0.4f, 0.6f, 0.8f, 1f))
        renderButton(resetDataButton, Color(0.8f, 0.3f, 0.2f, 1f))

        game.shapeRenderer.end()

        // Text
        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        game.font.color = Color.GOLD
        game.font.data.setScale(2.0f)
        game.font.draw(game.batch, "SETTINGS", 80f, 1090f)

        game.font.data.setScale(1.0f)
        game.font.color = Color.WHITE

        // Toggle labels
        game.font.draw(game.batch, "Sound Effects:", 80f, 940f)
        game.font.draw(game.batch, "Background Music:", 80f, 840f)
        game.font.draw(game.batch, "Vibration:", 80f, 740f)

        // Slider labels
        game.font.draw(game.batch, "Master: ${(gameData.masterVolume * 100).toInt()}%", 80f, 605f)
        game.font.draw(game.batch, "SFX: ${(gameData.sfxVolume * 100).toInt()}%", 80f, 505f)
        game.font.draw(game.batch, "Music: ${(gameData.musicVolume * 100).toInt()}%", 80f, 405f)

        // Button labels
        drawCenteredText("BACK", backButton.x + backButton.width / 2, backButton.y + 45f)
        drawCenteredText("RESET DATA", resetDataButton.x + resetDataButton.width / 2, resetDataButton.y + 45f)

        game.font.data.setScale(1.0f)
        game.batch.end()
    }

    private fun renderToggle(toggle: Rectangle, enabled: Boolean, label: String) {
        // Background
        game.shapeRenderer.color = if (enabled) {
            Color(0.2f, 0.8f, 0.3f, 1f)
        } else {
            Color(0.6f, 0.3f, 0.2f, 1f)
        }
        game.shapeRenderer.rect(toggle.x, toggle.y, toggle.width, toggle.height)

        // Border
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl.glLineWidth(2f)
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.rect(toggle.x, toggle.y, toggle.width, toggle.height)
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun renderSlider(slider: Rectangle, value: Float, label: String) {
        // Background track
        game.shapeRenderer.color = Color(0.3f, 0.3f, 0.3f, 1f)
        game.shapeRenderer.rect(slider.x, slider.y, slider.width, slider.height)

        // Fill
        game.shapeRenderer.color = Color(0.2f, 0.7f, 0.9f, 1f)
        game.shapeRenderer.rect(slider.x, slider.y, slider.width * value, slider.height)

        // Handle
        val handleX = slider.x + slider.width * value
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.circle(handleX, slider.y + slider.height / 2, 15f, 20)

        // Border
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl.glLineWidth(2f)
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.rect(slider.x, slider.y, slider.width, slider.height)
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun renderButton(button: Rectangle, color: Color) {
        game.shapeRenderer.color = color
        game.shapeRenderer.rect(button.x, button.y, button.width, button.height)

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

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        val worldPos = viewport.unproject(Vector2(x, y))

        when {
            backButton.contains(worldPos.x, worldPos.y) -> {
                Gdx.app.log("SettingsScreen", "Back button clicked")
                game.setScreen(MenuScreen(game))
                dispose()
                return true
            }
            soundToggle.contains(worldPos.x, worldPos.y) -> {
                gameData.soundEnabled = !gameData.soundEnabled
                Gdx.app.log("SettingsScreen", "Sound: ${gameData.soundEnabled}")
                return true
            }
            musicToggle.contains(worldPos.x, worldPos.y) -> {
                gameData.musicEnabled = !gameData.musicEnabled
                Gdx.app.log("SettingsScreen", "Music: ${gameData.musicEnabled}")
                return true
            }
            vibrationToggle.contains(worldPos.x, worldPos.y) -> {
                gameData.vibrationEnabled = !gameData.vibrationEnabled
                Gdx.app.log("SettingsScreen", "Vibration: ${gameData.vibrationEnabled}")
                return true
            }
            resetDataButton.contains(worldPos.x, worldPos.y) -> {
                gameData.resetAllData()
                Gdx.app.log("SettingsScreen", "Data reset")
                return true
            }
            masterVolumeSlider.contains(worldPos.x, worldPos.y) -> {
                isDraggingMaster = true
                updateSliderValue(masterVolumeSlider, worldPos.x)
                return true
            }
            sfxVolumeSlider.contains(worldPos.x, worldPos.y) -> {
                isDraggingSfx = true
                updateSliderValue(sfxVolumeSlider, worldPos.x)
                return true
            }
            musicVolumeSlider.contains(worldPos.x, worldPos.y) -> {
                isDraggingMusic = true
                updateSliderValue(musicVolumeSlider, worldPos.x)
                return true
            }
        }

        return false
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        val worldPos = viewport.unproject(Vector2(x, y))

        when {
            isDraggingMaster -> updateSliderValue(masterVolumeSlider, worldPos.x)
            isDraggingSfx -> updateSliderValue(sfxVolumeSlider, worldPos.x)
            isDraggingMusic -> updateSliderValue(musicVolumeSlider, worldPos.x)
        }

        return true
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        isDraggingMaster = false
        isDraggingSfx = false
        isDraggingMusic = false
        return true
    }

    private fun updateSliderValue(slider: Rectangle, touchX: Float) {
        val value = ((touchX - slider.x) / slider.width).coerceIn(0f, 1f)

        when (slider) {
            masterVolumeSlider -> gameData.masterVolume = value
            sfxVolumeSlider -> gameData.sfxVolume = value
            musicVolumeSlider -> gameData.musicVolume = value
        }
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false
    override fun longPress(x: Float, y: Float): Boolean = false
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false
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
