package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.datastore.UserPreferences
import com.grace.sdiary.data.local.db.dao.DiaryDao
import com.grace.sdiary.data.local.db.dao.HabitDao
import com.grace.sdiary.data.local.db.dao.HabitLogDao
import com.grace.sdiary.data.local.db.dao.PlannerDao
import com.grace.sdiary.data.local.db.dao.VocabularyDao
import com.grace.sdiary.data.model.DailyStats
import com.grace.sdiary.data.model.LevelSystem
import com.grace.sdiary.data.model.WeeklyProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProgressRepository(
    private val plannerDao: PlannerDao,
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val diaryDao: DiaryDao,
    private val vocabDao: VocabularyDao,
    private val prefs: UserPreferences
) {

    companion object {
        private const val DAILY_XP_LIMIT = 100L
    }

    fun getDailyStats(): Flow<DailyStats> = combine(
        prefs.dailyXp,
        prefs.streakCount,
        prefs.weeklyGoalMinutes
    ) { dailyXp, streak, weeklyGoalMinutes ->
        val today = normalizeDate(System.currentTimeMillis())
        val tomorrow = today + 86400000L

        val tasksCompleted = withContext(Dispatchers.IO) {
            plannerDao.getByDate(today).first().count { it.isComplete }
        }
        val habitsDone = withContext(Dispatchers.IO) {
            habitLogDao.getByDateRange(-1L, today, tomorrow).first().size
        }
        val diaryWritten = withContext(Dispatchers.IO) {
            diaryDao.getByDate(today)?.let { 1 } ?: 0
        }
        val vocabReviewed = withContext(Dispatchers.IO) {
            vocabDao.getCount().first()
        }

        DailyStats(
            xp = dailyXp.toInt(),
            streak = streak,
            weeklyGoalPct = if (weeklyGoalMinutes > 0) 0f else 0f,
            tasksCompleted = tasksCompleted,
            habitsDone = habitsDone,
            vocabReviewed = vocabReviewed.toInt(),
            diaryWritten = diaryWritten
        )
    }

    fun getWeeklyProgress(): Flow<WeeklyProgress> = combine(
        prefs.dailyXp,
        prefs.streakCount,
        prefs.weeklyGoalMinutes
    ) { dailyXp, streak, weeklyGoalMinutes ->
        val totalXp = dailyXp.toInt()
        val levelInfo = LevelSystem.getLevelInfo(totalXp)

        WeeklyProgress(
            xpTd = totalXp,
            streak = streak,
            weeklyGoalMinutes = weeklyGoalMinutes,
            weeklyProgressMinutes = 0,
            level = levelInfo.level,
            levelTitle = levelInfo.title,
            levelProgress = levelInfo.progress
        )
    }

    suspend fun updateXp(xpGained: Int) = withContext(Dispatchers.IO) {
        val currentXp = prefs.dailyXp.first()
        val newXp = (currentXp + xpGained).coerceAtMost(DAILY_XP_LIMIT)
        prefs.setDailyXp(newXp)
    }

    suspend fun incrementStreak() = withContext(Dispatchers.IO) {
        val currentStreak = prefs.streakCount.first()
        prefs.setStreakCount(currentStreak + 1)
        prefs.setLastActiveDate(System.currentTimeMillis())
    }

    suspend fun resetStreak() = withContext(Dispatchers.IO) {
        prefs.setStreakCount(0)
        prefs.setLastActiveDate(0L)
    }

    private fun normalizeDate(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timeMillis }
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
