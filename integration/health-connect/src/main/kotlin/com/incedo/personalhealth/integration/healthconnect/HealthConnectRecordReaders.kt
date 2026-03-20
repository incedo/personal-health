package com.incedo.personalhealth.integration.healthconnect

import androidx.health.connect.client.HealthConnectClient
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
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

internal suspend fun readHealthConnectRecords(
    client: HealthConnectClient,
    request: HealthReadRequest
): List<HealthRecord> {
    val records = mutableListOf<HealthRecord>()
    val range = TimeRangeFilter.between(
        Instant.ofEpochMilli(request.startEpochMillis),
        Instant.ofEpochMilli(request.endEpochMillis)
    )
    val limit = request.limit.coerceAtLeast(1)

    if (HealthMetricType.STEPS in request.metrics && records.size < limit) {
        records += readPaged(client, StepsRecord::class, range, limit - records.size) { step ->
            listOf(step.toRecord(HealthMetricType.STEPS, step.count.toDouble(), step.startTime.toEpochMilli(), step.endTime.toEpochMilli()))
        }
    }
    if (HealthMetricType.HEART_RATE_BPM in request.metrics && records.size < limit) {
        records += readPaged(client, HeartRateRecord::class, range, limit - records.size) { heartRate ->
            heartRate.samples.map { sample ->
                heartRate.toRecord(HealthMetricType.HEART_RATE_BPM, sample.beatsPerMinute.toDouble(), sample.time.toEpochMilli(), sample.time.toEpochMilli(), "${heartRate.metadata.id}:${sample.time.toEpochMilli()}")
            }
        }
    }
    if (HealthMetricType.SLEEP_DURATION_MINUTES in request.metrics && records.size < limit) {
        records += readPaged(client, SleepSessionRecord::class, range, limit - records.size) { sleep ->
            listOf(
                sleep.toRecord(
                    metric = HealthMetricType.SLEEP_DURATION_MINUTES,
                    value = Duration.between(sleep.startTime, sleep.endTime).toMinutes().toDouble(),
                    startEpochMillis = sleep.startTime.toEpochMilli(),
                    endEpochMillis = sleep.endTime.toEpochMilli()
                )
            )
        }
    }
    if (HealthMetricType.ACTIVE_ENERGY_KCAL in request.metrics && records.size < limit) {
        records += readPaged(client, ActiveCaloriesBurnedRecord::class, range, limit - records.size) { calories ->
            listOf(calories.toRecord(HealthMetricType.ACTIVE_ENERGY_KCAL, calories.energy.inKilocalories, calories.startTime.toEpochMilli(), calories.endTime.toEpochMilli()))
        }
    }
    if (HealthMetricType.BODY_WEIGHT_KG in request.metrics && records.size < limit) {
        records += readPaged(client, WeightRecord::class, range, limit - records.size) { weight ->
            listOf(weight.toRecord(HealthMetricType.BODY_WEIGHT_KG, weight.weight.inKilograms, weight.time.toEpochMilli(), weight.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.HEIGHT_CM in request.metrics && records.size < limit) {
        records += readPaged(client, HeightRecord::class, range, limit - records.size) { height ->
            listOf(height.toRecord(HealthMetricType.HEIGHT_CM, height.height.inMeters * 100.0, height.time.toEpochMilli(), height.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.BODY_FAT_PERCENTAGE in request.metrics && records.size < limit) {
        records += readPaged(client, BodyFatRecord::class, range, limit - records.size) { bodyFat ->
            listOf(bodyFat.toRecord(HealthMetricType.BODY_FAT_PERCENTAGE, bodyFat.percentage.value, bodyFat.time.toEpochMilli(), bodyFat.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.BASAL_METABOLIC_RATE_KCAL in request.metrics && records.size < limit) {
        records += readPaged(client, BasalMetabolicRateRecord::class, range, limit - records.size) { rate ->
            listOf(rate.toRecord(HealthMetricType.BASAL_METABOLIC_RATE_KCAL, rate.basalMetabolicRate.inKilocaloriesPerDay, rate.time.toEpochMilli(), rate.time.toEpochMilli()))
        }
    }
    if (request.metrics.any { it == HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG || it == HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG } && records.size < limit) {
        records += readPaged(client, BloodPressureRecord::class, range, limit - records.size) { pressure ->
            buildList {
                if (HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG in request.metrics) {
                    add(pressure.toRecord(HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG, pressure.systolic.inMillimetersOfMercury, pressure.time.toEpochMilli(), pressure.time.toEpochMilli(), "${pressure.metadata.id}:systolic"))
                }
                if (HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG in request.metrics) {
                    add(pressure.toRecord(HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG, pressure.diastolic.inMillimetersOfMercury, pressure.time.toEpochMilli(), pressure.time.toEpochMilli(), "${pressure.metadata.id}:diastolic"))
                }
            }
        }
    }
    if (HealthMetricType.BLOOD_GLUCOSE_MGDL in request.metrics && records.size < limit) {
        records += readPaged(client, BloodGlucoseRecord::class, range, limit - records.size) { glucose ->
            listOf(glucose.toRecord(HealthMetricType.BLOOD_GLUCOSE_MGDL, glucose.level.inMilligramsPerDeciliter, glucose.time.toEpochMilli(), glucose.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.OXYGEN_SATURATION_PERCENTAGE in request.metrics && records.size < limit) {
        records += readPaged(client, OxygenSaturationRecord::class, range, limit - records.size) { oxygen ->
            listOf(oxygen.toRecord(HealthMetricType.OXYGEN_SATURATION_PERCENTAGE, oxygen.percentage.value, oxygen.time.toEpochMilli(), oxygen.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.BODY_TEMPERATURE_CELSIUS in request.metrics && records.size < limit) {
        records += readPaged(client, BodyTemperatureRecord::class, range, limit - records.size) { temperature ->
            listOf(temperature.toRecord(HealthMetricType.BODY_TEMPERATURE_CELSIUS, temperature.temperature.inCelsius, temperature.time.toEpochMilli(), temperature.time.toEpochMilli()))
        }
    }
    if (HealthMetricType.HYDRATION_ML in request.metrics && records.size < limit) {
        records += readPaged(client, HydrationRecord::class, range, limit - records.size) { hydration ->
            listOf(hydration.toRecord(HealthMetricType.HYDRATION_ML, hydration.volume.inMilliliters, hydration.startTime.toEpochMilli(), hydration.endTime.toEpochMilli()))
        }
    }
    if (HealthMetricType.DIETARY_ENERGY_KCAL in request.metrics && records.size < limit) {
        records += readPaged(client, NutritionRecord::class, range, limit - records.size) { nutrition ->
            val energy = nutrition.energy ?: return@readPaged emptyList()
            listOf(nutrition.toRecord(HealthMetricType.DIETARY_ENERGY_KCAL, energy.inKilocalories, nutrition.startTime.toEpochMilli(), nutrition.endTime.toEpochMilli()))
        }
    }

    return records
}

internal suspend fun readTodayHealthConnectStepsSnapshot(
    client: HealthConnectClient,
    bucketSizeHours: Int
): HealthConnectGateway.TodayStepsSnapshot {
    val safeBucketHours = bucketSizeHours.coerceIn(1, 24)
    val now = Instant.now()
    val zoneId = ZoneId.systemDefault()
    val labelFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startOfDay = now.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant()
    val startEpochMillis = startOfDay.toEpochMilli()
    val nowEpochMillis = now.toEpochMilli()
    val bucketSizeMillis = safeBucketHours * 60L * 60L * 1000L
    val elapsedMillis = (nowEpochMillis - startEpochMillis).coerceAtLeast(1L)
    val bucketCount = ((elapsedMillis + bucketSizeMillis - 1L) / bucketSizeMillis).toInt().coerceAtLeast(1)
    val bucketValues = MutableList(bucketCount) { 0 }
    val range = TimeRangeFilter.between(startOfDay, now)

    readPaged(client, StepsRecord::class, range, Int.MAX_VALUE) { listOf(it) }.forEach { record ->
        val bucketIndex = ((record.endTime.toEpochMilli() - startEpochMillis) / bucketSizeMillis).toInt().coerceIn(0, bucketValues.lastIndex)
        bucketValues[bucketIndex] += record.count.toInt()
    }

    return HealthConnectGateway.TodayStepsSnapshot(
        totalSteps = bucketValues.sum(),
        buckets = bucketValues.mapIndexed { index, value ->
            val bucketStart = Instant.ofEpochMilli(startEpochMillis + (index * bucketSizeMillis)).atZone(zoneId)
            HealthConnectGateway.TodayStepBucket(label = bucketStart.format(labelFormatter), steps = value)
        }
    )
}

private suspend fun <T : Record, R> readPaged(
    client: HealthConnectClient,
    recordType: KClass<T>,
    range: TimeRangeFilter,
    limit: Int,
    mapRecord: (T) -> List<R>
): List<R> {
    if (limit <= 0) return emptyList()
    val records = mutableListOf<R>()
    var pageToken: String? = null
    val pageSize = HealthConnectGateway.PAGE_SIZE.coerceAtMost(limit)

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
        if (pageToken == null || response.records.isEmpty()) break
    }
    return records
}

private fun Record.toRecord(
    metric: HealthMetricType,
    value: Double,
    startEpochMillis: Long,
    endEpochMillis: Long,
    idOverride: String? = null
): HealthRecord = HealthRecord(
    id = idOverride ?: metadata.id.ifBlank { "$metric:$startEpochMillis" },
    metric = metric,
    value = value,
    startEpochMillis = startEpochMillis,
    endEpochMillis = endEpochMillis,
    source = HealthDataSource.HEALTH_CONNECT
)
