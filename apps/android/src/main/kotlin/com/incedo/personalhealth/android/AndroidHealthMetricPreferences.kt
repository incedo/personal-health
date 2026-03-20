package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.health.HealthMetricType

internal val samsungPreferredMetrics = setOf(
    HealthMetricType.STEPS,
    HealthMetricType.HEART_RATE_BPM,
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.HEIGHT_CM,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.WATER_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE,
    HealthMetricType.BODY_MASS_INDEX,
    HealthMetricType.BASAL_METABOLIC_RATE_KCAL,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.BLOOD_GLUCOSE_MGDL,
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
    HealthMetricType.BODY_TEMPERATURE_CELSIUS,
    HealthMetricType.HYDRATION_ML,
    HealthMetricType.DIETARY_ENERGY_KCAL
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
