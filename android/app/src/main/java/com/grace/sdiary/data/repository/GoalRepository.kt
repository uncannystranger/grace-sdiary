package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.GoalDao
import com.grace.sdiary.data.local.db.entity.GoalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GoalRepository(private val dao: GoalDao) {

    fun getAll(): Flow<List<GoalEntity>> = dao.getAll()

    fun getActive(): Flow<List<GoalEntity>> = dao.getActive()

    fun getByType(type: String): Flow<List<GoalEntity>> = dao.getByType(type)

    suspend fun getById(id: Long): GoalEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: GoalEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: GoalEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: GoalEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}
