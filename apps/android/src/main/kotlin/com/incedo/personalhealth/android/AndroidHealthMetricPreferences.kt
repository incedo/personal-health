package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.health.HealthMetricType

internal val samsungPreferredMetrics = setOf(
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG
)

internal fun samsungMetricsFor(metrics: Set<HealthMetricType>): Set<HealthMetricType> =
    metrics intersect samsungPreferredMetrics

internal fun healthConnectFallbackMetricsFor(
    metrics: Set<HealthMetricType>,
    samsungReady: Boolean
): Set<HealthMetricType> = if (samsungReady) {
    metrics - samsungPreferredMetrics
} else {
    metrics
}
