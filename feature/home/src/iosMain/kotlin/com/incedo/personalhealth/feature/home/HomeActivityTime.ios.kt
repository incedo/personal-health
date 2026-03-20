package com.incedo.personalhealth.feature.home

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

private const val APPLE_REFERENCE_DATE_UNIX_EPOCH_SECONDS = 978_307_200.0

actual fun currentHomeActivityEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()

actual fun localDayWindow(epochMillis: Long): LocalDayWindow {
    val calendar = NSCalendar.currentCalendar
    val epochSeconds = epochMillis.toDouble() / 1000.0
    val date = NSDate(
        timeIntervalSinceReferenceDate = epochSeconds - APPLE_REFERENCE_DATE_UNIX_EPOCH_SECONDS
    )
    val start = calendar.startOfDayForDate(date)
    val end = calendar.dateByAddingUnit(NSCalendarUnitDay, 1, start, 0u) ?: date
    return LocalDayWindow(
        startEpochMillis = (start.timeIntervalSince1970 * 1000.0).toLong(),
        endEpochMillisExclusive = (end.timeIntervalSince1970 * 1000.0).toLong()
    )
}
