package com.incedo.personalhealth.feature.home

@JsFun("() => Date.now()")
private external fun activityDateNowEpochMillis(): Double

@JsFun("epochMillis => { const start = new Date(epochMillis); start.setHours(0, 0, 0, 0); return start.getTime(); }")
private external fun activityDayStartEpochMillis(epochMillis: Double): Double

@JsFun("epochMillis => { const end = new Date(epochMillis); end.setHours(0, 0, 0, 0); end.setDate(end.getDate() + 1); return end.getTime(); }")
private external fun activityDayEndEpochMillis(epochMillis: Double): Double

actual fun currentHomeActivityEpochMillis(): Long = activityDateNowEpochMillis().toLong()

actual fun localDayWindow(epochMillis: Long): LocalDayWindow = LocalDayWindow(
    startEpochMillis = activityDayStartEpochMillis(epochMillis.toDouble()).toLong(),
    endEpochMillisExclusive = activityDayEndEpochMillis(epochMillis.toDouble()).toLong()
)
