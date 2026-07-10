package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.ReminderDao
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReminderRepository(private val dao: ReminderDao) {

    fun getAll(): Flow<List<ReminderEntity>> = dao.getAll()

    fun getEnabled(): Flow<List<ReminderEntity>> = dao.getEnabled()

    fun getUpcoming(now: Long): Flow<List<ReminderEntity>> = dao.getUpcoming(now)

    fun getOverdue(now: Long): Flow<List<ReminderEntity>> = dao.getOverdue(now)

    fun getByType(type: String): Flow<List<ReminderEntity>> = dao.getByType(type)

    suspend fun getById(id: Long): ReminderEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: ReminderEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: ReminderEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: ReminderEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}
