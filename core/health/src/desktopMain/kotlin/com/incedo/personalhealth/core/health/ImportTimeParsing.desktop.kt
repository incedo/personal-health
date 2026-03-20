package com.incedo.personalhealth.core.health

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val importDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val importDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

actual fun parseImportDateTimeToEpochMillis(rawValue: String): Long? {
    val normalized = rawValue.trim().removeSurrounding("\"")
    if (normalized.isBlank()) return null
    return runCatching {
        when {
            normalized.length == 10 -> LocalDate.parse(normalized, importDateFormatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            else -> LocalDateTime.parse(normalized, importDateTimeFormatter)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }.getOrNull()
}

actual fun importDayWindow(epochMillis: Long): CanonicalHealthImportWindow {
    val zoneId = ZoneId.systemDefault()
    val localDate = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
    val start = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    val end = localDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1L
    return CanonicalHealthImportWindow(startEpochMillis = start, endEpochMillis = end)
}
