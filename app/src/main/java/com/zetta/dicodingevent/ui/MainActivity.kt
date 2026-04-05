package com.zetta.dicodingevent.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.zetta.dicodingevent.ui.navigation.MainNavigation
import com.zetta.dicodingevent.ui.screen.setting.SettingViewModel
import com.zetta.dicodingevent.ui.screen.setting.ThemeMode
import com.zetta.dicodingevent.ui.theme.DicodingEventTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupUI()
    }

    private fun setupUI() =
        setContent {
            val uiState = viewModel.uiState

            uiState.themeMode?.let {
                val themeMode = when (it) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

                DicodingEventTheme(themeMode) {
                    MainNavigation(viewModel)
                }
            }
        }
}