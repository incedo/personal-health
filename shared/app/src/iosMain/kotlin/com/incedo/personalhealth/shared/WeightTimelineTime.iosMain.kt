package com.incedo.personalhealth.shared

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

private fun weightFormatter(pattern: String): NSDateFormatter = NSDateFormatter().apply {
    dateFormat = pattern
    locale = NSLocale.currentLocale
}

internal actual fun formatWeightDayLabel(epochMillis: Long): String =
    weightFormatter("d MMM").stringFromDate(NSDate.dateWithTimeIntervalSince1970(epochMillis.toDouble() / 1000.0))

internal actual fun formatWeightMonthLabel(epochMillis: Long): String =
    weightFormatter("MMM yyyy").stringFromDate(NSDate.dateWithTimeIntervalSince1970(epochMillis.toDouble() / 1000.0))

internal actual fun yearOfEpochMillis(epochMillis: Long): Int {
    val date = NSDate.dateWithTimeIntervalSince1970(epochMillis.toDouble() / 1000.0)
    val components = NSCalendar.currentCalendar.components(NSCalendarUnitYear, fromDate = date)
    return components.year.toInt()
}

internal actual fun startOfYearEpochMillis(year: Int): Long {
    val components = NSDateComponents().apply {
        this.year = year.toLong()
        month = 1
        day = 1
    }
    val date = NSCalendar.currentCalendar.dateFromComponents(components) ?: NSDate()
    return (date.timeIntervalSince1970 * 1000.0).toLong()
}
