package com.orbboard.boardoar.util

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

fun parseColor(hex: String): Color {
    return try {
        Color(AndroidColor.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF3ED2FF)
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun formatShortDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun formatDayName(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

fun startOfWeek(): Long {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun startOfDay(daysAgo: Int = 0): Long {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
