package com.zetta.dicodingevent.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.zetta.dicodingevent.data.local.datastore.SettingPreferences
import com.zetta.dicodingevent.data.local.room.AppDatabase
import com.zetta.dicodingevent.data.remote.ktor.ApiConfig
import com.zetta.dicodingevent.data.remote.ktor.EventService
import com.zetta.dicodingevent.data.repository.EventRepository
import com.zetta.dicodingevent.ui.platform.NotificationHelper
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore("settings")

val coreModule = module {
    single { Dispatchers.IO }

    // Remote services
    single { ApiConfig.provideClient() }
    single { EventService(get()) }

    // Local services
    single { androidContext().dataStore }
    single { SettingPreferences(get()) }
    single { AppDatabase.build(androidContext()) }
    single { get<AppDatabase>().favoriteDao() }

    // Repositories
    single { EventRepository(get(), get(), get()) }

    single { NotificationHelper(get()) }
}