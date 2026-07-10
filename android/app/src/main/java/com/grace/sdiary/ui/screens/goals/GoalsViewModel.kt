package com.grace.sdiary.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.GoalEntity
import com.grace.sdiary.data.repository.GoalRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    val allGoals = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeGoals = repository.getActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val weeklyGoals = repository.getByType("weekly")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val monthlyGoals = repository.getByType("monthly")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val yearlyGoals = repository.getByType("yearly")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(title: String, type: String, targetValue: Double, unit: String? = null) = viewModelScope.launch {
        repository.insert(GoalEntity(
            title = title, type = type, targetValue = targetValue, unit = unit,
            startDate = DateUtils.todayStart(), createdAt = DateUtils.now(), updatedAt = DateUtils.now()
        ))
    }

    fun updateProgress(id: Long, newValue: Double) = viewModelScope.launch {
        val goal = repository.getById(id) ?: return@launch
        repository.update(goal.copy(currentValue = newValue, isCompleted = newValue >= goal.targetValue, updatedAt = DateUtils.now()))
    }

    fun archiveGoal(id: Long) = viewModelScope.launch {
        val goal = repository.getById(id) ?: return@launch
        repository.update(goal.copy(isArchived = true))
    }

    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch {
        repository.delete(goal)
    }
}
