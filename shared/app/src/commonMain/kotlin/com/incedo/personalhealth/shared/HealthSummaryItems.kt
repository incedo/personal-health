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
    val availableMetrics = HealthMetricType.entries.filter { metric ->
        preferredMetricRecords(records, metric).isNotEmpty()
    }

    return availableMetrics.mapNotNull { metric ->
        buildSummaryItem(
            metric = metric,
            records = records,
            dayStartEpochMillis = dayStartEpochMillis,
            dayEndEpochMillis = dayEndEpochMillis
        )
    }
}

private fun buildSummaryItem(
    metric: HealthMetricType,
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long
): FrontendEvent.HealthSummaryItem? {
    val preferredRecords = preferredMetricRecords(records, metric)
    if (preferredRecords.isEmpty()) return null

    val presentation = when (metric) {
        HealthMetricType.STEPS -> buildStepsPresentation(records, dayStartEpochMillis, dayEndEpochMillis)
        HealthMetricType.HEART_RATE_BPM -> buildHeartRatePresentation(records, dayStartEpochMillis, dayEndEpochMillis)
        HealthMetricType.SLEEP_DURATION_MINUTES -> buildSleepPresentation(preferredRecords)
        HealthMetricType.ACTIVE_ENERGY_KCAL -> buildEnergyPresentation(preferredRecords)
        in aggregateMetrics -> buildAggregatePresentation(metric, preferredRecords)
        else -> buildLatestValuePresentation(metric, preferredRecords)
    }

    return FrontendEvent.HealthSummaryItem(
        metricKey = metric.key,
        metricId = metric.metricId,
        domainId = metric.dataType.domain.name,
        title = metric.title,
        value = presentation.value,
        detail = presentation.detail,
        progress = presentation.progress,
        sourceSummary = healthSourceSummary(records, metric)
    )
}

