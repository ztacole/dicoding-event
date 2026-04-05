package com.zetta.dicodingevent.ui.platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.zetta.dicodingevent.R

class NotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val CHANNEL_NAME = "Daily Event Reminder"
        private const val NOTIFICATION_ID = 101
    }

    fun sendDailyReminderNotification(title: String, date: String, time: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.daily_reminder_title))
            .setContentText(context.getString(R.string.daily_reminder_message, title, date, time))
            .setSmallIcon(R.drawable.ic_event_upcoming)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSubText(context.getString(R.string.daily_reminder_subtext))
            .setAutoCancel(true)

        // This project min SDK is 33
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        builder.setChannelId(CHANNEL_ID)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}