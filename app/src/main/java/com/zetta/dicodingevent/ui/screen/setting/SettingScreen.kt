package com.zetta.dicodingevent.ui.screen.setting

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zetta.dicodingevent.R

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
) {
    SettingContent(
        uiState = viewModel.uiState,
        onUiAction = viewModel::onUiAction,
        modifier = Modifier.statusBarsPadding(),
    )
}

@Composable
private fun SettingContent(
    uiState: SettingUiState,
    onUiAction: (SettingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onUiAction(SettingIntent.SelectDailyReminder(true))
        }
    }

    Surface(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            overscrollEffect = null
        ) {
            item {
                Text(
                    text = stringResource(R.string.setting),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Column {
                    ThemeModeSelection(
                        themeMode = uiState.themeMode,
                        onThemeModeSelected = { onUiAction(SettingIntent.SelectTheme(it)) }
                    )
                    DailyReminderSelection(
                        isActive = uiState.dailyReminder,
                        onDailyReminderSelected = { checked ->
                            val isAlreadyGranted = ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                            if (!isAlreadyGranted) {
                                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onUiAction(SettingIntent.SelectDailyReminder(checked))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeSelection(
    themeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable { showDialog = true }
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.setting_theme),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = when(themeMode) {
                ThemeMode.LIGHT -> stringResource(R.string.setting_theme_light)
                ThemeMode.DARK -> stringResource(R.string.setting_theme_dark)
                ThemeMode.SYSTEM -> stringResource(R.string.setting_system_default)
            },
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light)
        )
    }

    if (showDialog) {
        ThemeModeSelectionDialog(
            selectedThemeMode = themeMode,
            onThemeModeSelected = {
                onThemeModeSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun DailyReminderSelection(
    isActive: Boolean,
    onDailyReminderSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column{
            Text(
                text = stringResource(R.string.setting_theme),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.setting_recommendation_event),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light)
            )
        }
        Switch(
            checked = isActive,
            onCheckedChange = onDailyReminderSelected
        )
    }
}

@Composable
private fun ThemeModeSelectionDialog(
    selectedThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val radioOptions = listOf(
        ThemeMode.LIGHT,
        ThemeMode.DARK,
        ThemeMode.SYSTEM
    )
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions.first { it == selectedThemeMode }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onThemeModeSelected(selectedOption) }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.setting_theme_dialog_title)) },
        text = {
            Column(Modifier.selectableGroup()) {
                radioOptions.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = when (option) {
                                ThemeMode.LIGHT -> stringResource(R.string.setting_theme_light)
                                ThemeMode.DARK -> stringResource(R.string.setting_theme_dark)
                                ThemeMode.SYSTEM -> stringResource(R.string.setting_system_default)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    )
}