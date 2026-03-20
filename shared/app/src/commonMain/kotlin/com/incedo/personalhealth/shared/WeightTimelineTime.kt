package com.incedo.personalhealth.shared

internal expect fun formatWeightDayLabel(epochMillis: Long): String

internal expect fun formatWeightMonthLabel(epochMillis: Long): String

internal expect fun yearOfEpochMillis(epochMillis: Long): Int

internal expect fun startOfYearEpochMillis(year: Int): Long
