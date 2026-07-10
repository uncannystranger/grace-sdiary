package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val type: String,
    val targetValue: Double = 0.0,
    val currentValue: Double = 0.0,
    val unit: String? = null,
    val startDate: Long,
    val endDate: Long? = null,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
