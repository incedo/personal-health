package com.incedo.personalhealth.integration.healthkit

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import platform.HealthKit.HKHealthStore

class HealthKitGateway(
    private val eventBus: AppEventBus? = null
) : HealthDataGateway {

    private val healthStore: HKHealthStore? =
        if (HKHealthStore.isHealthDataAvailable()) HKHealthStore() else null

    fun isAvailable(): Boolean = healthStore != null

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        val store = healthStore ?: return emptyList()
        val result = readHealthKitRecords(store, request)
            .sortedBy { it.startEpochMillis }
            .take(request.limit.coerceAtLeast(1))

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
}
