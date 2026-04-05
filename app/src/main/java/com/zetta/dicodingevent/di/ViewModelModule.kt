package com.zetta.dicodingevent.di

import androidx.work.WorkManager
import com.zetta.dicodingevent.ui.screen.setting.SettingViewModel
import com.zetta.dicodingevent.ui.screen.event.EventViewModel
import com.zetta.dicodingevent.ui.screen.favorite.FavoriteViewModel
import com.zetta.dicodingevent.ui.screen.finished.FinishedViewModel
import com.zetta.dicodingevent.ui.screen.home.HomeViewModel
import com.zetta.dicodingevent.ui.screen.upcoming.UpcomingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SettingViewModel(get(), WorkManager.getInstance(get())) }
    viewModel { HomeViewModel(get()) }
    viewModel { UpcomingViewModel(get()) }
    viewModel { FinishedViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { EventViewModel(get(), get(), get()) }
}