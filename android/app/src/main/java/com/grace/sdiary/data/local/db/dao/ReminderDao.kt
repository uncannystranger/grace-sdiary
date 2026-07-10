package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY dateTime")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY dateTime")
    fun getEnabled(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dateTime > :now AND isEnabled = 1 ORDER BY dateTime")
    fun getUpcoming(now: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dateTime <= :now AND isEnabled = 1 AND (snoozedUntil IS NULL OR snoozedUntil < :now)")
    fun getOverdue(now: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE type = :type")
    fun getByType(type: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReminderEntity): Long

    @Update
    suspend fun update(entity: ReminderEntity)

    @Delete
    suspend fun delete(entity: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY dateTime")
    suspend fun getAllSync(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE dateTime <= :now AND isEnabled = 1 AND (snoozedUntil IS NULL OR snoozedUntil < :now)")
    suspend fun getOverdueSync(now: Long): List<ReminderEntity>
}
