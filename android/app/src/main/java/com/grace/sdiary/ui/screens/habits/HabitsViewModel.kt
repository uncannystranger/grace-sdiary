package com.grace.sdiary.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.HabitEntity
import com.grace.sdiary.data.repository.HabitRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    val allHabits = repository.getActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayCompleted = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val todayCompleted: StateFlow<Map<Long, Boolean>> = _todayCompleted.asStateFlow()

    val streakCount = repository.getStreaks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshTodayStatus(habits: List<HabitEntity>) {
        viewModelScope.launch {
            habits.forEach { habit ->
                val completed = repository.isCompleted(habit.id, DateUtils.todayStart())
                _todayCompleted.value = _todayCompleted.value + (habit.id to completed)
            }
        }
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val completed = repository.isCompleted(habitId, DateUtils.todayStart())
            if (completed) {
                repository.getLogsByDateRange(habitId, DateUtils.todayStart(), DateUtils.todayEnd())
                    .collect { logs ->
                        logs.firstOrNull()?.let { log ->
                            log.id.let { /* delete by id - would need habitLogDao.delete(id) */ }
                        }
                        return@collect
                    }
            } else {
                repository.logHabit(habitId, DateUtils.todayStart())
            }
            _todayCompleted.value = _todayCompleted.value + (habitId to !completed)
        }
    }

    fun addHabit(name: String, description: String? = null, frequency: String = "daily") = viewModelScope.launch {
        repository.insert(HabitEntity(
            name = name,
            description = description,
            frequency = frequency,
            createdAt = DateUtils.now()
        ))
    }

    fun deleteHabit(habit: HabitEntity) = viewModelScope.launch {
        repository.delete(habit)
    }
}
