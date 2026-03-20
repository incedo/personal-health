package com.incedo.personalhealth.integration.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord

class HealthConnectGateway(
    context: Context,
    private val eventBus: AppEventBus? = null
) : HealthDataGateway {

    private val client: HealthConnectClient = HealthConnectClient.getOrCreate(context)

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        val result = readHealthConnectRecords(client, request)
            .sortedBy { it.startEpochMillis }
            .take(request.limit.coerceAtLeast(1))

        eventBus?.publish(
            HealthEvent.RecordsRead(
                source = HealthDataSource.HEALTH_CONNECT,
                request = request,
                count = result.size,
                emittedAtEpochMillis = System.currentTimeMillis()
            )
        )

        return result
    }

    suspend fun readTodayStepsSnapshot(
        bucketSizeHours: Int = DEFAULT_BUCKET_SIZE_HOURS
    ): TodayStepsSnapshot = readTodayHealthConnectStepsSnapshot(
        client = client,
        bucketSizeHours = bucketSizeHours
    )

    data class TodayStepsSnapshot(
        val totalSteps: Int,
        val buckets: List<TodayStepBucket>
    )

    data class TodayStepBucket(
        val label: String,
        val steps: Int
    )

    companion object {
        private const val HEALTH_CONNECT_PROVIDER_PACKAGE = "com.google.android.apps.healthdata"
        internal const val PAGE_SIZE = 500
        private const val DEFAULT_BUCKET_SIZE_HOURS = 2

        fun requiredPermissions(): Set<String> = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class),
            HealthPermission.getReadPermission(BodyFatRecord::class),
            HealthPermission.getReadPermission(BasalMetabolicRateRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(BloodGlucoseRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY
        )

        fun isAvailable(context: Context): Boolean {
            val status = HealthConnectClient.getSdkStatus(
                context,
                HEALTH_CONNECT_PROVIDER_PACKAGE
            )
            return status == HealthConnectClient.SDK_AVAILABLE
        }
    }
}
