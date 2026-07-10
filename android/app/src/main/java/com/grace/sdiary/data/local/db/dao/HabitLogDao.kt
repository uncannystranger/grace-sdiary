package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getByHabitAndDate(habitId: Long, date: Long): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY date DESC")
    fun getByHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate")
    fun getByDateRange(habitId: Long, startDate: Long, endDate: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT date FROM habit_logs WHERE habitId = :habitId GROUP BY date")
    fun getCompletedDates(habitId: Long): Flow<List<Long>>

    @Query("SELECT * FROM habit_logs WHERE id = :id")
    suspend fun getById(id: Long): HabitLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HabitLogEntity): Long

    @Update
    suspend fun update(entity: HabitLogEntity)

    @Delete
    suspend fun delete(entity: HabitLogEntity)

    @Query("SELECT * FROM habit_logs")
    suspend fun getAllSync(): List<HabitLogEntity>
}
