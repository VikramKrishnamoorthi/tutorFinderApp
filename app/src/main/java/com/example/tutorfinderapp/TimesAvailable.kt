package com.example.tutorfinderapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class TimesAvailable {
    data class TimesAvailable(
        val DOW: List<Int>,
        val Times: List<String>
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDOW(): String {
        val today = LocalDate.now()
        return today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String{
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val formattedTime = now.format(formatter)
    }
}