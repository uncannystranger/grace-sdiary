package com.grace.sdiary.data.model

enum class Mood(val value: Int, val emoji: String) {
    Great(5, "\uD83D\uDE04"),
    Good(4, "\uD83D\uDE42"),
    Okay(3, "\uD83D\uDE10"),
    Low(2, "\uD83D\uDE14"),
    Terrible(1, "\uD83D\uDE22");

    companion object {
        fun fromValue(value: Int): Mood = entries.firstOrNull { it.value == value } ?: Okay
    }
}

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class WordStatus {
    LEARNING,
    KNOWN,
    REVIEWING
}

enum class GoalType {
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class HabitFrequency {
    DAILY,
    WEEKLY,
    WEEKDAYS
}

enum class Priority(val value: Int) {
    NONE(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3)
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

enum class ReminderType {
    STUDY,
    WATER,
    MEDICATION,
    EXERCISE,
    DIARY,
    GENERAL
}

enum class RepeatInterval {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class PlannerCategory {
    STUDY,
    WORK,
    PERSONAL,
    HEALTH
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class WeeklyProgress(
    val xpTd: Int = 0,
    val streak: Int = 0,
    val weeklyGoalMinutes: Int = 300,
    val weeklyProgressMinutes: Int = 0,
    val level: Int = 0,
    val levelTitle: String = "Beginner",
    val levelProgress: Float = 0f
)

data class DailyStats(
    val xp: Int = 0,
    val streak: Int = 0,
    val weeklyGoalPct: Float = 0f,
    val tasksCompleted: Int = 0,
    val habitsDone: Int = 0,
    val vocabReviewed: Int = 0,
    val diaryWritten: Int = 0
)

data class LevelInfo(
    val level: Int,
    val title: String,
    val xpForNext: Int,
    val progress: Float
)

object LevelSystem {
    private val LEVELS = listOf(
        0 to "Beginner",
        100 to "Bronze",
        300 to "Silver",
        600 to "Gold",
        1000 to "Platinum",
        1500 to "Diamond",
        2500 to "Master",
        5000 to "Grandmaster",
        10000 to "Legend",
        20000 to "Mythic"
    )

    fun getLevelInfo(xp: Int): LevelInfo {
        val currentLevel = (LEVELS.lastOrNull { it.first <= xp } ?: (0 to "Beginner"))
        val levelIndex = LEVELS.indexOfFirst { it.first == currentLevel.first }
        val nextLevelIndex = levelIndex + 1
        val level = levelIndex

        val title = currentLevel.second

        if (nextLevelIndex >= LEVELS.size) {
            return LevelInfo(level, title, 0, 1f)
        }

        val nextLevel = LEVELS[nextLevelIndex]
        val xpForNext = nextLevel.first
        val currentLevelXp = currentLevel.first
        val xpInLevel = xp - currentLevelXp
        val xpRequired = xpForNext - currentLevelXp
        val progress = if (xpRequired > 0) xpInLevel.toFloat() / xpRequired else 1f

        return LevelInfo(level, title, xpForNext, progress.coerceIn(0f, 1f))
    }

    fun getXpForLevel(level: Int): Int {
        if (level < 0) return 0
        if (level >= LEVELS.size) return LEVELS.last().first
        return LEVELS[level].first
    }

    fun getLevelTitle(level: Int): String {
        if (level < 0) return LEVELS.first().second
        if (level >= LEVELS.size) return LEVELS.last().second
        return LEVELS[level].second
    }
}
