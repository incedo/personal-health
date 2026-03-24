package com.incedo.personalhealth.integration.samsunghealth

import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal val samsungReadableDataTypes = listOf(
    DataTypes.SLEEP,
    DataTypes.STEPS,
    DataTypes.HEART_RATE,
    DataTypes.ACTIVITY_SUMMARY,
    DataTypes.BODY_COMPOSITION,
    DataTypes.BLOOD_PRESSURE,
    DataTypes.BLOOD_GLUCOSE,
    DataTypes.BLOOD_OXYGEN,
    DataTypes.BODY_TEMPERATURE,
    DataTypes.WATER_INTAKE,
    DataTypes.NUTRITION
)

internal val samsungRecordReaders = listOf(
    SamsungRecordReader(setOf(HealthMetricType.SLEEP_DURATION_MINUTES)) { store, request -> readSleepRecords(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.STEPS)) { store, request -> readStepsAggregate(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.ACTIVE_ENERGY_KCAL)) { store, request -> readActiveEnergyAggregate(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.HEART_RATE_BPM)) { store, request -> readHeartRateRecords(store, request) },
    SamsungRecordReader(
        setOf(
            HealthMetricType.BODY_WEIGHT_KG,
            HealthMetricType.HEIGHT_CM,
            HealthMetricType.BODY_FAT_PERCENTAGE,
            HealthMetricType.BODY_FAT_MASS_KG,
            HealthMetricType.MUSCLE_MASS_KG,
            HealthMetricType.MUSCLE_PERCENTAGE,
            HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE,
            HealthMetricType.FAT_FREE_PERCENTAGE,
            HealthMetricType.FAT_FREE_MASS_KG,
            HealthMetricType.WATER_MASS_KG,
            HealthMetricType.BODY_MASS_INDEX,
            HealthMetricType.BASAL_METABOLIC_RATE_KCAL
        )
    ) { store, request -> readBodyCompositionRecords(store, request) },
    SamsungRecordReader(
        setOf(
            HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
            HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
            HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG,
            HealthMetricType.PULSE_RATE_BPM
        )
    ) { store, request -> readBloodPressureRecords(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.BLOOD_GLUCOSE_MGDL)) { store, request -> readBloodGlucoseRecords(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.OXYGEN_SATURATION_PERCENTAGE)) { store, request -> readBloodOxygenRecords(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.BODY_TEMPERATURE_CELSIUS)) { store, request -> readBodyTemperatureRecords(store, request) },
    SamsungRecordReader(setOf(HealthMetricType.HYDRATION_ML)) { store, request -> readHydrationRecords(store, request) },
    SamsungRecordReader(samsungNutritionMetrics) { store, request -> readNutritionRecords(store, request) }
)

internal class SamsungRecordReader(
    private val metrics: Set<HealthMetricType>,
    private val loader: suspend (HealthDataStore, HealthReadRequest) -> List<HealthRecord>
) {
    fun supports(requestedMetrics: Set<HealthMetricType>): Boolean = requestedMetrics.any { it in metrics }
    suspend fun read(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> = loader(store, request)
}

private suspend fun readSleepRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.SLEEP.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val duration = point.getValue(DataType.SleepType.DURATION) ?: return@mapNotNull null
        val startTime = point.startTime ?: return@mapNotNull null
        val endTime = point.endTime ?: startTime
        point.toRecord(metric = HealthMetricType.SLEEP_DURATION_MINUTES, value = duration.toMinutes().toDouble(), start = startTime.toEpochMilli(), end = endTime.toEpochMilli())
    }

