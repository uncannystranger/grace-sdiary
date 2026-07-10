package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val colorHex: String? = null,
    val frequency: String = "daily",
    val targetCount: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long
)
