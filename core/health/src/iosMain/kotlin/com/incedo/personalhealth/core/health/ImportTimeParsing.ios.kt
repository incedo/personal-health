package com.incedo.personalhealth.core.health

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.timeIntervalSince1970

private const val APPLE_REFERENCE_DATE_UNIX_EPOCH_SECONDS = 978_307_200.0

actual fun parseImportDateTimeToEpochMillis(rawValue: String): Long? {
    val normalized = rawValue.trim().removeSurrounding("\"")
    if (normalized.isBlank()) return null
    val dateAndTime = normalized.split(" ")
    val dateParts = dateAndTime.firstOrNull()?.split("-") ?: return null
    if (dateParts.size != 3) return null
    val timeParts = dateAndTime.getOrNull(1)?.split(":").orEmpty()

    val components = NSDateComponents().apply {
        year = dateParts[0].toLongOrNull() ?: return null
        month = dateParts[1].toLongOrNull() ?: return null
        day = dateParts[2].toLongOrNull() ?: return null
        hour = timeParts.getOrNull(0)?.toLongOrNull() ?: 0L
        minute = timeParts.getOrNull(1)?.toLongOrNull() ?: 0L
        second = timeParts.getOrNull(2)?.toLongOrNull() ?: 0L
    }
    val calendar = NSCalendar.currentCalendar.apply {
    }
    val date = calendar.dateFromComponents(components) ?: return null
    return (date.timeIntervalSince1970 * 1000.0).toLong()
}

actual fun importDayWindow(epochMillis: Long): CanonicalHealthImportWindow {
    val calendar = NSCalendar.currentCalendar
    val epochSeconds = epochMillis.toDouble() / 1000.0
    val date = NSDate(
        timeIntervalSinceReferenceDate = epochSeconds - APPLE_REFERENCE_DATE_UNIX_EPOCH_SECONDS
    )
    val startDate = calendar.startOfDayForDate(date)
    val nextDate = calendar.dateByAddingUnit(NSCalendarUnitDay, 1, startDate, 0u) ?: startDate
    return CanonicalHealthImportWindow(
        startEpochMillis = (startDate.timeIntervalSince1970 * 1000.0).toLong(),
        endEpochMillis = (nextDate.timeIntervalSince1970 * 1000.0).toLong() - 1L
    )
}
