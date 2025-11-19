package com.coinpusher.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.*

/**
 * Creates the walls and collection area for the coin pusher
 */
class GameBounds(private val world: World) {

    private val walls = mutableListOf<Body>()
    private var collectorBody: Body? = null

    companion object {
        const val WALL_THICKNESS = 0.2f
        const val GAME_WIDTH = 7.2f // 720 pixels / 100 PPM
        const val GAME_HEIGHT = 12.8f // 1280 pixels / 100 PPM
    }

    init {
        createWalls()
        createCollector()
    }

    private fun createWalls() {
        // Left wall
        createWall(WALL_THICKNESS / 2, GAME_HEIGHT / 2, WALL_THICKNESS, GAME_HEIGHT)

        // Right wall
        createWall(GAME_WIDTH - WALL_THICKNESS / 2, GAME_HEIGHT / 2, WALL_THICKNESS, GAME_HEIGHT)

        // Top wall (where coins spawn)
        createWall(GAME_WIDTH / 2, GAME_HEIGHT - WALL_THICKNESS / 2, GAME_WIDTH, WALL_THICKNESS)

        // Back wall (behind platform)
        createWall(GAME_WIDTH / 2, 1.0f, GAME_WIDTH, WALL_THICKNESS)
    }

    private fun createWall(x: Float, y: Float, width: Float, height: Float) {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
            position.set(x, y)
        }

        val body = world.createBody(bodyDef).apply {
            val box = PolygonShape().apply {
                setAsBox(width / 2, height / 2)
            }

            val fixtureDef = FixtureDef().apply {
                shape = box
                density = 0f
                friction = 0.3f
            }

            createFixture(fixtureDef)
            box.dispose()

            userData = "wall"
        }

        walls.add(body)
    }

    private fun createCollector() {
        // Collection area at the bottom
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
            position.set(GAME_WIDTH / 2, 0.3f)
        }

        collectorBody = world.createBody(bodyDef).apply {
            val box = PolygonShape().apply {
                setAsBox(GAME_WIDTH / 2, 0.2f)
            }

            val fixtureDef = FixtureDef().apply {
                shape = box
                isSensor = true // Don't physically block, just detect
            }

            createFixture(fixtureDef)
            box.dispose()

            userData = "collector"
        }
    }

    fun render(renderer: ShapeRenderer, ppm: Float) {
        renderer.color = Color.BROWN

        // Draw walls
        for (wall in walls) {
            val pos = wall.position
            val fixture = wall.fixtureList.first()
            val shape = fixture.shape as PolygonShape

            // Simple rectangle rendering for walls
            // This is a simplified version - in production you'd get actual vertices
            renderer.rect(
                (pos.x - 0.1f) * ppm,
                (pos.y - GAME_HEIGHT / 2) * ppm,
                WALL_THICKNESS * ppm,
                GAME_HEIGHT * ppm
            )
        }

        // Draw collector area
        renderer.color = Color(0.2f, 0.7f, 0.2f, 0.5f)
        collectorBody?.let { body ->
            val pos = body.position
            renderer.rect(
                0f,
                0f,
                GAME_WIDTH * ppm,
                0.5f * ppm
            )
        }
    }

    fun dispose() {
        for (wall in walls) {
            world.destroyBody(wall)
        }
        collectorBody?.let { world.destroyBody(it) }
        walls.clear()
    }
}
