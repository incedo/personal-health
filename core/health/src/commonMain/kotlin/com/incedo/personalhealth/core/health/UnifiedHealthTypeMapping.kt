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
    MUSCLE_MASS(HealthDomain.BODY_MEASUREMENTS),
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
    BLOOD_GLUCOSE(HealthDomain.VITALS),
    OXYGEN_SATURATION(HealthDomain.VITALS),
    RESPIRATORY_RATE(HealthDomain.VITALS),
    BODY_TEMPERATURE(HealthDomain.VITALS)
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

object UnifiedHealthTypeMappings {
    val all: List<UnifiedHealthTypeMapping> = listOf(
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.STEPS,
            healthConnectRecordClass = "StepsRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierStepCount",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.DISTANCE,
            healthConnectRecordClass = "DistanceRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierDistanceWalkingRunning",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.ACTIVE_ENERGY,
            healthConnectRecordClass = "ActiveCaloriesBurnedRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierActiveEnergyBurned",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.EXERCISE_SESSION,
            healthConnectRecordClass = "ExerciseSessionRecord",
            healthKitTypeIdentifier = "HKWorkoutType",
            healthKitTypeKind = HealthKitTypeKind.WORKOUT
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BODY_WEIGHT,
            healthConnectRecordClass = "WeightRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBodyMass",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.HEIGHT,
            healthConnectRecordClass = "HeightRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierHeight",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BODY_FAT_PERCENTAGE,
            healthConnectRecordClass = "BodyFatRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBodyFatPercentage",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.MUSCLE_MASS,
            healthConnectRecordClass = null,
            healthKitTypeIdentifier = null
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BONE_MASS,
            healthConnectRecordClass = null,
            healthKitTypeIdentifier = null
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.TOTAL_BODY_WATER,
            healthConnectRecordClass = null,
            healthKitTypeIdentifier = null
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BODY_MASS_INDEX,
            healthConnectRecordClass = null,
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBodyMassIndex",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BASAL_METABOLIC_RATE,
            healthConnectRecordClass = "BasalMetabolicRateRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBasalEnergyBurned",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.MENSTRUATION_PERIOD,
            healthConnectRecordClass = "MenstruationPeriodRecord",
            healthKitTypeIdentifier = "HKCategoryTypeIdentifierMenstrualFlow",
            healthKitTypeKind = HealthKitTypeKind.CATEGORY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.MENSTRUAL_FLOW,
            healthConnectRecordClass = "MenstruationFlowRecord",
            healthKitTypeIdentifier = "HKCategoryTypeIdentifierMenstrualFlow",
            healthKitTypeKind = HealthKitTypeKind.CATEGORY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.OVULATION_TEST,
            healthConnectRecordClass = "OvulationTestRecord",
            healthKitTypeIdentifier = "HKCategoryTypeIdentifierOvulationTestResult",
            healthKitTypeKind = HealthKitTypeKind.CATEGORY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.DIETARY_ENERGY,
            healthConnectRecordClass = "NutritionRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierDietaryEnergyConsumed",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.HYDRATION,
            healthConnectRecordClass = "HydrationRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierDietaryWater",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.SLEEP_SESSION,
            healthConnectRecordClass = "SleepSessionRecord",
            healthKitTypeIdentifier = "HKCategoryTypeIdentifierSleepAnalysis",
            healthKitTypeKind = HealthKitTypeKind.CATEGORY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.HEART_RATE,
            healthConnectRecordClass = "HeartRateRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierHeartRate",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.RESTING_HEART_RATE,
            healthConnectRecordClass = "RestingHeartRateRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierRestingHeartRate",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.HEART_RATE_VARIABILITY,
            healthConnectRecordClass = "HeartRateVariabilityRmssdRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierHeartRateVariabilitySDNN",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BLOOD_PRESSURE_SYSTOLIC,
            healthConnectRecordClass = "BloodPressureRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBloodPressureSystolic",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BLOOD_PRESSURE_DIASTOLIC,
            healthConnectRecordClass = "BloodPressureRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBloodPressureDiastolic",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BLOOD_GLUCOSE,
            healthConnectRecordClass = "BloodGlucoseRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBloodGlucose",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.OXYGEN_SATURATION,
            healthConnectRecordClass = "OxygenSaturationRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierOxygenSaturation",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.RESPIRATORY_RATE,
            healthConnectRecordClass = "RespiratoryRateRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierRespiratoryRate",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        ),
        UnifiedHealthTypeMapping(
            canonicalType = CanonicalHealthDataType.BODY_TEMPERATURE,
            healthConnectRecordClass = "BodyTemperatureRecord",
            healthKitTypeIdentifier = "HKQuantityTypeIdentifierBodyTemperature",
            healthKitTypeKind = HealthKitTypeKind.QUANTITY
        )
    )
}
