package com.zetta.dicodingevent.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDateToString(date: String, format: String = "EEE, MMM dd yyyy"): String {
        val stringFormatter = SimpleDateFormat(format, Locale.getDefault())
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date)
        val formattedDate = dateFormatter?.let { stringFormatter.format(dateFormatter) } ?: date

        return formattedDate
    }

    fun formatStringToDate(date: String, format: String = "yyyy-MM-dd HH:mm:ss"): Date {
        val stringFormatter = SimpleDateFormat(format, Locale.getDefault())
        return stringFormatter.parse(date) ?: Date()
    }
}