private suspend fun readStepsAggregate(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> {
    val response = store.aggregateData(
        DataType.StepsType.TOTAL.requestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .build()
    )
    val aggregated = response.dataList.firstOrNull() ?: return emptyList()
    val value = aggregated.value ?: return emptyList()
    return listOf(
        HealthRecord(
            id = "samsung-steps-${request.startEpochMillis}-${request.endEpochMillis}",
            metric = HealthMetricType.STEPS,
            value = value.toDouble(),
            startEpochMillis = aggregated.startTime.toEpochMilli(),
            endEpochMillis = aggregated.endTime.toEpochMilli(),
            source = HealthDataSource.SAMSUNG_HEALTH,
            metadata = samsungMetadata()
        )
    )
}

private suspend fun readActiveEnergyAggregate(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> {
    val response = store.aggregateData(
        DataType.ActivitySummaryType.TOTAL_ACTIVE_CALORIES_BURNED.requestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .build()
    )
    val aggregated = response.dataList.firstOrNull() ?: return emptyList()
    val value = aggregated.value ?: return emptyList()
    return listOf(
        HealthRecord(
            id = "samsung-active-energy-${request.startEpochMillis}-${request.endEpochMillis}",
            metric = HealthMetricType.ACTIVE_ENERGY_KCAL,
            value = value.toDouble(),
            startEpochMillis = aggregated.startTime.toEpochMilli(),
            endEpochMillis = aggregated.endTime.toEpochMilli(),
            source = HealthDataSource.SAMSUNG_HEALTH,
            metadata = samsungMetadata()
        )
    )
}

private suspend fun readHeartRateRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.HEART_RATE.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val heartRate = point.getValue(DataType.HeartRateType.HEART_RATE) ?: return@mapNotNull null
        val time = point.endTime ?: point.startTime ?: return@mapNotNull null
        point.toRecord(metric = HealthMetricType.HEART_RATE_BPM, value = heartRate.toDouble(), start = time.toEpochMilli(), end = time.toEpochMilli())
    }

private suspend fun readBodyCompositionRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.BODY_COMPOSITION.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.flatMap { point ->
        val start = point.startTime?.toEpochMilli() ?: return@flatMap emptyList()
        val end = (point.endTime ?: point.startTime)?.toEpochMilli() ?: start
        buildList {
            addMetricIfRequested(request, point, DataType.BodyCompositionType.WEIGHT, HealthMetricType.BODY_WEIGHT_KG, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.HEIGHT, HealthMetricType.HEIGHT_CM, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.BODY_FAT, HealthMetricType.BODY_FAT_PERCENTAGE, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.BODY_FAT_MASS, HealthMetricType.BODY_FAT_MASS_KG, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.SKELETAL_MUSCLE_MASS, HealthMetricType.MUSCLE_MASS_KG, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.MUSCLE_MASS, HealthMetricType.MUSCLE_PERCENTAGE, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.SKELETAL_MUSCLE, HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.FAT_FREE, HealthMetricType.FAT_FREE_PERCENTAGE, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.FAT_FREE_MASS, HealthMetricType.FAT_FREE_MASS_KG, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.TOTAL_BODY_WATER, HealthMetricType.WATER_MASS_KG, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.BODY_MASS_INDEX, HealthMetricType.BODY_MASS_INDEX, start, end)
            addMetricIfRequested(request, point, DataType.BodyCompositionType.BASAL_METABOLIC_RATE, HealthMetricType.BASAL_METABOLIC_RATE_KCAL, start, end)
        }
    }

private suspend fun readBloodPressureRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.BLOOD_PRESSURE.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.flatMap { point ->
        val start = point.startTime?.toEpochMilli() ?: return@flatMap emptyList()
        val end = (point.endTime ?: point.startTime)?.toEpochMilli() ?: start
        buildList {
            addMetricIfRequested(request, point, DataType.BloodPressureType.SYSTOLIC, HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG, start, end)
            addMetricIfRequested(request, point, DataType.BloodPressureType.DIASTOLIC, HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG, start, end)
            addMetricIfRequested(request, point, DataType.BloodPressureType.MEAN, HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG, start, end)
            addMetricIfRequested(request, point, DataType.BloodPressureType.PULSE_RATE, HealthMetricType.PULSE_RATE_BPM, start, end)
        }
    }

private suspend fun readBloodGlucoseRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.BLOOD_GLUCOSE.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val value = point.getValue(DataType.BloodGlucoseType.GLUCOSE_LEVEL) ?: return@mapNotNull null
        val time = point.endTime ?: point.startTime ?: return@mapNotNull null
        point.toRecord(metric = HealthMetricType.BLOOD_GLUCOSE_MGDL, value = value.toDouble(), start = time.toEpochMilli(), end = time.toEpochMilli())
    }

private suspend fun readBloodOxygenRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.BLOOD_OXYGEN.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val value = point.getValue(DataType.BloodOxygenType.OXYGEN_SATURATION) ?: return@mapNotNull null
        val time = point.endTime ?: point.startTime ?: return@mapNotNull null
        point.toRecord(metric = HealthMetricType.OXYGEN_SATURATION_PERCENTAGE, value = value.toDouble(), start = time.toEpochMilli(), end = time.toEpochMilli())
    }

private suspend fun readBodyTemperatureRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.BODY_TEMPERATURE.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val value = point.getValue(DataType.BodyTemperatureType.BODY_TEMPERATURE) ?: return@mapNotNull null
        val time = point.endTime ?: point.startTime ?: return@mapNotNull null
        point.toRecord(metric = HealthMetricType.BODY_TEMPERATURE_CELSIUS, value = value.toDouble(), start = time.toEpochMilli(), end = time.toEpochMilli())
    }

private suspend fun readHydrationRecords(store: HealthDataStore, request: HealthReadRequest): List<HealthRecord> =
    store.readData(
        DataTypes.WATER_INTAKE.readDataRequestBuilder
            .setLocalTimeFilter(request.toLocalTimeFilter())
            .setOrdering(Ordering.DESC)
            .setLimit(request.limit)
            .build()
    ).dataList.mapNotNull { point ->
        val value = point.getValue(DataType.WaterIntakeType.AMOUNT) ?: return@mapNotNull null
        val time = point.endTime ?: point.startTime ?: return@mapNotNull null
        point.toRecord(metric = HealthMetricType.HYDRATION_ML, value = value.toDouble(), start = time.toEpochMilli(), end = time.toEpochMilli())
    }

internal fun <T : Number> MutableList<HealthRecord>.addMetricIfRequested(
    request: HealthReadRequest,
    point: HealthDataPoint,
    field: com.samsung.android.sdk.health.data.data.Field<T>,
    metric: HealthMetricType,
    start: Long,
    end: Long
) {
    if (metric !in request.metrics) return
    val value = point.getValue(field) ?: return
    add(point.toRecord(metric = metric, value = value.toDouble(), start = start, end = end))
}

internal fun HealthDataPoint.toRecord(metric: HealthMetricType, value: Double, start: Long, end: Long): HealthRecord = HealthRecord(
    id = "${uid ?: metric.metricId}-$start-${metric.metricId}",
    metric = metric,
    value = value,
    startEpochMillis = start,
    endEpochMillis = end,
    source = HealthDataSource.SAMSUNG_HEALTH,
    metadata = samsungMetadata()
)

internal fun samsungMetadata(): Map<String, String> = mapOf("provider" to "samsung_health")

internal fun HealthReadRequest.toLocalTimeFilter(): LocalTimeFilter = LocalTimeFilter.of(
    LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpochMillis), ZoneId.systemDefault()),
    LocalDateTime.ofInstant(Instant.ofEpochMilli(endEpochMillis), ZoneId.systemDefault())
)
