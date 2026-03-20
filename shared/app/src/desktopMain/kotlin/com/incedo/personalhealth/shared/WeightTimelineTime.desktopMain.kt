package com.incedo.personalhealth.shared

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val weightDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
private val weightMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
private val weightZoneId: ZoneId = ZoneId.systemDefault()

internal actual fun formatWeightDayLabel(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis)
    .atZone(weightZoneId)
    .toLocalDate()
    .format(weightDayFormatter)

internal actual fun formatWeightMonthLabel(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis)
    .atZone(weightZoneId)
    .toLocalDate()
    .withDayOfMonth(1)
    .format(weightMonthFormatter)

internal actual fun yearOfEpochMillis(epochMillis: Long): Int = Instant.ofEpochMilli(epochMillis)
    .atZone(weightZoneId)
    .toLocalDate()
    .year

internal actual fun startOfYearEpochMillis(year: Int): Long = LocalDate.of(year, 1, 1)
    .atStartOfDay(weightZoneId)
    .toInstant()
    .toEpochMilli()
