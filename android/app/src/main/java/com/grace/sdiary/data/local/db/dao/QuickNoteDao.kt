package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.QuickNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickNoteDao {

    @Query("SELECT * FROM quick_notes ORDER BY isPinned DESC, createdAt DESC")
    fun getAll(): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE isPinned = 1")
    fun getPinned(): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE content LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE id = :id")
    suspend fun getById(id: Long): QuickNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QuickNoteEntity): Long

    @Update
    suspend fun update(entity: QuickNoteEntity)

    @Delete
    suspend fun delete(entity: QuickNoteEntity)

    @Query("SELECT * FROM quick_notes ORDER BY isPinned DESC, createdAt DESC")
    suspend fun getAllSync(): List<QuickNoteEntity>
}
