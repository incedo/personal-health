package com.incedo.personalhealth.core.health

@JsFun("() => Date.now()")
external fun dateNowEpochMillis(): Double

actual fun currentEpochMillis(): Long = dateNowEpochMillis().toLong()
