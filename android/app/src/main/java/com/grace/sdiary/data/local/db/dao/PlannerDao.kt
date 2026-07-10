package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    @Query("SELECT * FROM planner")
    fun getAll(): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE date = :date ORDER BY sortOrder, startTime")
    fun getByDate(date: Long): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE date BETWEEN :startDate AND :endDate")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE date = :today")
    fun getToday(today: Long): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE date < :now AND isComplete = 0")
    fun getOverdue(now: Long): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PlannerEntity>>

    @Query("SELECT * FROM planner WHERE id = :id")
    suspend fun getById(id: Long): PlannerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlannerEntity): Long

    @Update
    suspend fun update(entity: PlannerEntity)

    @Delete
    suspend fun delete(entity: PlannerEntity)

    @Query("SELECT * FROM planner")
    suspend fun getAllSync(): List<PlannerEntity>

    @Query("SELECT * FROM planner WHERE date = :today")
    suspend fun getTodaySync(today: Long): List<PlannerEntity>
}