private fun buildStepsPresentation(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long
): SummaryPresentation {
    val snapshot = buildTodayStepsSnapshot(
        records = preferredMetricRecords(records, HealthMetricType.STEPS),
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    return SummaryPresentation(
        value = "${snapshot.totalSteps}",
        detail = "Vandaag totaal",
        progress = (snapshot.totalSteps / 10_000f).coerceIn(0f, 1f)
    )
}

private fun buildHeartRatePresentation(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long
): SummaryPresentation {
    val snapshot = buildTodayHeartRateSnapshot(
        records = preferredMetricRecords(records, HealthMetricType.HEART_RATE_BPM),
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    return SummaryPresentation(
        value = snapshot.latestHeartRateBpm?.let { "$it bpm" } ?: "Geen data",
        detail = snapshot.averageHeartRateBpm?.let { "Gemiddeld $it bpm vandaag" } ?: "Laatste meting ontbreekt",
        progress = ((snapshot.latestHeartRateBpm ?: 0) - 40).div(80f).coerceIn(0f, 1f)
    )
}

private fun buildSleepPresentation(records: List<HealthRecord>): SummaryPresentation {
    val latestRecord = records.maxByOrNull { it.endEpochMillis }
    return SummaryPresentation(
        value = latestRecord?.value?.roundToInt()?.let { "$it min" } ?: "Geen data",
        detail = if (latestRecord == null) "Laatste slaapsessie ontbreekt" else "Laatste slaapsessie",
        progress = ((latestRecord?.value ?: 0.0) / 480.0).toFloat().coerceIn(0f, 1f)
    )
}

private fun buildEnergyPresentation(records: List<HealthRecord>): SummaryPresentation {
    val total = records.sumOf { it.value }
    return SummaryPresentation(
        value = "${total.roundToInt()} kcal",
        detail = "Binnen venster totaal",
        progress = (total / 600.0).toFloat().coerceIn(0f, 1f)
    )
}

private fun buildAggregatePresentation(
    metric: HealthMetricType,
    records: List<HealthRecord>
): SummaryPresentation {
    val total = records.sumOf { it.value }
    return SummaryPresentation(
        value = formatMetricValue(metric, total),
        detail = "Binnen venster totaal",
        progress = estimateProgress(metric, total)
    )
}

private fun buildLatestValuePresentation(
    metric: HealthMetricType,
    records: List<HealthRecord>
): SummaryPresentation {
    val latestRecord = records.maxByOrNull { it.endEpochMillis }
    val value = latestRecord?.let { formatMetricValue(metric, it.value) } ?: "Geen data"
    return SummaryPresentation(
        value = value,
        detail = if (latestRecord == null) {
            "Laatste meting ontbreekt"
        } else {
            "Laatste meting"
        },
        progress = latestRecord?.let { estimateProgress(metric, it.value) } ?: 0f
    )
}

private fun formatMetricValue(metric: HealthMetricType, value: Double): String = when (metric.unit) {
    "count" -> value.roundToInt().toString()
    "bpm", "min", "kcal", "mL", "mg/dL", "mmHg", "mg", "mcg" -> "${value.roundToInt()} ${metric.unit}"
    "%" -> "${value.roundToInt()} %"
    "g" -> "${((value * 10).roundToInt()) / 10.0} g"
    "kg" -> "${((value * 10).roundToInt()) / 10.0} kg"
    "cm" -> "${value.roundToInt()} cm"
    "C" -> "${((value * 10).roundToInt()) / 10.0} C"
    "kg/m2" -> "${((value * 10).roundToInt()) / 10.0}"
    else -> "${value.roundToInt()} ${metric.unit}"
}

private fun estimateProgress(metric: HealthMetricType, value: Double): Float = when (metric) {
    HealthMetricType.BODY_WEIGHT_KG -> (((value - 40.0) / 100.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.BODY_FAT_PERCENTAGE -> (value / 50.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.BODY_FAT_MASS_KG -> (value / 50.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.MUSCLE_MASS_KG, HealthMetricType.FAT_FREE_MASS_KG -> (value / 100.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.MUSCLE_PERCENTAGE, HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE, HealthMetricType.FAT_FREE_PERCENTAGE -> (value / 100.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG -> (((value - 80.0) / 100.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG -> (((value - 50.0) / 70.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG -> (((value - 60.0) / 80.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.PULSE_RATE_BPM -> (((value - 40.0) / 100.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.BLOOD_GLUCOSE_MGDL -> (value / 180.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.OXYGEN_SATURATION_PERCENTAGE -> (value / 100.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.BODY_TEMPERATURE_CELSIUS -> (((value - 34.0) / 6.0).toFloat()).coerceIn(0f, 1f)
    HealthMetricType.HYDRATION_ML -> (value / 2_500.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.DIETARY_ENERGY_KCAL -> (value / 2_500.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.PROTEIN_G -> (value / 150.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.CARBOHYDRATE_G -> (value / 350.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.TOTAL_FAT_G -> (value / 120.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.SATURATED_FAT_G -> (value / 40.0).toFloat().coerceIn(0f, 1f)
    HealthMetricType.POLYUNSATURATED_FAT_G,
    HealthMetricType.MONOUNSATURATED_FAT_G,
    HealthMetricType.TRANS_FAT_G,
    HealthMetricType.DIETARY_FIBER_G,
    HealthMetricType.SUGAR_G,
    HealthMetricType.CHOLESTEROL_MG,
    HealthMetricType.SODIUM_MG,
    HealthMetricType.POTASSIUM_MG,
    HealthMetricType.CALCIUM_MG,
    HealthMetricType.IRON_MG,
    HealthMetricType.VITAMIN_A_MCG,
    HealthMetricType.VITAMIN_C_MG -> 0f
    else -> 0f
}

private val aggregateMetrics = setOf(
    HealthMetricType.HYDRATION_ML,
    HealthMetricType.DIETARY_ENERGY_KCAL,
    HealthMetricType.PROTEIN_G,
    HealthMetricType.CARBOHYDRATE_G,
    HealthMetricType.TOTAL_FAT_G,
    HealthMetricType.SATURATED_FAT_G,
    HealthMetricType.POLYUNSATURATED_FAT_G,
    HealthMetricType.MONOUNSATURATED_FAT_G,
    HealthMetricType.TRANS_FAT_G,
    HealthMetricType.DIETARY_FIBER_G,
    HealthMetricType.SUGAR_G,
    HealthMetricType.CHOLESTEROL_MG,
    HealthMetricType.SODIUM_MG,
    HealthMetricType.POTASSIUM_MG,
    HealthMetricType.CALCIUM_MG,
    HealthMetricType.IRON_MG,
    HealthMetricType.VITAMIN_A_MCG,
    HealthMetricType.VITAMIN_C_MG
)

private data class SummaryPresentation(
    val value: String,
    val detail: String,
    val progress: Float
)
