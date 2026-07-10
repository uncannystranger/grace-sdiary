package com.grace.sdiary.ui.screens.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.RoutineEntity
import com.grace.sdiary.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    val allRoutines = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val morningRoutines = repository.getByTimeOfDay("morning")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val afternoonRoutines = repository.getByTimeOfDay("afternoon")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val eveningRoutines = repository.getByTimeOfDay("evening")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val nightRoutines = repository.getByTimeOfDay("night")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRoutine(title: String, timeOfDay: String) = viewModelScope.launch {
        repository.insert(RoutineEntity(
            title = title, timeOfDay = timeOfDay,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ))
    }

    fun toggleEnabled(routine: RoutineEntity) = viewModelScope.launch {
        repository.update(routine.copy(isEnabled = !routine.isEnabled))
    }

    fun deleteRoutine(routine: RoutineEntity) = viewModelScope.launch {
        repository.delete(routine)
    }
}
