package com.zetta.dicodingevent.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zetta.dicodingevent.data.local.datastore.SettingPreferences
import com.zetta.dicodingevent.data.repository.EventRepository
import com.zetta.dicodingevent.helper.DateFormatter
import com.zetta.dicodingevent.ui.platform.NotificationHelper
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val eventRepository: EventRepository,
    private val prefs: SettingPreferences,
    private val notificationHelper: NotificationHelper
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val isDailyReminderActive = prefs.getDailyReminderSetting().first()
        if (!isDailyReminderActive) return Result.success()

        return try {
            val response = eventRepository.getNearestEvent()
            if (response.events.isNotEmpty()) {
                val event = response.events[0]
                notificationHelper.sendDailyReminderNotification(
                    title = event.name,
                    date = DateFormatter.formatDateToString(event.beginTime, "MMM dd yyyy"),
                    time = DateFormatter.formatDateToString(event.beginTime, "HH:mm")
                )
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}