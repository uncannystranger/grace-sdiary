package com.grace.sdiary.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import com.grace.sdiary.data.repository.ReminderRepository
import com.grace.sdiary.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    val allReminders = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val upcoming = repository.getUpcoming(DateUtils.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val overdue = repository.getOverdue(DateUtils.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, dateTime: Long, type: String = "general", repeat: String? = null, description: String? = null) = viewModelScope.launch {
        repository.insert(ReminderEntity(
            title = title, dateTime = dateTime, type = type,
            repeatInterval = repeat, description = description,
            notificationId = (1000..9999).random(),
            createdAt = DateUtils.now()
        ))
    }

    fun toggleEnabled(reminder: ReminderEntity) = viewModelScope.launch {
        repository.update(reminder.copy(isEnabled = !reminder.isEnabled))
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.delete(reminder)
    }

    fun snooze(reminder: ReminderEntity) = viewModelScope.launch {
        repository.update(reminder.copy(snoozedUntil = DateUtils.now() + 600_000))
    }
}
