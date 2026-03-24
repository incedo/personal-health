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
    HealthMetricType.BODY_FAT_MASS_KG,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.MUSCLE_PERCENTAGE,
    HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE,
    HealthMetricType.FAT_FREE_PERCENTAGE,
    HealthMetricType.FAT_FREE_MASS_KG,
    HealthMetricType.BONE_MASS_KG,
    HealthMetricType.WATER_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE,
    HealthMetricType.BODY_MASS_INDEX,
    HealthMetricType.BASAL_METABOLIC_RATE_KCAL,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG,
    HealthMetricType.PULSE_RATE_BPM,
    HealthMetricType.BLOOD_GLUCOSE_MGDL,
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
    HealthMetricType.BODY_TEMPERATURE_CELSIUS,
    HealthMetricType.PROTEIN_G,
    HealthMetricType.CARBOHYDRATE_G,
    HealthMetricType.TOTAL_FAT_G,
    HealthMetricType.SATURATED_FAT_G,
    HealthMetricType.POLYUNSATURATED_FAT_G,
    HealthMetricType.MONOUNSATURATED_FAT_G,
    HealthMetricType.TRANS_FAT_G,
    HealthMetricType.DIETARY_FIBER_G,
    HealthMetricType.SUGAR_G,
    HealthMetricType.CHOLESTEROL_MG,
    HealthMetricType.SODIUM_MG,
    HealthMetricType.POTASSIUM_MG,
    HealthMetricType.CALCIUM_MG,
    HealthMetricType.IRON_MG,
    HealthMetricType.VITAMIN_A_MCG,
    HealthMetricType.VITAMIN_C_MG -> listOf(
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
