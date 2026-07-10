package com.grace.sdiary.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.repository.*
import com.grace.sdiary.data.model.DailyStats
import com.grace.sdiary.data.model.WeeklyProgress
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val plannerRepository: PlannerRepository,
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val dailyStats: StateFlow<DailyStats> = progressRepository.getDailyStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyStats())

    val weeklyProgress: StateFlow<WeeklyProgress> = progressRepository.getWeeklyProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyProgress())

    val todaySchedule = plannerRepository.getToday(DateUtils.todayStart())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val greeting = MutableStateFlow("")
    private val greetings = listOf(
        "Good Morning" to 5..11, "Good Afternoon" to 12..17,
        "Good Evening" to 18..21, "Good Night" to 22..24
    )

    init {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val msg = greetings.firstOrNull { (_, r) -> hour in r }?.first ?: "Hello"
        greeting.value = "$msg, Sihaam"
    }

    fun completePlannerItem(id: Long) = viewModelScope.launch {
        val item = plannerRepository.getById(id) ?: return@launch
        plannerRepository.update(item.copy(isComplete = !item.isComplete))
    }
}
