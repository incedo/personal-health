package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthRecord

internal fun preferredMetricRecords(
    records: List<HealthRecord>,
    metric: HealthMetricType
): List<HealthRecord> {
    val metricRecords = records.filter { it.metric == metric }
    if (metricRecords.isEmpty()) return emptyList()

    val preferredSource = metricSourcePriority(metric)
        .firstOrNull { source -> metricRecords.any { it.source == source } }
        ?: return metricRecords

    return metricRecords.filter { it.source == preferredSource }
}

private fun metricSourcePriority(metric: HealthMetricType): List<HealthDataSource> = when (metric) {
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.HEIGHT_CM,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.BONE_MASS_KG,
    HealthMetricType.WATER_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE,
    HealthMetricType.BODY_MASS_INDEX,
    HealthMetricType.BASAL_METABOLIC_RATE_KCAL,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.BLOOD_GLUCOSE_MGDL,
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
    HealthMetricType.BODY_TEMPERATURE_CELSIUS -> listOf(
        HealthDataSource.WITHINGS,
        HealthDataSource.SAMSUNG_HEALTH,
        HealthDataSource.HEALTH_CONNECT,
        HealthDataSource.HEALTHKIT,
        HealthDataSource.UNKNOWN
    )

    else -> listOf(
        HealthDataSource.SAMSUNG_HEALTH,
        HealthDataSource.HEALTH_CONNECT,
        HealthDataSource.HEALTHKIT,
        HealthDataSource.WITHINGS,
        HealthDataSource.UNKNOWN
    )
}
