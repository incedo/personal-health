package com.incedo.personalhealth.integration.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass

class HealthConnectGateway(
    context: Context,
    private val eventBus: AppEventBus? = null
) : HealthDataGateway {

    private val client: HealthConnectClient = HealthConnectClient.getOrCreate(context)

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        val records = mutableListOf<HealthRecord>()
        val range = TimeRangeFilter.between(
            Instant.ofEpochMilli(request.startEpochMillis),
            Instant.ofEpochMilli(request.endEpochMillis)
        )
        val limit = request.limit.coerceAtLeast(1)

        if (HealthMetricType.STEPS in request.metrics && records.size < limit) {
            records += readSteps(range, limit - records.size)
        }
        if (HealthMetricType.HEART_RATE_BPM in request.metrics && records.size < limit) {
            records += readHeartRate(range, limit - records.size)
        }
        if (HealthMetricType.SLEEP_DURATION_MINUTES in request.metrics && records.size < limit) {
            records += readSleepSessions(range, limit - records.size)
        }
        if (HealthMetricType.ACTIVE_ENERGY_KCAL in request.metrics && records.size < limit) {
            records += readActiveCalories(range, limit - records.size)
        }
        if (HealthMetricType.BODY_WEIGHT_KG in request.metrics && records.size < limit) {
            records += readBodyWeight(range, limit - records.size)
        }

        val result = records
            .sortedBy { it.startEpochMillis }
            .take(limit)

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

    private suspend fun readSteps(
        range: TimeRangeFilter,
        limit: Int
    ): List<HealthRecord> {
        return readPaged(
            recordType = StepsRecord::class,
            range = range,
            limit = limit
        ) { step: StepsRecord ->
            listOf(
                HealthRecord(
                    id = step.metadata.id.ifBlank { step.startTime.toEpochMilli().toString() },
                    metric = HealthMetricType.STEPS,
                    value = step.count.toDouble(),
                    unit = "count",
                    startEpochMillis = step.startTime.toEpochMilli(),
                    endEpochMillis = step.endTime.toEpochMilli(),
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        }
    }

    private suspend fun readHeartRate(
        range: TimeRangeFilter,
        limit: Int
    ): List<HealthRecord> {
        return readPaged(
            recordType = HeartRateRecord::class,
            range = range,
            limit = limit
        ) { heartRate: HeartRateRecord ->
            heartRate.samples.map { sample ->
                HealthRecord(
                    id = "${heartRate.metadata.id}:${sample.time.toEpochMilli()}",
                    metric = HealthMetricType.HEART_RATE_BPM,
                    value = sample.beatsPerMinute.toDouble(),
                    unit = "bpm",
                    startEpochMillis = sample.time.toEpochMilli(),
                    endEpochMillis = sample.time.toEpochMilli(),
                    source = HealthDataSource.HEALTH_CONNECT
                )
            }
        }
    }

    private suspend fun readSleepSessions(
        range: TimeRangeFilter,
        limit: Int
    ): List<HealthRecord> {
        return readPaged(
            recordType = SleepSessionRecord::class,
            range = range,
            limit = limit
        ) { sleep: SleepSessionRecord ->
            val durationMinutes = Duration.between(
                sleep.startTime,
                sleep.endTime
            ).toMinutes().toDouble()

            listOf(
                HealthRecord(
                    id = sleep.metadata.id.ifBlank { sleep.startTime.toEpochMilli().toString() },
                    metric = HealthMetricType.SLEEP_DURATION_MINUTES,
                    value = durationMinutes,
                    unit = "min",
                    startEpochMillis = sleep.startTime.toEpochMilli(),
                    endEpochMillis = sleep.endTime.toEpochMilli(),
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        }
    }

    private suspend fun readActiveCalories(
        range: TimeRangeFilter,
        limit: Int
    ): List<HealthRecord> {
        return readPaged(
            recordType = ActiveCaloriesBurnedRecord::class,
            range = range,
            limit = limit
        ) { calories: ActiveCaloriesBurnedRecord ->
            listOf(
                HealthRecord(
                    id = calories.metadata.id.ifBlank { calories.startTime.toEpochMilli().toString() },
                    metric = HealthMetricType.ACTIVE_ENERGY_KCAL,
                    value = calories.energy.inKilocalories,
                    unit = "kcal",
                    startEpochMillis = calories.startTime.toEpochMilli(),
                    endEpochMillis = calories.endTime.toEpochMilli(),
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        }
    }

    private suspend fun readBodyWeight(
        range: TimeRangeFilter,
        limit: Int
    ): List<HealthRecord> {
        return readPaged(
            recordType = WeightRecord::class,
            range = range,
            limit = limit
        ) { weight: WeightRecord ->
            listOf(
                HealthRecord(
                    id = weight.metadata.id.ifBlank { weight.time.toEpochMilli().toString() },
                    metric = HealthMetricType.BODY_WEIGHT_KG,
                    value = weight.weight.inKilograms,
                    unit = "kg",
                    startEpochMillis = weight.time.toEpochMilli(),
                    endEpochMillis = weight.time.toEpochMilli(),
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        }
    }

    private suspend fun <T : Record> readPaged(
        recordType: KClass<T>,
        range: TimeRangeFilter,
        limit: Int,
        mapRecord: (T) -> List<HealthRecord>
    ): List<HealthRecord> {
        if (limit <= 0) return emptyList()

        val records = mutableListOf<HealthRecord>()
        var pageToken: String? = null
        val pageSize = PAGE_SIZE.coerceAtMost(limit)

        while (records.size < limit) {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = recordType,
                    timeRangeFilter = range,
                    pageSize = pageSize,
                    pageToken = pageToken
                )
            )

            response.records.forEach { record ->
                if (records.size < limit) {
                    records += mapRecord(record).take(limit - records.size)
                }
            }

            pageToken = response.pageToken
            if (pageToken == null || response.records.isEmpty()) {
                break
            }
        }

        return records
    }

    companion object {
        private const val HEALTH_CONNECT_PROVIDER_PACKAGE = "com.google.android.apps.healthdata"
        private const val PAGE_SIZE = 500

        fun requiredPermissions(): Set<String> = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
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
