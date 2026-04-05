package com.zetta.dicodingevent.ui.screen.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zetta.dicodingevent.data.local.datastore.SettingPreferences
import com.zetta.dicodingevent.data.worker.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingViewModel(
    private val pref: SettingPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    var uiState by mutableStateOf(SettingUiState())
        private set

    companion object {
        private const val DAILY_REMINDER_WORK_NAME = "daily_reminder_work"
    }

    init {
        observeSettings()
    }

    private fun observeSettings() {
        observeThemeSetting()
        observeDailyReminderSetting()
    }

    fun onUiAction(action: SettingIntent) {
        when (action) {
            is SettingIntent.SelectTheme -> {
                uiState = uiState.copy(themeMode = action.themeMode)
                saveThemeSetting(action.themeMode)
            }
            is SettingIntent.SelectDailyReminder -> {
                uiState = uiState.copy(dailyReminder = action.isActive)
                saveDailyReminderSetting(action.isActive)
            }
        }
    }

    private fun observeThemeSetting() {
        viewModelScope.launch {
            pref.getThemeSetting().collect {
                uiState = uiState.copy(themeMode = it.toThemeMode())
            }
        }
    }

    private fun saveThemeSetting(themeMode: ThemeMode) {
        viewModelScope.launch {
            when (themeMode) {
                ThemeMode.LIGHT -> pref.saveThemeSetting(false)
                ThemeMode.DARK -> pref.saveThemeSetting(true)
                ThemeMode.SYSTEM -> pref.clearThemeSetting()
            }
        }
    }

    private fun observeDailyReminderSetting() {
        viewModelScope.launch {
            pref.getDailyReminderSetting().collect {
                uiState = uiState.copy(dailyReminder = it)
            }
        }
    }
    private fun saveDailyReminderSetting(isActive: Boolean) {
        viewModelScope.launch {
            pref.saveDailyReminderSetting(isActive)
            if (isActive) startDailyReminderWorker()
            else cancelDailyReminderWorker()
        }
    }

    private fun startDailyReminderWorker() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun cancelDailyReminderWorker() {
        workManager.cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }
}

fun Boolean?.toThemeMode() = when (this) {
    true -> ThemeMode.DARK
    false -> ThemeMode.LIGHT
    null -> ThemeMode.SYSTEM
}

data class SettingUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dailyReminder: Boolean = false
)

interface SettingIntent {
    data class SelectTheme(val themeMode: ThemeMode) : SettingIntent
    data class SelectDailyReminder(val isActive: Boolean) : SettingIntent
}

enum class ThemeMode { LIGHT, DARK, SYSTEM }