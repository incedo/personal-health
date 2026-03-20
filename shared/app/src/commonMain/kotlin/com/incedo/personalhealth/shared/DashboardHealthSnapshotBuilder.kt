package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthMetricType

internal data class DashboardHealthSnapshot(
    val uiState: DashboardHealthUiState,
    val updatedAtEpochMillis: Long,
    val metricMetadata: List<PersistedHealthMetricMetadata>
)

internal fun buildDashboardHealthSnapshot(
    event: HealthEvent.DashboardRecordsUpdated
): DashboardHealthSnapshot = DashboardHealthSnapshot(
    uiState = buildDashboardHealthUiState(event),
    updatedAtEpochMillis = event.emittedAtEpochMillis,
    metricMetadata = HealthMetricType.entries.map { metric ->
        val records = event.records.filter { it.metric == metric }
        PersistedHealthMetricMetadata(
            metricId = metric.key,
            recordCount = records.size,
            latestRecordEpochMillis = records.maxOfOrNull { it.endEpochMillis }
        )
    }
)
