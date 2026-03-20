package com.incedo.personalhealth.integration.healthkit

import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import platform.Foundation.NSDate
import platform.Foundation.NSPredicate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetricPrefixCenti
import platform.HealthKit.HKMetricPrefixDeci
import platform.HealthKit.HKMetricPrefixKilo
import platform.HealthKit.HKMetricPrefixMilli
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionNone
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyMassIndex
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierDietaryEnergyConsumed
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierOxygenSaturation
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSample
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKUnit
import platform.HealthKit.countUnit
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.gramUnitWithMetricPrefix
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.literUnitWithMetricPrefix
import platform.HealthKit.meterUnitWithMetricPrefix
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.HealthKit.unitDividedByUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal suspend fun readHealthKitRecords(
    store: HKHealthStore,
    request: HealthReadRequest
): List<HealthRecord> {
    val limit = request.limit.coerceAtLeast(1)
    val predicate = request.toPredicate()
    val records = mutableListOf<HealthRecord>()

    if (HealthMetricType.STEPS in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierStepCount, with(HKUnit.Companion) { countUnit() }, HealthMetricType.STEPS, predicate, limit - records.size)
    }
    if (HealthMetricType.HEART_RATE_BPM in request.metrics && records.size < limit) {
        records += readQuantityRecords(
            store,
            HKQuantityTypeIdentifierHeartRate,
            with(HKUnit.Companion) { countUnit().unitDividedByUnit(minuteUnit()) },
            HealthMetricType.HEART_RATE_BPM,
            predicate,
            limit - records.size
        )
    }
    if (HealthMetricType.SLEEP_DURATION_MINUTES in request.metrics && records.size < limit) {
        records += readSleepRecords(store, predicate, limit - records.size)
    }
    if (HealthMetricType.ACTIVE_ENERGY_KCAL in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierActiveEnergyBurned, with(HKUnit.Companion) { kilocalorieUnit() }, HealthMetricType.ACTIVE_ENERGY_KCAL, predicate, limit - records.size)
    }
    if (HealthMetricType.BODY_WEIGHT_KG in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierBodyMass, with(HKUnit.Companion) { gramUnitWithMetricPrefix(HKMetricPrefixKilo) }, HealthMetricType.BODY_WEIGHT_KG, predicate, limit - records.size)
    }
    if (HealthMetricType.HEIGHT_CM in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierHeight, with(HKUnit.Companion) { meterUnitWithMetricPrefix(HKMetricPrefixCenti) }, HealthMetricType.HEIGHT_CM, predicate, limit - records.size)
    }
    if (HealthMetricType.BODY_FAT_PERCENTAGE in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierBodyFatPercentage, with(HKUnit.Companion) { percentUnit() }, HealthMetricType.BODY_FAT_PERCENTAGE, predicate, limit - records.size)
    }
    if (HealthMetricType.BODY_MASS_INDEX in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierBodyMassIndex, with(HKUnit.Companion) { countUnit() }, HealthMetricType.BODY_MASS_INDEX, predicate, limit - records.size)
    }
    if (request.metrics.any { it == HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG || it == HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG } && records.size < limit) {
        if (HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG in request.metrics) {
            records += readQuantityRecords(store, HKQuantityTypeIdentifierBloodPressureSystolic, bloodPressureUnit(), HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG, predicate, limit - records.size)
        }
        if (HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG in request.metrics && records.size < limit) {
            records += readQuantityRecords(store, HKQuantityTypeIdentifierBloodPressureDiastolic, bloodPressureUnit(), HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG, predicate, limit - records.size)
        }
    }
    if (HealthMetricType.BLOOD_GLUCOSE_MGDL in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierBloodGlucose, bloodGlucoseUnit(), HealthMetricType.BLOOD_GLUCOSE_MGDL, predicate, limit - records.size)
    }
    if (HealthMetricType.OXYGEN_SATURATION_PERCENTAGE in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierOxygenSaturation, with(HKUnit.Companion) { percentUnit() }, HealthMetricType.OXYGEN_SATURATION_PERCENTAGE, predicate, limit - records.size)
    }
    if (HealthMetricType.BODY_TEMPERATURE_CELSIUS in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierBodyTemperature, bodyTemperatureUnit(), HealthMetricType.BODY_TEMPERATURE_CELSIUS, predicate, limit - records.size)
    }
    if (HealthMetricType.HYDRATION_ML in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierDietaryWater, with(HKUnit.Companion) { literUnitWithMetricPrefix(HKMetricPrefixMilli) }, HealthMetricType.HYDRATION_ML, predicate, limit - records.size)
    }
    if (HealthMetricType.DIETARY_ENERGY_KCAL in request.metrics && records.size < limit) {
        records += readQuantityRecords(store, HKQuantityTypeIdentifierDietaryEnergyConsumed, with(HKUnit.Companion) { kilocalorieUnit() }, HealthMetricType.DIETARY_ENERGY_KCAL, predicate, limit - records.size)
    }

    return records
}

