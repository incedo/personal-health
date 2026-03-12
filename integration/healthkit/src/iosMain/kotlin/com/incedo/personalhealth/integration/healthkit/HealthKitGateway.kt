package com.incedo.personalhealth.integration.healthkit

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthEvent
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
import platform.HealthKit.HKMetricPrefixKilo
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSample
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKUnit
import platform.HealthKit.HKQueryOptionNone
import platform.HealthKit.countUnit
import platform.HealthKit.gramUnitWithMetricPrefix
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.HealthKit.unitDividedByUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HealthKitGateway(
    private val eventBus: AppEventBus? = null
) : HealthDataGateway {

    private val healthStore: HKHealthStore? =
        if (HKHealthStore.isHealthDataAvailable()) HKHealthStore() else null

    fun isAvailable(): Boolean = healthStore != null

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        val store = healthStore ?: return emptyList()
        val limit = request.limit.coerceAtLeast(1)
        val startDate = with(NSDate.Companion) {
            dateWithTimeIntervalSince1970(request.startEpochMillis.toDouble() / 1000.0)
        }
        val endDate = with(NSDate.Companion) {
            dateWithTimeIntervalSince1970(request.endEpochMillis.toDouble() / 1000.0)
        }
        val predicate = with(HKQuery.Companion) {
            predicateForSamplesWithStartDate(
                startDate = startDate,
                endDate = endDate,
                options = HKQueryOptionNone
            )
        }

        val records = mutableListOf<HealthRecord>()

        if (HealthMetricType.STEPS in request.metrics && records.size < limit) {
            records += readQuantityRecords(
                store = store,
                sampleType = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount),
                unit = with(HKUnit.Companion) { countUnit() },
                metric = HealthMetricType.STEPS,
                canonicalUnit = "count",
                predicate = predicate,
                limit = limit - records.size
            )
        }
        if (HealthMetricType.HEART_RATE_BPM in request.metrics && records.size < limit) {
            records += readQuantityRecords(
                store = store,
                sampleType = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate),
                unit = with(HKUnit.Companion) {
                    countUnit().unitDividedByUnit(minuteUnit())
                },
                metric = HealthMetricType.HEART_RATE_BPM,
                canonicalUnit = "bpm",
                predicate = predicate,
                limit = limit - records.size
            )
        }
        if (HealthMetricType.SLEEP_DURATION_MINUTES in request.metrics && records.size < limit) {
            records += readSleepRecords(
                store = store,
                predicate = predicate,
                limit = limit - records.size
            )
        }
        if (HealthMetricType.ACTIVE_ENERGY_KCAL in request.metrics && records.size < limit) {
            records += readQuantityRecords(
                store = store,
                sampleType = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned),
                unit = with(HKUnit.Companion) { kilocalorieUnit() },
                metric = HealthMetricType.ACTIVE_ENERGY_KCAL,
                canonicalUnit = "kcal",
                predicate = predicate,
                limit = limit - records.size
            )
        }
        if (HealthMetricType.BODY_WEIGHT_KG in request.metrics && records.size < limit) {
            records += readQuantityRecords(
                store = store,
                sampleType = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass),
                unit = with(HKUnit.Companion) { gramUnitWithMetricPrefix(HKMetricPrefixKilo) },
                metric = HealthMetricType.BODY_WEIGHT_KG,
                canonicalUnit = "kg",
                predicate = predicate,
                limit = limit - records.size
            )
        }

        val result = records
            .sortedBy { it.startEpochMillis }
            .take(limit)

        eventBus?.publish(
            HealthEvent.RecordsRead(
                source = HealthDataSource.HEALTHKIT,
                request = request,
                count = result.size,
                emittedAtEpochMillis = nowEpochMillis()
            )
        )

        return result
    }

    private suspend fun readQuantityRecords(
        store: HKHealthStore,
        sampleType: Any?,
        unit: HKUnit,
        metric: HealthMetricType,
        canonicalUnit: String,
        predicate: NSPredicate?,
        limit: Int
    ): List<HealthRecord> {
        if (sampleType == null || limit <= 0) return emptyList()

        val samples = readSamples(
            store = store,
            sampleType = sampleType,
            predicate = predicate,
            limit = limit
        )

        return samples.mapNotNull { sample ->
            val quantitySample = sample as? HKQuantitySample ?: return@mapNotNull null
            val startEpochMillis = (quantitySample.startDate.timeIntervalSince1970 * 1000.0).toLong()
            val endEpochMillis = (quantitySample.endDate.timeIntervalSince1970 * 1000.0).toLong()
            HealthRecord(
                id = quantitySample.UUID.UUIDString ?: "$metric:$startEpochMillis",
                metric = metric,
                value = quantitySample.quantity.doubleValueForUnit(unit),
                unit = canonicalUnit,
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
        val sleepType = HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)
            ?: return emptyList()

        val samples = readSamples(
            store = store,
            sampleType = sleepType,
            predicate = predicate,
            limit = limit
        )

        return samples.mapNotNull { sample ->
            val sleepSample = sample as? HKCategorySample ?: return@mapNotNull null
            val startEpochMillis = (sleepSample.startDate.timeIntervalSince1970 * 1000.0).toLong()
            val endEpochMillis = (sleepSample.endDate.timeIntervalSince1970 * 1000.0).toLong()
            val durationMinutes = ((endEpochMillis - startEpochMillis).coerceAtLeast(0L) / 60000.0)
            HealthRecord(
                id = sleepSample.UUID.UUIDString ?: "sleep:$startEpochMillis",
                metric = HealthMetricType.SLEEP_DURATION_MINUTES,
                value = durationMinutes,
                unit = "min",
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
            val typed = samples?.mapNotNull { it as? HKSample } ?: emptyList()
            continuation.resume(typed)
        }
        store.executeQuery(query as HKQuery)
    }

    private fun nowEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
}
