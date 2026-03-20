package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthRecord
import com.incedo.personalhealth.core.health.currentEpochMillis

internal data class DashboardHealthEventCache(
    val records: List<HealthRecord>,
    val dayStartEpochMillis: Long,
    val dayEndEpochMillis: Long
)

internal fun HealthEvent.DashboardRecordsUpdated.toCache(): DashboardHealthEventCache = DashboardHealthEventCache(
    records = records,
    dayStartEpochMillis = dayStartEpochMillis,
    dayEndEpochMillis = dayEndEpochMillis
)

internal suspend fun DashboardHealthEventCache.recalculate(eventBus: AppEventBus) {
    publishDashboardHealthEvents(
        records = records,
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        emittedAtEpochMillis = currentEpochMillis(),
        eventBus = eventBus
    )
}