private suspend fun readQuantityRecords(
    store: HKHealthStore,
    identifier: String?,
    unit: HKUnit,
    metric: HealthMetricType,
    predicate: NSPredicate?,
    limit: Int,
    transform: (Double) -> Double = { it }
): List<HealthRecord> {
    if (identifier == null || limit <= 0) return emptyList()
    val sampleType = HKObjectType.quantityTypeForIdentifier(identifier) ?: return emptyList()
    return readSamples(store, sampleType, predicate, limit).mapNotNull { sample ->
        val quantitySample = sample as? HKQuantitySample ?: return@mapNotNull null
        val startEpochMillis = (quantitySample.startDate.timeIntervalSince1970 * 1000.0).toLong()
        val endEpochMillis = (quantitySample.endDate.timeIntervalSince1970 * 1000.0).toLong()
        HealthRecord(
            id = quantitySample.UUID.UUIDString ?: "${metric.key}:$startEpochMillis",
            metric = metric,
            value = transform(quantitySample.quantity.doubleValueForUnit(unit)),
            startEpochMillis = startEpochMillis,
            endEpochMillis = endEpochMillis,
            source = HealthDataSource.HEALTHKIT
        )
    }
}

private suspend fun readSleepRecords(
    store: HKHealthStore,
    predicate: NSPredicate?,
    limit: Int
): List<HealthRecord> {
    if (limit <= 0) return emptyList()
    val sleepType = HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis) ?: return emptyList()
    return readSamples(store, sleepType, predicate, limit).mapNotNull { sample ->
        val sleepSample = sample as? HKCategorySample ?: return@mapNotNull null
        val startEpochMillis = (sleepSample.startDate.timeIntervalSince1970 * 1000.0).toLong()
        val endEpochMillis = (sleepSample.endDate.timeIntervalSince1970 * 1000.0).toLong()
        HealthRecord(
            id = sleepSample.UUID.UUIDString ?: "sleep:$startEpochMillis",
            metric = HealthMetricType.SLEEP_DURATION_MINUTES,
            value = ((endEpochMillis - startEpochMillis).coerceAtLeast(0L) / 60000.0),
            startEpochMillis = startEpochMillis,
            endEpochMillis = endEpochMillis,
            source = HealthDataSource.HEALTHKIT,
            metadata = mapOf("sleepStageValue" to sleepSample.value.toString())
        )
    }
}

private suspend fun readSamples(
    store: HKHealthStore,
    sampleType: Any,
    predicate: NSPredicate?,
    limit: Int
): List<HKSample> = suspendCoroutine { continuation ->
    val query = HKSampleQuery(
        sampleType = sampleType as HKSampleType,
        predicate = predicate,
        limit = limit.toULong(),
        sortDescriptors = null
    ) { _: HKSampleQuery?, samples: List<*>?, _: platform.Foundation.NSError? ->
        continuation.resume(samples?.mapNotNull { it as? HKSample } ?: emptyList())
    }
    store.executeQuery(query as HKQuery)
}

private fun HealthReadRequest.toPredicate(): NSPredicate {
    val startDate = with(NSDate.Companion) { dateWithTimeIntervalSince1970(startEpochMillis.toDouble() / 1000.0) }
    val endDate = with(NSDate.Companion) { dateWithTimeIntervalSince1970(endEpochMillis.toDouble() / 1000.0) }
    return with(HKQuery.Companion) {
        predicateForSamplesWithStartDate(startDate = startDate, endDate = endDate, options = HKQueryOptionNone)
    }
}

private fun bloodPressureUnit(): HKUnit = with(HKUnit.Companion) { millimeterOfMercuryUnit() }

private fun bloodGlucoseUnit(): HKUnit = with(HKUnit.Companion) {
    gramUnitWithMetricPrefix(HKMetricPrefixMilli).unitDividedByUnit(literUnitWithMetricPrefix(HKMetricPrefixDeci))
}

private fun bodyTemperatureUnit(): HKUnit = with(HKUnit.Companion) { degreeCelsiusUnit() }

internal fun nowEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
