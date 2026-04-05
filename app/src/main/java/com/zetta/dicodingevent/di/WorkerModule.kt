package com.zetta.dicodingevent.di

import com.zetta.dicodingevent.data.worker.DailyReminderWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { DailyReminderWorker(get(), get(), get(), get(), get()) }
}