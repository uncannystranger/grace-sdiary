package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.RoutineDao
import com.grace.sdiary.data.local.db.entity.RoutineEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoutineRepository(private val dao: RoutineDao) {

    fun getAll(): Flow<List<RoutineEntity>> = dao.getAll()

    fun getByTimeOfDay(timeOfDay: String): Flow<List<RoutineEntity>> = dao.getByTimeOfDay(timeOfDay)

    fun getEnabled(): Flow<List<RoutineEntity>> = dao.getEnabled()

    suspend fun getById(id: Long): RoutineEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: RoutineEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: RoutineEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: RoutineEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}
