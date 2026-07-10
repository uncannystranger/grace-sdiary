package com.grace.sdiary.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.datastore.UserPreferences
import com.grace.sdiary.data.repository.*
import com.grace.sdiary.util.DateUtils
import com.grace.sdiary.data.model.LevelSystem
import com.grace.sdiary.data.model.LevelInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val plannerRepository: PlannerRepository,
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val weeklyProgress = plannerRepository.getByDateRange(DateUtils.weekStart(), DateUtils.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHabits = habitRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val diaryCount = diaryRepository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vocabCount = vocabularyRepository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val xp = prefs.dailyXp
    val streak = prefs.streakCount

    val levelInfo: StateFlow<LevelInfo> = combine(xp, streak) { xpVal, _ ->
        LevelSystem.getLevelInfo(xpVal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LevelSystem.getLevelInfo(0))

    val weeklyCompletion = weeklyProgress.map { items ->
        if (items.isEmpty()) 0f else items.count { it.isComplete }.toFloat() / items.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)
}
