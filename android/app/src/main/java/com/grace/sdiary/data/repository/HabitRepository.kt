package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.HabitDao
import com.grace.sdiary.data.local.db.dao.HabitLogDao
import com.grace.sdiary.data.local.db.entity.HabitEntity
import com.grace.sdiary.data.local.db.entity.HabitLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {

    fun getAll(): Flow<List<HabitEntity>> = habitDao.getAll()

    fun getActive(): Flow<List<HabitEntity>> = habitDao.getActive()

    fun getStreaks(): Flow<List<HabitEntity>> = habitDao.getStreaks()

    suspend fun getById(id: Long): HabitEntity? = withContext(Dispatchers.IO) {
        habitDao.getById(id)
    }

    suspend fun insert(entity: HabitEntity): Long = withContext(Dispatchers.IO) {
        habitDao.insert(entity)
    }

    suspend fun update(entity: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.update(entity)
    }

    suspend fun delete(entity: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.delete(entity)
    }

    suspend fun logHabit(habitId: Long, date: Long, count: Int = 1) = withContext(Dispatchers.IO) {
        val log = HabitLogEntity(
            habitId = habitId,
            date = date,
            count = count,
            createdAt = System.currentTimeMillis()
        )
        habitLogDao.insert(log)
        val habit = habitDao.getById(habitId)
        if (habit != null) {
            habitDao.update(habit.copy(currentStreak = habit.currentStreak + 1))
        }
    }

    fun getLogs(habitId: Long): Flow<List<HabitLogEntity>> = habitLogDao.getByHabit(habitId)

    fun getCompletedDates(habitId: Long): Flow<List<Long>> = habitLogDao.getCompletedDates(habitId)

    suspend fun isCompleted(habitId: Long, date: Long): Boolean = withContext(Dispatchers.IO) {
        habitLogDao.getByHabitAndDate(habitId, date) != null
    }

    fun getLogsByDateRange(habitId: Long, start: Long, end: Long): Flow<List<HabitLogEntity>> =
        habitLogDao.getByDateRange(habitId, start, end)
}
