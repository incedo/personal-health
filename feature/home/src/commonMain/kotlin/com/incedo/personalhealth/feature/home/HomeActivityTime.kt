package com.incedo.personalhealth.feature.home

data class LocalDayWindow(
    val startEpochMillis: Long,
    val endEpochMillisExclusive: Long
)

expect fun currentHomeActivityEpochMillis(): Long

expect fun localDayWindow(epochMillis: Long): LocalDayWindow
