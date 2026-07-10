package com.grace.sdiary.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.CalendarEventEntity
import com.grace.sdiary.data.repository.CalendarEventRepository
import com.grace.sdiary.data.repository.PlannerRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: CalendarEventRepository,
    private val plannerRepository: PlannerRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    val monthEvents = _currentMonth.flatMapLatest { ym ->
        val start = ym.atDay(1)
        val end = ym.atEndOfMonth()
        eventRepository.getByMonth(
            start.atStartOfDay(java.time.LocalTime.MIDNIGHT).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
            end.atTime(java.time.LocalTime.MAX).toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthPlannerItems = _currentMonth.flatMapLatest { ym ->
        val start = ym.atDay(1)
        val end = ym.atEndOfMonth()
        plannerRepository.getByDateRange(
            start.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
            end.atTime(java.time.LocalTime.MAX).atZone(java.time.ZoneId.systemDefault()).toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun previousMonth() { _currentMonth.value = _currentMonth.value.minusMonths(1) }
    fun nextMonth() { _currentMonth.value = _currentMonth.value.plusMonths(1) }

    fun addEvent(title: String, date: Long, description: String? = null) = viewModelScope.launch {
        eventRepository.insert(CalendarEventEntity(
            title = title, date = date, description = description,
            createdAt = DateUtils.now()
        ))
    }
}
