package com.coinpusher.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manages game data persistence using libGDX Preferences
 * Saves high scores, settings, progress, and daily rewards
 */
class GameData {

    private val prefs: Preferences = Gdx.app.getPreferences("CoinPusherData")

    // Game statistics
    var highScore: Int
        get() = prefs.getInteger("highScore", 0)
        set(value) {
            if (value > highScore) {
                prefs.putInteger("highScore", value)
                prefs.flush()
                Gdx.app.log("GameData", "New high score: $value")
            }
        }

    var totalCoinsCollected: Long
        get() = prefs.getLong("totalCoinsCollected", 0L)
        set(value) {
            prefs.putLong("totalCoinsCollected", value)
            prefs.flush()
        }

    var gamesPlayed: Int
        get() = prefs.getInteger("gamesPlayed", 0)
        set(value) {
            prefs.putInteger("gamesPlayed", value)
            prefs.flush()
        }

    var totalPlayTime: Long
        get() = prefs.getLong("totalPlayTime", 0L)
        set(value) {
            prefs.putLong("totalPlayTime", value)
            prefs.flush()
        }

    // Currency
    var coins: Int
        get() = prefs.getInteger("coins", 100)
        set(value) {
            prefs.putInteger("coins", value.coerceAtLeast(0))
            prefs.flush()
        }

    var gems: Int
        get() = prefs.getInteger("gems", 0)
        set(value) {
            prefs.putInteger("gems", value.coerceAtLeast(0))
            prefs.flush()
        }

    // Settings
    var masterVolume: Float
        get() = prefs.getFloat("masterVolume", 1.0f)
        set(value) {
            prefs.putFloat("masterVolume", value.coerceIn(0f, 1f))
            prefs.flush()
        }

    var sfxVolume: Float
        get() = prefs.getFloat("sfxVolume", 0.7f)
        set(value) {
            prefs.putFloat("sfxVolume", value.coerceIn(0f, 1f))
            prefs.flush()
        }

    var musicVolume: Float
        get() = prefs.getFloat("musicVolume", 0.5f)
        set(value) {
            prefs.putFloat("musicVolume", value.coerceIn(0f, 1f))
            prefs.flush()
        }

    var soundEnabled: Boolean
        get() = prefs.getBoolean("soundEnabled", true)
        set(value) {
            prefs.putBoolean("soundEnabled", value)
            prefs.flush()
        }

    var musicEnabled: Boolean
        get() = prefs.getBoolean("musicEnabled", true)
        set(value) {
            prefs.putBoolean("musicEnabled", value)
            prefs.flush()
        }

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibrationEnabled", true)
        set(value) {
            prefs.putBoolean("vibrationEnabled", value)
            prefs.flush()
        }

    // Daily rewards
    var lastDailyRewardDate: String
        get() = prefs.getString("lastDailyReward", "")
        set(value) {
            prefs.putString("lastDailyReward", value)
            prefs.flush()
        }

    var dailyRewardStreak: Int
        get() = prefs.getInteger("dailyRewardStreak", 0)
        set(value) {
            prefs.putInteger("dailyRewardStreak", value)
            prefs.flush()
        }

    // Unlockables
    var unlockedMachines: Set<String>
        get() {
            val machines = prefs.getString("unlockedMachines", "classic")
            return machines.split(",").toSet()
        }
        set(value) {
            prefs.putString("unlockedMachines", value.joinToString(","))
            prefs.flush()
        }

    var currentMachine: String
        get() = prefs.getString("currentMachine", "classic")
        set(value) {
            prefs.putString("currentMachine", value)
            prefs.flush()
        }

    // First time user experience
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean("isFirstLaunch", true)
        set(value) {
            prefs.putBoolean("isFirstLaunch", value)
            prefs.flush()
        }

    var tutorialCompleted: Boolean
        get() = prefs.getBoolean("tutorialCompleted", false)
        set(value) {
            prefs.putBoolean("tutorialCompleted", value)
            prefs.flush()
        }

    /**
     * Check if daily reward is available
     */
    fun isDailyRewardAvailable(): Boolean {
        val today = getCurrentDate()
        return lastDailyRewardDate != today
    }

    /**
     * Claim daily reward
     */
    fun claimDailyReward(): Int {
        val today = getCurrentDate()

        // Check if claiming on consecutive days
        val yesterday = getYesterdayDate()
        if (lastDailyRewardDate == yesterday) {
            dailyRewardStreak++
        } else if (lastDailyRewardDate != today) {
            // Streak broken
            dailyRewardStreak = 1
        }

        lastDailyRewardDate = today

        // Reward scales with streak
        val reward = when {
            dailyRewardStreak >= 7 -> 500  // Week streak
            dailyRewardStreak >= 3 -> 200  // 3 day streak
            else -> 100  // Base reward
        }

        coins += reward
        return reward
    }

    /**
     * Add coins to player's balance
     */
    fun addCoins(amount: Int) {
        coins += amount
        totalCoinsCollected += amount.toLong()
    }

    /**
     * Spend coins
     */
    fun spendCoins(amount: Int): Boolean {
        return if (coins >= amount) {
            coins -= amount
            true
        } else {
            false
        }
    }

    /**
     * Update high score if new score is higher
     */
    fun updateHighScore(score: Int) {
        if (score > highScore) {
            highScore = score
        }
    }

    /**
     * Unlock a new machine
     */
    fun unlockMachine(machineId: String) {
        val current = unlockedMachines.toMutableSet()
        current.add(machineId)
        unlockedMachines = current
        Gdx.app.log("GameData", "Unlocked machine: $machineId")
    }

    /**
     * Reset all game data (for testing or player request)
     */
    fun resetAllData() {
        prefs.clear()
        prefs.flush()
        Gdx.app.log("GameData", "All data reset")
    }

    /**
     * Get stats summary
     */
    fun getStatsSummary(): String {
        return """
            High Score: $highScore
            Total Coins: $totalCoinsCollected
            Games Played: $gamesPlayed
            Play Time: ${totalPlayTime / 60}m
            Streak: $dailyRewardStreak days
        """.trimIndent()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.format(Date())
    }

    private fun getYesterdayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(calendar.time)
    }

    companion object {
        private var instance: GameData? = null

        fun getInstance(): GameData {
            if (instance == null) {
                instance = GameData()
            }
            return instance!!
        }
    }
}
