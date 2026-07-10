package com.grace.sdiary.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.repository.PlannerRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val plannerRepository: PlannerRepository
) : ViewModel() {

    // Focus timer state
    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _mode = MutableStateFlow("focus") // "focus" or "break"
    val mode: StateFlow<String> = _mode.asStateFlow()

    private val _pomodoroCount = MutableStateFlow(0)
    val pomodoroCount: StateFlow<Int> = _pomodoroCount.asStateFlow()

    private var timerJob: Job? = null
    private var focusDuration = 25 * 60L // 25 minutes
    private var breakDuration = 5 * 60L // 5 minutes

    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _elapsedSeconds.value++
                if (_elapsedSeconds.value >= focusDuration && _mode.value == "focus") {
                    _mode.value = "break"
                    _elapsedSeconds.value = 0
                    _pomodoroCount.value++
                } else if (_elapsedSeconds.value >= breakDuration && _mode.value == "break") {
                    _mode.value = "focus"
                    _elapsedSeconds.value = 0
                }
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _elapsedSeconds.value = 0
        _mode.value = "focus"
    }

    fun setFocusDuration(minutes: Int) {
        focusDuration = minutes * 60L
        if (!_isRunning.value) resetTimer()
    }

    fun setBreakDuration(minutes: Int) {
        breakDuration = minutes * 60L
    }

    val formattedTime: StateFlow<String> = _elapsedSeconds.map { seconds ->
        val remaining = if (_mode.value == "focus") focusDuration - seconds else breakDuration - seconds
        val mins = (remaining / 60).coerceAtLeast(0)
        val secs = (remaining % 60).coerceAtLeast(0)
        String.format("%02d:%02d", mins, secs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "25:00")

    val progress: StateFlow<Float> = _elapsedSeconds.map { seconds ->
        val total = if (_mode.value == "focus") focusDuration else breakDuration
        (seconds.toFloat() / total).coerceIn(0f, 1f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
