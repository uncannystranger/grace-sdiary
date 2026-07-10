package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.QuickNoteDao
import com.grace.sdiary.data.local.db.entity.QuickNoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class QuickNoteRepository(private val dao: QuickNoteDao) {

    fun getAll(): Flow<List<QuickNoteEntity>> = dao.getAll()

    fun getPinned(): Flow<List<QuickNoteEntity>> = dao.getPinned()

    fun search(query: String): Flow<List<QuickNoteEntity>> = dao.search(query)

    suspend fun getById(id: Long): QuickNoteEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: QuickNoteEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: QuickNoteEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: QuickNoteEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}
