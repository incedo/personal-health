package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.health.HealthMetricType

internal val samsungCanonicalMetrics = setOf(
    HealthMetricType.STEPS,
    HealthMetricType.HEART_RATE_BPM,
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.HEIGHT_CM,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.BODY_FAT_MASS_KG,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.MUSCLE_PERCENTAGE,
    HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE,
    HealthMetricType.FAT_FREE_PERCENTAGE,
    HealthMetricType.FAT_FREE_MASS_KG,
    HealthMetricType.WATER_MASS_KG,
    HealthMetricType.BODY_MASS_INDEX,
    HealthMetricType.BASAL_METABOLIC_RATE_KCAL,
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG,
    HealthMetricType.PULSE_RATE_BPM,
    HealthMetricType.BLOOD_GLUCOSE_MGDL,
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
    HealthMetricType.BODY_TEMPERATURE_CELSIUS,
    HealthMetricType.HYDRATION_ML,
    HealthMetricType.DIETARY_ENERGY_KCAL,
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
    HealthMetricType.VITAMIN_C_MG
)

internal val dashboardWindowMetrics = setOf(
    HealthMetricType.STEPS,
    HealthMetricType.HEART_RATE_BPM,
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BLOOD_GLUCOSE_MGDL,
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
    HealthMetricType.BODY_TEMPERATURE_CELSIUS,
    HealthMetricType.HYDRATION_ML,
    HealthMetricType.DIETARY_ENERGY_KCAL,
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
    HealthMetricType.VITAMIN_C_MG
)

internal val dashboardHistoricalMetrics: Set<HealthMetricType> = samsungCanonicalMetrics - dashboardWindowMetrics

internal val androidSyncMetrics: Set<HealthMetricType> = samsungCanonicalMetrics
