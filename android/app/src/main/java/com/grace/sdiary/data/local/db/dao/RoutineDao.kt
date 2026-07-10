package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines ORDER BY sortOrder")
    fun getAll(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE timeOfDay = :timeOfDay ORDER BY sortOrder")
    fun getByTimeOfDay(timeOfDay: String): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE isEnabled = 1 ORDER BY sortOrder")
    fun getEnabled(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getById(id: Long): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RoutineEntity): Long

    @Update
    suspend fun update(entity: RoutineEntity)

    @Delete
    suspend fun delete(entity: RoutineEntity)

    @Query("SELECT * FROM routines ORDER BY sortOrder")
    suspend fun getAllSync(): List<RoutineEntity>
}
