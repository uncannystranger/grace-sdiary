package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val title: String? = null,
    val content: String,
    val mood: Int? = null,
    val tags: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
