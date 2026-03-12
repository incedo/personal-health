# Unified Health Model Mapping (Health Connect + HealthKit)

This document maps Android Health Connect and Apple HealthKit data types to one canonical model.

## Primary sources
- Android Health Connect records package summary:
  - https://developer.android.com/reference/androidx/health/connect/client/records/package-summary
- Apple HealthKit type identifiers:
  - https://developer.apple.com/documentation/healthkit/hkquantitytypeidentifier
  - https://developer.apple.com/documentation/healthkit/hkcategorytypeidentifier
  - https://developer.apple.com/documentation/healthkit/hkworkouttype

## Canonical domains
- `ACTIVITY`
- `BODY_MEASUREMENTS`
- `CYCLE_TRACKING`
- `NUTRITION`
- `SLEEP`
- `VITALS`

## Canonical mapping set

| Canonical Type | Domain | Health Connect | HealthKit |
|---|---|---|---|
| `STEPS` | `ACTIVITY` | `StepsRecord` | `HKQuantityTypeIdentifierStepCount` |
| `DISTANCE` | `ACTIVITY` | `DistanceRecord` | `HKQuantityTypeIdentifierDistanceWalkingRunning` |
| `ACTIVE_ENERGY` | `ACTIVITY` | `ActiveCaloriesBurnedRecord` | `HKQuantityTypeIdentifierActiveEnergyBurned` |
| `EXERCISE_SESSION` | `ACTIVITY` | `ExerciseSessionRecord` | `HKWorkoutType` |
| `BODY_WEIGHT` | `BODY_MEASUREMENTS` | `WeightRecord` | `HKQuantityTypeIdentifierBodyMass` |
| `HEIGHT` | `BODY_MEASUREMENTS` | `HeightRecord` | `HKQuantityTypeIdentifierHeight` |
| `BODY_FAT_PERCENTAGE` | `BODY_MEASUREMENTS` | `BodyFatRecord` | `HKQuantityTypeIdentifierBodyFatPercentage` |
| `MENSTRUATION_PERIOD` | `CYCLE_TRACKING` | `MenstruationPeriodRecord` | `HKCategoryTypeIdentifierMenstrualFlow` |
| `MENSTRUAL_FLOW` | `CYCLE_TRACKING` | `MenstruationFlowRecord` | `HKCategoryTypeIdentifierMenstrualFlow` |
| `OVULATION_TEST` | `CYCLE_TRACKING` | `OvulationTestRecord` | `HKCategoryTypeIdentifierOvulationTestResult` |
| `DIETARY_ENERGY` | `NUTRITION` | `NutritionRecord` | `HKQuantityTypeIdentifierDietaryEnergyConsumed` |
| `HYDRATION` | `NUTRITION` | `HydrationRecord` | `HKQuantityTypeIdentifierDietaryWater` |
| `SLEEP_SESSION` | `SLEEP` | `SleepSessionRecord` | `HKCategoryTypeIdentifierSleepAnalysis` |
| `HEART_RATE` | `VITALS` | `HeartRateRecord` | `HKQuantityTypeIdentifierHeartRate` |
| `RESTING_HEART_RATE` | `VITALS` | `RestingHeartRateRecord` | `HKQuantityTypeIdentifierRestingHeartRate` |
| `HEART_RATE_VARIABILITY` | `VITALS` | `HeartRateVariabilityRmssdRecord` | `HKQuantityTypeIdentifierHeartRateVariabilitySDNN` |
| `BLOOD_PRESSURE_SYSTOLIC` | `VITALS` | `BloodPressureRecord` | `HKQuantityTypeIdentifierBloodPressureSystolic` |
| `BLOOD_PRESSURE_DIASTOLIC` | `VITALS` | `BloodPressureRecord` | `HKQuantityTypeIdentifierBloodPressureDiastolic` |
| `BLOOD_GLUCOSE` | `VITALS` | `BloodGlucoseRecord` | `HKQuantityTypeIdentifierBloodGlucose` |
| `OXYGEN_SATURATION` | `VITALS` | `OxygenSaturationRecord` | `HKQuantityTypeIdentifierOxygenSaturation` |
| `RESPIRATORY_RATE` | `VITALS` | `RespiratoryRateRecord` | `HKQuantityTypeIdentifierRespiratoryRate` |
| `BODY_TEMPERATURE` | `VITALS` | `BodyTemperatureRecord` | `HKQuantityTypeIdentifierBodyTemperature` |

## Code location
- Canonical enums and mapping list:
  - `core/health/src/commonMain/kotlin/com/incedo/personalhealth/core/health/UnifiedHealthTypeMapping.kt`

## Notes
- The mapping is semantic and normalized; units and sample granularity are aligned in gateway-level adapters.
- Some platform-specific types have no 1:1 equivalent and are intentionally excluded from the cross-platform canonical set.
