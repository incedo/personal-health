package com.incedo.personalhealth.integration.healthkit

import com.incedo.personalhealth.core.health.HealthChangeSignal
import com.incedo.personalhealth.core.health.HealthChangeSignalSource
import com.incedo.personalhealth.core.health.HealthChangeTrigger
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthSignalSubscription
import kotlinx.coroutines.CoroutineScope
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKObserverQuery
import platform.HealthKit.HKQuery
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierDietaryEnergyConsumed
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierOxygenSaturation
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKSampleType
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class HealthKitObserverSignalSource : HealthChangeSignalSource {

    override fun start(
        scope: CoroutineScope,
        onSignal: (HealthChangeSignal) -> Unit
    ): HealthSignalSubscription {
        val store = if (HKHealthStore.isHealthDataAvailable()) HKHealthStore() else null
            ?: return HealthSignalSubscription {}
        val startedQueries = mutableListOf<HKObserverQuery>()

        supportedSampleTypes().forEach { sampleType ->
            val query = HKObserverQuery(sampleType, null) { _, completionHandler, _ ->
                onSignal(
                    HealthChangeSignal(
                        intentId = buildObserverIntentId(),
                        source = HealthDataSource.HEALTHKIT,
                        trigger = HealthChangeTrigger.PLATFORM_OBSERVER
                    )
                )
                completionHandler?.invoke()
            }
            startedQueries += query
            store.executeQuery(query as HKQuery)
        }

        return HealthSignalSubscription {
            startedQueries.forEach { query ->
                store.stopQuery(query as HKQuery)
            }
            startedQueries.clear()
        }
    }

    private fun supportedSampleTypes(): List<HKSampleType> {
        val types = mutableListOf<HKSampleType>()
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierOxygenSaturation)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)?.let { types += it }
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryEnergyConsumed)?.let { types += it }
        HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)?.let { types += it }
        return types
    }

    private fun buildObserverIntentId(): String {
        val bucket = (NSDate().timeIntervalSince1970 * 1000.0).toLong() / 5_000L
        return "healthkit-observer:$bucket"
    }
}
