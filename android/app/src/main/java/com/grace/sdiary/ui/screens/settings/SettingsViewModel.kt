package com.grace.sdiary.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.datastore.UserPreferences
import com.grace.sdiary.data.repository.*
import com.grace.sdiary.data.backup.BackupManager
import com.grace.sdiary.data.export.ExportManager
import com.grace.sdiary.data.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val backupManager: BackupManager,
    private val exportManager: ExportManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val theme = prefs.theme.map { mode ->
        when (mode) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val fontScale = prefs.fontScale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val reducedMotion = prefs.reducedMotion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val highContrast = prefs.highContrast
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName = prefs.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sihaam")

    val notificationsEnabled = prefs.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language = prefs.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    fun setTheme(mode: ThemeMode) = viewModelScope.launch {
        prefs.setTheme(mode.name.lowercase())
    }

    fun setFontScale(scale: Float) = viewModelScope.launch {
        prefs.setFontScale(scale)
    }

    fun setReducedMotion(enabled: Boolean) = viewModelScope.launch {
        prefs.setReducedMotion(enabled)
    }

    fun setHighContrast(enabled: Boolean) = viewModelScope.launch {
        prefs.setHighContrast(enabled)
    }

    fun setUserName(name: String) = viewModelScope.launch {
        prefs.setUserName(name)
    }

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        prefs.setNotificationsEnabled(enabled)
    }

    fun createBackup() = viewModelScope.launch {
        _backupStatus.value = "Creating backup..."
        try {
            backupManager.createBackup()
            _backupStatus.value = "Backup created successfully"
        } catch (e: Exception) {
            _backupStatus.value = "Backup failed: ${e.message}"
        }
    }

    fun restoreBackup(uri: Uri) = viewModelScope.launch {
        _backupStatus.value = "Restoring backup..."
        try {
            backupManager.restoreBackup(uri)
            _backupStatus.value = "Backup restored successfully"
        } catch (e: Exception) {
            _backupStatus.value = "Restore failed: ${e.message}"
        }
    }

    fun exportData(format: String) = viewModelScope.launch {
        _backupStatus.value = "Exporting..."
        try {
            val uri = exportManager.export(format)
            _backupStatus.value = "Exported successfully"
        } catch (e: Exception) {
            _backupStatus.value = "Export failed: ${e.message}"
        }
    }
}
