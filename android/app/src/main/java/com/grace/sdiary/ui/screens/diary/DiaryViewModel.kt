package com.grace.sdiary.ui.screens.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.DiaryEntity
import com.grace.sdiary.data.repository.DiaryRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    val entries = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayEntry = repository.getByDate(DateUtils.todayStart())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val count = repository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun saveEntry(content: String, mood: Int?, title: String?) = viewModelScope.launch {
        val existing = todayEntry.value
        if (existing != null) {
            repository.update(existing.copy(
                content = content,
                mood = mood,
                title = title,
                updatedAt = DateUtils.now()
            ))
        } else {
            if (content.isNotBlank()) {
                repository.insert(DiaryEntity(
                    date = DateUtils.todayStart(),
                    content = content,
                    mood = mood,
                    title = title,
                    createdAt = DateUtils.now(),
                    updatedAt = DateUtils.now()
                ))
            }
        }
    }

    fun deleteEntry(id: Long) = viewModelScope.launch {
        repository.getById(id)?.let { repository.delete(it) }
    }
}
