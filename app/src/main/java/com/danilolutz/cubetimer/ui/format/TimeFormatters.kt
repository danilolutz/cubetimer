package com.danilolutz.cubetimer.ui.format

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(millis: Long): String = "%d:%02d.%02d".format(
    millis / MILLIS_PER_MINUTE,
    (millis / MILLIS_PER_SECOND) % SECONDS_PER_MINUTE,
    (millis % MILLIS_PER_SECOND) / MILLIS_PER_CENTISECOND,
)

fun formatSolveDialogTime(millis: Long): String = if (millis < MILLIS_PER_MINUTE) {
    "%d.%02d".format(millis / MILLIS_PER_SECOND, (millis % MILLIS_PER_SECOND) / MILLIS_PER_CENTISECOND)
} else {
    formatTime(millis)
}

fun formatSolveDateTime(completedAtMillis: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(completedAtMillis))

private const val MILLIS_PER_CENTISECOND = 10
private const val MILLIS_PER_SECOND = 1_000
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE
