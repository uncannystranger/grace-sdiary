package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner")
data class PlannerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val title: String,
    val description: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val category: String? = null,
    val priority: Int = 0,
    val isComplete: Boolean = false,
    val isRecurring: Boolean = false,
    val recurringType: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
