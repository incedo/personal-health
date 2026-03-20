package com.incedo.personalhealth.integration.samsunghealth

import android.app.Activity
import android.content.Context
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class SamsungHealthGateway(
    private val context: Context
) : HealthDataGateway {
    private val store by lazy { HealthDataService.getStore(context) }

    fun availability(): SamsungHealthSdkAvailability = currentSamsungHealthAvailability(context)

    fun requiredReadPermissions(): Set<Permission> = setOf(
        Permission.of(DataTypes.SLEEP, AccessType.READ),
        Permission.of(DataTypes.ACTIVITY_SUMMARY, AccessType.READ),
        Permission.of(DataTypes.BODY_COMPOSITION, AccessType.READ),
        Permission.of(DataTypes.BLOOD_PRESSURE, AccessType.READ)
    )

    suspend fun hasRequiredPermissions(): Boolean {
        val required = requiredReadPermissions()
        return store.getGrantedPermissions(required).containsAll(required)
    }

    suspend fun requestReadPermissions(activity: Activity): Boolean {
        val required = requiredReadPermissions()
        return store.requestPermissions(required, activity).containsAll(required)
    }

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        if (!availability().isReady || !hasRequiredPermissions()) return emptyList()

        return buildList {
            if (HealthMetricType.SLEEP_DURATION_MINUTES in request.metrics) {
                addAll(readSleepRecords(request))
            }
            if (HealthMetricType.ACTIVE_ENERGY_KCAL in request.metrics) {
                readActiveEnergyRecord(request)?.let(::add)
            }
            if (HealthMetricType.BODY_WEIGHT_KG in request.metrics) {
                addAll(readBodyCompositionRecords(request))
            } else if (request.metrics.any { it in BODY_COMPOSITION_METRICS }) {
                addAll(readBodyCompositionRecords(request))
            }
            if (request.metrics.any { it in BLOOD_PRESSURE_METRICS }) {
                addAll(readBloodPressureRecords(request))
            }
        }.sortedByDescending { it.endEpochMillis }.take(request.limit)
    }

    private suspend fun readSleepRecords(request: HealthReadRequest): List<HealthRecord> {
        val response = store.readData(
            DataTypes.SLEEP.readDataRequestBuilder
                .setLocalTimeFilter(request.toLocalTimeFilter())
                .setOrdering(Ordering.DESC)
                .setLimit(request.limit)
                .build()
        )

        return response.dataList.mapNotNull { point ->
            val duration = point.getValue(DataType.SleepType.DURATION) ?: return@mapNotNull null
            val startTime = point.startTime ?: return@mapNotNull null
            val endTime = point.endTime ?: startTime
            HealthRecord(
                id = point.uid ?: "sleep-${startTime.toEpochMilli()}",
                metric = HealthMetricType.SLEEP_DURATION_MINUTES,
                value = duration.toMinutes().toDouble(),
                unit = "min",
                startEpochMillis = startTime.toEpochMilli(),
                endEpochMillis = endTime.toEpochMilli(),
                source = HealthDataSource.SAMSUNG_HEALTH,
                metadata = mapOf("provider" to "samsung_health")
            )
        }
    }

    private suspend fun readActiveEnergyRecord(request: HealthReadRequest): HealthRecord? {
        val response = store.aggregateData(
            DataType.ActivitySummaryType.TOTAL_ACTIVE_CALORIES_BURNED.requestBuilder
                .setLocalTimeFilter(request.toLocalTimeFilter())
                .build()
        )
        val aggregated = response.dataList.firstOrNull() ?: return null
        val value = aggregated.value ?: return null
        return HealthRecord(
            id = "samsung-active-energy-${request.startEpochMillis}-${request.endEpochMillis}",
            metric = HealthMetricType.ACTIVE_ENERGY_KCAL,
            value = value.toDouble(),
            unit = "kcal",
            startEpochMillis = aggregated.startTime.toEpochMilli(),
            endEpochMillis = aggregated.endTime.toEpochMilli(),
            source = HealthDataSource.SAMSUNG_HEALTH,
            metadata = mapOf("provider" to "samsung_health")
        )
    }

    private suspend fun readBodyCompositionRecords(request: HealthReadRequest): List<HealthRecord> {
        val response = store.readData(
            DataTypes.BODY_COMPOSITION.readDataRequestBuilder
                .setLocalTimeFilter(request.toLocalTimeFilter())
                .setOrdering(Ordering.DESC)
                .setLimit(request.limit)
                .build()
        )

        return response.dataList.flatMap { point ->
            val startTime = point.startTime ?: return@flatMap emptyList()
            val endTime = point.endTime ?: startTime
            buildList {
                point.getValue(DataType.BodyCompositionType.WEIGHT)?.let { weight ->
                    add(
                        point.toHealthRecord(
                            suffix = "weight",
                            metric = HealthMetricType.BODY_WEIGHT_KG,
                            value = weight.toDouble(),
                            unit = "kg",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
                point.getValue(DataType.BodyCompositionType.BODY_FAT)?.let { bodyFat ->
                    add(
                        point.toHealthRecord(
                            suffix = "body-fat",
                            metric = HealthMetricType.BODY_FAT_PERCENTAGE,
                            value = bodyFat.toDouble(),
                            unit = "%",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
                point.getValue(DataType.BodyCompositionType.MUSCLE_MASS)?.let { muscleMass ->
                    add(
                        point.toHealthRecord(
                            suffix = "muscle-mass",
                            metric = HealthMetricType.MUSCLE_MASS_KG,
                            value = muscleMass.toDouble(),
                            unit = "kg",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
                point.getValue(DataType.BodyCompositionType.TOTAL_BODY_WATER)?.let { water ->
                    add(
                        point.toHealthRecord(
                            suffix = "total-body-water",
                            metric = HealthMetricType.WATER_PERCENTAGE,
                            value = water.toDouble(),
                            unit = "kg",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
            }
        }
    }

    private suspend fun readBloodPressureRecords(request: HealthReadRequest): List<HealthRecord> {
        val response = store.readData(
            DataTypes.BLOOD_PRESSURE.readDataRequestBuilder
                .setLocalTimeFilter(request.toLocalTimeFilter())
                .setOrdering(Ordering.DESC)
                .setLimit(request.limit)
                .build()
        )

        return response.dataList.flatMap { point ->
            val startTime = point.startTime ?: return@flatMap emptyList()
            val endTime = point.endTime ?: startTime
            buildList {
                point.getValue(DataType.BloodPressureType.SYSTOLIC)?.let { systolic ->
                    add(
                        point.toHealthRecord(
                            suffix = "systolic",
                            metric = HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
                            value = systolic.toDouble(),
                            unit = "mmHg",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
                point.getValue(DataType.BloodPressureType.DIASTOLIC)?.let { diastolic ->
                    add(
                        point.toHealthRecord(
                            suffix = "diastolic",
                            metric = HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
                            value = diastolic.toDouble(),
                            unit = "mmHg",
                            startEpochMillis = startTime.toEpochMilli(),
                            endEpochMillis = endTime.toEpochMilli()
                        )
                    )
                }
            }
        }
    }
}

private val BODY_COMPOSITION_METRICS = setOf(
    HealthMetricType.BODY_WEIGHT_KG,
    HealthMetricType.BODY_FAT_PERCENTAGE,
    HealthMetricType.MUSCLE_MASS_KG,
    HealthMetricType.WATER_PERCENTAGE
)

private val BLOOD_PRESSURE_METRICS = setOf(
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG
)

private fun com.samsung.android.sdk.health.data.data.HealthDataPoint.toHealthRecord(
    suffix: String,
    metric: HealthMetricType,
    value: Double,
    unit: String,
    startEpochMillis: Long,
    endEpochMillis: Long
): HealthRecord = HealthRecord(
    id = "${uid ?: metric.name.lowercase()}-$suffix-$startEpochMillis",
    metric = metric,
    value = value,
    unit = unit,
    startEpochMillis = startEpochMillis,
    endEpochMillis = endEpochMillis,
    source = HealthDataSource.SAMSUNG_HEALTH,
    metadata = mapOf("provider" to "samsung_health")
)

private fun HealthReadRequest.toLocalTimeFilter(): LocalTimeFilter = LocalTimeFilter.of(
    LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpochMillis), ZoneId.systemDefault()),
    LocalDateTime.ofInstant(Instant.ofEpochMilli(endEpochMillis), ZoneId.systemDefault())
)
