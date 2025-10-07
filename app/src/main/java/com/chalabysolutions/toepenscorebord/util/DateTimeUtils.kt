package com.chalabysolutions.toepenscorebord.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateTimeUtils {

    fun formatDateToDayMonth(epochMillis: Long): String {
        val sdf = SimpleDateFormat("EEEE d MMMM", Locale.forLanguageTag("nl-NL"))
        return sdf.format(Date(epochMillis))
    }

    fun formatTimeToMinutes(epochMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.forLanguageTag("nl-NL"))
        return sdf.format(Date(epochMillis))
    }

    fun formatTimeToSeconds(epochMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("nl-NL"))
        return sdf.format(Date(epochMillis))
    }
}