package com.zetta.dicodingevent

import android.app.Application
import com.zetta.dicodingevent.di.coreModule
import com.zetta.dicodingevent.di.viewModelModule
import com.zetta.dicodingevent.di.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            workManagerFactory()
            modules(coreModule, viewModelModule, workerModule)
        }
    }
}