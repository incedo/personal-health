package com.incedo.personalhealth.core.health

enum class HealthDomain {
    ACTIVITY,
    BODY_MEASUREMENTS,
    CYCLE_TRACKING,
    NUTRITION,
    SLEEP,
    VITALS
}

enum class CanonicalHealthDataType(
    val domain: HealthDomain
) {
    STEPS(HealthDomain.ACTIVITY),
    DISTANCE(HealthDomain.ACTIVITY),
    ACTIVE_ENERGY(HealthDomain.ACTIVITY),
    EXERCISE_SESSION(HealthDomain.ACTIVITY),

    BODY_WEIGHT(HealthDomain.BODY_MEASUREMENTS),
    HEIGHT(HealthDomain.BODY_MEASUREMENTS),
    BODY_FAT_PERCENTAGE(HealthDomain.BODY_MEASUREMENTS),
    BODY_FAT_MASS(HealthDomain.BODY_MEASUREMENTS),
    MUSCLE_MASS(HealthDomain.BODY_MEASUREMENTS),
    MUSCLE_PERCENTAGE(HealthDomain.BODY_MEASUREMENTS),
    SKELETAL_MUSCLE_PERCENTAGE(HealthDomain.BODY_MEASUREMENTS),
    FAT_FREE_PERCENTAGE(HealthDomain.BODY_MEASUREMENTS),
    FAT_FREE_MASS(HealthDomain.BODY_MEASUREMENTS),
    BONE_MASS(HealthDomain.BODY_MEASUREMENTS),
    TOTAL_BODY_WATER(HealthDomain.BODY_MEASUREMENTS),
    BODY_MASS_INDEX(HealthDomain.BODY_MEASUREMENTS),
    BASAL_METABOLIC_RATE(HealthDomain.BODY_MEASUREMENTS),

    MENSTRUATION_PERIOD(HealthDomain.CYCLE_TRACKING),
    MENSTRUAL_FLOW(HealthDomain.CYCLE_TRACKING),
    OVULATION_TEST(HealthDomain.CYCLE_TRACKING),

    DIETARY_ENERGY(HealthDomain.NUTRITION),
    HYDRATION(HealthDomain.NUTRITION),

    SLEEP_SESSION(HealthDomain.SLEEP),

    HEART_RATE(HealthDomain.VITALS),
    RESTING_HEART_RATE(HealthDomain.VITALS),
    HEART_RATE_VARIABILITY(HealthDomain.VITALS),
    BLOOD_PRESSURE_SYSTOLIC(HealthDomain.VITALS),
    BLOOD_PRESSURE_DIASTOLIC(HealthDomain.VITALS),
    BLOOD_PRESSURE_MEAN(HealthDomain.VITALS),
    PULSE_RATE(HealthDomain.VITALS),
    BLOOD_GLUCOSE(HealthDomain.VITALS),
    OXYGEN_SATURATION(HealthDomain.VITALS),
    RESPIRATORY_RATE(HealthDomain.VITALS),
    BODY_TEMPERATURE(HealthDomain.VITALS),

    PROTEIN(HealthDomain.NUTRITION),
    CARBOHYDRATE(HealthDomain.NUTRITION),
    TOTAL_FAT(HealthDomain.NUTRITION),
    SATURATED_FAT(HealthDomain.NUTRITION),
    POLYUNSATURATED_FAT(HealthDomain.NUTRITION),
    MONOUNSATURATED_FAT(HealthDomain.NUTRITION),
    TRANS_FAT(HealthDomain.NUTRITION),
    DIETARY_FIBER(HealthDomain.NUTRITION),
    SUGAR(HealthDomain.NUTRITION),
    CHOLESTEROL(HealthDomain.NUTRITION),
    SODIUM(HealthDomain.NUTRITION),
    POTASSIUM(HealthDomain.NUTRITION),
    CALCIUM(HealthDomain.NUTRITION),
    IRON(HealthDomain.NUTRITION),
    VITAMIN_A(HealthDomain.NUTRITION),
    VITAMIN_C(HealthDomain.NUTRITION)
}

enum class HealthKitTypeKind {
    QUANTITY,
    CATEGORY,
    WORKOUT
}

data class UnifiedHealthTypeMapping(
    val canonicalType: CanonicalHealthDataType,
    val healthConnectRecordClass: String?,
    val healthKitTypeIdentifier: String?,
    val healthKitTypeKind: HealthKitTypeKind? = null
)
