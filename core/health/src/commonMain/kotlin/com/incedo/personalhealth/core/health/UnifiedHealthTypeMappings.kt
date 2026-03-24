package com.incedo.personalhealth.core.health

object UnifiedHealthTypeMappings {
    val all: List<UnifiedHealthTypeMapping> = buildList {
        addAll(activityAndBodyMappings())
        addAll(cycleAndSleepMappings())
        addAll(vitalMappings())
        addAll(nutritionMappings())
    }
}

private fun activityAndBodyMappings() = listOf(
    mapping(CanonicalHealthDataType.STEPS, "StepsRecord", "HKQuantityTypeIdentifierStepCount", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.DISTANCE, "DistanceRecord", "HKQuantityTypeIdentifierDistanceWalkingRunning", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.ACTIVE_ENERGY, "ActiveCaloriesBurnedRecord", "HKQuantityTypeIdentifierActiveEnergyBurned", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.EXERCISE_SESSION, "ExerciseSessionRecord", "HKWorkoutType", HealthKitTypeKind.WORKOUT),
    mapping(CanonicalHealthDataType.BODY_WEIGHT, "WeightRecord", "HKQuantityTypeIdentifierBodyMass", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.HEIGHT, "HeightRecord", "HKQuantityTypeIdentifierHeight", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BODY_FAT_PERCENTAGE, "BodyFatRecord", "HKQuantityTypeIdentifierBodyFatPercentage", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BODY_FAT_MASS),
    mapping(CanonicalHealthDataType.MUSCLE_MASS),
    mapping(CanonicalHealthDataType.MUSCLE_PERCENTAGE),
    mapping(CanonicalHealthDataType.SKELETAL_MUSCLE_PERCENTAGE),
    mapping(CanonicalHealthDataType.FAT_FREE_PERCENTAGE),
    mapping(CanonicalHealthDataType.FAT_FREE_MASS),
    mapping(CanonicalHealthDataType.BONE_MASS),
    mapping(CanonicalHealthDataType.TOTAL_BODY_WATER),
    mapping(CanonicalHealthDataType.BODY_MASS_INDEX, null, "HKQuantityTypeIdentifierBodyMassIndex", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BASAL_METABOLIC_RATE, "BasalMetabolicRateRecord", "HKQuantityTypeIdentifierBasalEnergyBurned", HealthKitTypeKind.QUANTITY)
)

private fun cycleAndSleepMappings() = listOf(
    mapping(CanonicalHealthDataType.MENSTRUATION_PERIOD, "MenstruationPeriodRecord", "HKCategoryTypeIdentifierMenstrualFlow", HealthKitTypeKind.CATEGORY),
    mapping(CanonicalHealthDataType.MENSTRUAL_FLOW, "MenstruationFlowRecord", "HKCategoryTypeIdentifierMenstrualFlow", HealthKitTypeKind.CATEGORY),
    mapping(CanonicalHealthDataType.OVULATION_TEST, "OvulationTestRecord", "HKCategoryTypeIdentifierOvulationTestResult", HealthKitTypeKind.CATEGORY),
    mapping(CanonicalHealthDataType.DIETARY_ENERGY, "NutritionRecord", "HKQuantityTypeIdentifierDietaryEnergyConsumed", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.HYDRATION, "HydrationRecord", "HKQuantityTypeIdentifierDietaryWater", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.SLEEP_SESSION, "SleepSessionRecord", "HKCategoryTypeIdentifierSleepAnalysis", HealthKitTypeKind.CATEGORY)
)

private fun vitalMappings() = listOf(
    mapping(CanonicalHealthDataType.HEART_RATE, "HeartRateRecord", "HKQuantityTypeIdentifierHeartRate", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.RESTING_HEART_RATE, "RestingHeartRateRecord", "HKQuantityTypeIdentifierRestingHeartRate", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.HEART_RATE_VARIABILITY, "HeartRateVariabilityRmssdRecord", "HKQuantityTypeIdentifierHeartRateVariabilitySDNN", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BLOOD_PRESSURE_SYSTOLIC, "BloodPressureRecord", "HKQuantityTypeIdentifierBloodPressureSystolic", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BLOOD_PRESSURE_DIASTOLIC, "BloodPressureRecord", "HKQuantityTypeIdentifierBloodPressureDiastolic", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BLOOD_PRESSURE_MEAN),
    mapping(CanonicalHealthDataType.PULSE_RATE),
    mapping(CanonicalHealthDataType.BLOOD_GLUCOSE, "BloodGlucoseRecord", "HKQuantityTypeIdentifierBloodGlucose", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.OXYGEN_SATURATION, "OxygenSaturationRecord", "HKQuantityTypeIdentifierOxygenSaturation", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.RESPIRATORY_RATE, "RespiratoryRateRecord", "HKQuantityTypeIdentifierRespiratoryRate", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.BODY_TEMPERATURE, "BodyTemperatureRecord", "HKQuantityTypeIdentifierBodyTemperature", HealthKitTypeKind.QUANTITY)
)

private fun nutritionMappings() = listOf(
    mapping(CanonicalHealthDataType.PROTEIN, null, "HKQuantityTypeIdentifierDietaryProtein", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.CARBOHYDRATE, null, "HKQuantityTypeIdentifierDietaryCarbohydrates", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.TOTAL_FAT, null, "HKQuantityTypeIdentifierDietaryFatTotal", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.SATURATED_FAT, null, "HKQuantityTypeIdentifierDietaryFatSaturated", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.POLYUNSATURATED_FAT, null, "HKQuantityTypeIdentifierDietaryFatPolyunsaturated", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.MONOUNSATURATED_FAT, null, "HKQuantityTypeIdentifierDietaryFatMonounsaturated", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.TRANS_FAT, null, "HKQuantityTypeIdentifierDietaryFatTrans", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.DIETARY_FIBER, null, "HKQuantityTypeIdentifierDietaryFiber", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.SUGAR, null, "HKQuantityTypeIdentifierDietarySugar", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.CHOLESTEROL, null, "HKQuantityTypeIdentifierDietaryCholesterol", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.SODIUM, null, "HKQuantityTypeIdentifierDietarySodium", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.POTASSIUM, null, "HKQuantityTypeIdentifierDietaryPotassium", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.CALCIUM, null, "HKQuantityTypeIdentifierDietaryCalcium", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.IRON, null, "HKQuantityTypeIdentifierDietaryIron", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.VITAMIN_A, null, "HKQuantityTypeIdentifierDietaryVitaminA", HealthKitTypeKind.QUANTITY),
    mapping(CanonicalHealthDataType.VITAMIN_C, null, "HKQuantityTypeIdentifierDietaryVitaminC", HealthKitTypeKind.QUANTITY)
)

private fun mapping(
    canonicalType: CanonicalHealthDataType,
    healthConnectRecordClass: String? = null,
    healthKitTypeIdentifier: String? = null,
    healthKitTypeKind: HealthKitTypeKind? = null
) = UnifiedHealthTypeMapping(
    canonicalType = canonicalType,
    healthConnectRecordClass = healthConnectRecordClass,
    healthKitTypeIdentifier = healthKitTypeIdentifier,
    healthKitTypeKind = healthKitTypeKind
)
