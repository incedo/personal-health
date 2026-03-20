package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthRecord
import com.incedo.personalhealth.core.health.buildTodayHeartRateSnapshot
import com.incedo.personalhealth.core.health.buildTodayStepsSnapshot
import kotlin.math.roundToInt

internal fun buildHealthSummaryItems(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long
): List<FrontendEvent.HealthSummaryItem> {
    val preferredStepRecords = preferredMetricRecords(records, HealthMetricType.STEPS)
    val preferredHeartRateRecords = preferredMetricRecords(records, HealthMetricType.HEART_RATE_BPM)
    val preferredSleepRecords = preferredMetricRecords(records, HealthMetricType.SLEEP_DURATION_MINUTES)
    val preferredEnergyRecords = preferredMetricRecords(records, HealthMetricType.ACTIVE_ENERGY_KCAL)
    val preferredWeightRecords = preferredMetricRecords(records, HealthMetricType.BODY_WEIGHT_KG)
    val preferredBodyFatRecords = preferredMetricRecords(records, HealthMetricType.BODY_FAT_PERCENTAGE)
    val preferredSystolicRecords = preferredMetricRecords(records, HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG)
    val preferredDiastolicRecords = preferredMetricRecords(records, HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG)

    val stepSnapshot = buildTodayStepsSnapshot(
        records = preferredStepRecords,
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    val heartSnapshot = buildTodayHeartRateSnapshot(
        records = preferredHeartRateRecords,
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    val sleepRecord = preferredSleepRecords.maxByOrNull { it.endEpochMillis }
    val caloriesTotal = preferredEnergyRecords.sumOf { it.value }
    val latestWeight = preferredWeightRecords.maxByOrNull { it.endEpochMillis }
    val latestBodyFat = preferredBodyFatRecords.maxByOrNull { it.endEpochMillis }
    val latestSystolic = preferredSystolicRecords.maxByOrNull { it.endEpochMillis }
    val latestDiastolic = preferredDiastolicRecords.maxByOrNull { it.endEpochMillis }

    return listOf(
        FrontendEvent.HealthSummaryItem(
            metricId = "steps",
            title = "Stappen",
            value = "${stepSnapshot.totalSteps}",
            detail = "Vandaag totaal",
            progress = (stepSnapshot.totalSteps / 10_000f).coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.STEPS)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "heart_rate",
            title = "Hartslag",
            value = heartSnapshot.latestHeartRateBpm?.let { "$it bpm" } ?: "Geen data",
            detail = heartSnapshot.averageHeartRateBpm?.let { "Gemiddeld $it bpm vandaag" } ?: "Laatste meting ontbreekt",
            progress = ((heartSnapshot.latestHeartRateBpm ?: 0) - 40).div(80f).coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.HEART_RATE_BPM)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "sleep",
            title = "Slaap",
            value = sleepRecord?.value?.roundToInt()?.let { "$it min" } ?: "Geen data",
            detail = if (sleepRecord == null) "Laatste sessie ontbreekt" else "Laatste slaapsessie",
            progress = ((sleepRecord?.value ?: 0.0) / 480.0).toFloat().coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.SLEEP_DURATION_MINUTES)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "active_energy",
            title = "Actieve energie",
            value = "${caloriesTotal.roundToInt()} kcal",
            detail = "Vandaag verbrand",
            progress = (caloriesTotal / 600.0).toFloat().coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.ACTIVE_ENERGY_KCAL)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "body_weight",
            title = "Gewicht",
            value = latestWeight?.value?.let { "${(it * 10).roundToInt() / 10.0} kg" } ?: "Geen data",
            detail = if (latestWeight == null) "Laatste meting ontbreekt" else "Laatste meting",
            progress = (((latestWeight?.value ?: 0.0) - 40.0) / 100.0).toFloat().coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.BODY_WEIGHT_KG)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "body_composition",
            title = "Body composition",
            value = latestBodyFat?.value?.roundToInt()?.let { "$it %" } ?: "Geen data",
            detail = if (latestBodyFat == null) "Laatste body composition ontbreekt" else "Laatste vetpercentage",
            progress = ((latestBodyFat?.value ?: 0.0) / 50.0).toFloat().coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.BODY_FAT_PERCENTAGE)
        ),
        FrontendEvent.HealthSummaryItem(
            metricId = "blood_pressure",
            title = "Bloeddruk",
            value = if (latestSystolic == null || latestDiastolic == null) {
                "Geen data"
            } else {
                "${latestSystolic.value.roundToInt()}/${latestDiastolic.value.roundToInt()} mmHg"
            },
            detail = if (latestSystolic == null || latestDiastolic == null) {
                "Laatste bloeddruk ontbreekt"
            } else {
                "Laatste meting"
            },
            progress = (((latestSystolic?.value ?: 0.0) - 80.0) / 100.0).toFloat().coerceIn(0f, 1f),
            sourceSummary = healthSourceSummary(records, HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG)
        )
    )
}
