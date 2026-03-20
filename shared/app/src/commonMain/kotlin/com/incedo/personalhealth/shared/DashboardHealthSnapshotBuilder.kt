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
    metricMetadata = DASHBOARD_HEALTH_METRICS.map { metric ->
        val records = event.records.filter { it.metric == metric }
        PersistedHealthMetricMetadata(
            metricId = metric.name,
            recordCount = records.size,
            latestRecordEpochMillis = records.maxOfOrNull { it.endEpochMillis }
        )
    }
)

private val DASHBOARD_HEALTH_METRICS = listOf(
    HealthMetricType.STEPS,
    HealthMetricType.HEART_RATE_BPM,
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.BONE_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG
)
