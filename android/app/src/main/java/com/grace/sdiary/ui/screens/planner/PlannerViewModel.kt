package com.grace.sdiary.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import com.grace.sdiary.data.repository.PlannerRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val repository: PlannerRepository
) : ViewModel() {

    val todayItems = repository.getToday(DateUtils.todayStart())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overdueItems = repository.getOverdue(DateUtils.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearch(query: String) { _searchQuery.value = query }

    fun toggleComplete(id: Long) = viewModelScope.launch {
        val item = repository.getById(id) ?: return@launch
        repository.update(item.copy(isComplete = !item.isComplete))
    }

    fun addItem(title: String, description: String? = null, startTime: Long? = null) = viewModelScope.launch {
        repository.insert(PlannerEntity(
            date = DateUtils.todayStart(),
            title = title,
            description = description,
            startTime = startTime,
            createdAt = DateUtils.now(),
            updatedAt = DateUtils.now()
        ))
    }

    fun deleteItem(id: Long) = viewModelScope.launch {
        val item = repository.getById(id) ?: return@launch
        repository.delete(item)
    }
}
