package com.incedo.personalhealth.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

@Composable
fun rememberActivityClock(active: Boolean): Long = produceState(
    initialValue = currentHomeActivityEpochMillis(),
    key1 = active
) {
    value = currentHomeActivityEpochMillis()
    if (!active) return@produceState
    while (true) {
        delay(1_000L)
        value = currentHomeActivityEpochMillis()
    }
}.value
