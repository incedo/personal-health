package com.incedo.personalhealth.feature.home

import java.time.Instant
import java.time.ZoneId

actual fun currentHomeActivityEpochMillis(): Long = System.currentTimeMillis()

actual fun localDayWindow(epochMillis: Long): LocalDayWindow {
    val zoneId = ZoneId.systemDefault()
    val localDate = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
    val start = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    val end = localDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    return LocalDayWindow(startEpochMillis = start, endEpochMillisExclusive = end)
}
