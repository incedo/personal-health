package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthRecord
import com.incedo.personalhealth.feature.home.HomeWeightChartCatalog
import com.incedo.personalhealth.feature.home.HomeWeightRange
import com.incedo.personalhealth.feature.home.HomeWeightTimeline
import com.incedo.personalhealth.feature.home.WeightTimelinePoint
import kotlin.math.ceil

private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
private const val WEEK_MILLIS = 7L * DAY_MILLIS
private const val MONTH_MILLIS = 30L * DAY_MILLIS
private const val SEMESTER_MILLIS = 182L * DAY_MILLIS

internal fun buildWeightChartCatalog(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long
): HomeWeightChartCatalog {
    val weightRecords = preferredMetricRecords(records, HealthMetricType.BODY_WEIGHT_KG)
        .sortedBy { it.endEpochMillis }

    return HomeWeightChartCatalog(
        timelines = HomeWeightRange.entries.associateWith { range ->
            when (range) {
                HomeWeightRange.WEEK -> buildWeeklyMeasurementTimeline(weightRecords, dayEndEpochMillis, range)
                HomeWeightRange.MONTH -> buildRollingDailyTimeline(weightRecords, dayStartEpochMillis, 30, range)
                HomeWeightRange.QUARTER -> buildAveragedTimeline(weightRecords, dayEndEpochMillis, 13, WEEK_MILLIS, range, "Weekgemiddelde")
                HomeWeightRange.SEMESTER -> buildMonthlyMeasurementTimeline(weightRecords, dayEndEpochMillis, 6, range)
                HomeWeightRange.YEAR -> buildAveragedTimeline(weightRecords, dayEndEpochMillis, 12, MONTH_MILLIS, range, "Maandgemiddelde")
                HomeWeightRange.ALL -> buildAllTimeTimeline(weightRecords, dayEndEpochMillis, range)
            }
        }
    )
}

private fun buildWeeklyMeasurementTimeline(
    records: List<HealthRecord>,
    dayEndEpochMillis: Long,
    range: HomeWeightRange
): HomeWeightTimeline {
    val startEpochMillis = dayEndEpochMillis - WEEK_MILLIS
    val points = records
        .filter { it.endEpochMillis >= startEpochMillis }
        .map { record ->
            WeightTimelinePoint(
                label = relativeDayLabel(record.endEpochMillis, dayEndEpochMillis),
                weightKg = record.value
            )
        }
    return HomeWeightTimeline(range = range, title = "Alle metingen van deze week", points = points)
}

private fun buildRollingDailyTimeline(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayCount: Int,
    range: HomeWeightRange
): HomeWeightTimeline {
    val startEpochMillis = dayStartEpochMillis - (dayCount - 1L) * DAY_MILLIS
    val points = (0 until dayCount).map { index ->
        val bucketStart = startEpochMillis + index * DAY_MILLIS
        val bucketEnd = bucketStart + DAY_MILLIS
        val latestRecord = records
            .filter { it.endEpochMillis in bucketStart until bucketEnd }
            .maxByOrNull { it.endEpochMillis }
        WeightTimelinePoint(
            label = if (index == dayCount - 1) "Vandaag" else "D-${dayCount - 1 - index}",
            weightKg = latestRecord?.value
        )
    }
    return HomeWeightTimeline(range = range, title = "Laatste $dayCount dagen", points = points)
}

private fun buildAveragedTimeline(
    records: List<HealthRecord>,
    dayEndEpochMillis: Long,
    bucketCount: Int,
    bucketMillis: Long,
    range: HomeWeightRange,
    titlePrefix: String
): HomeWeightTimeline {
    val alignedEnd = dayEndEpochMillis + DAY_MILLIS
    val startEpochMillis = alignedEnd - bucketCount * bucketMillis
    val points = (0 until bucketCount).map { index ->
        val bucketStart = startEpochMillis + index * bucketMillis
        val bucketEnd = bucketStart + bucketMillis
        val bucketRecords = records
            .filter { it.endEpochMillis in bucketStart until bucketEnd }
        val average = bucketRecords.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
        WeightTimelinePoint(
            label = "${index + 1}",
            weightKg = average
        )
    }
    return HomeWeightTimeline(
        range = range,
        title = "$titlePrefix over ${points.size} blokken",
        points = points
    )
}

private fun buildMonthlyMeasurementTimeline(
    records: List<HealthRecord>,
    dayEndEpochMillis: Long,
    monthCount: Int,
    range: HomeWeightRange
): HomeWeightTimeline {
    val alignedEnd = dayEndEpochMillis + DAY_MILLIS
    val startEpochMillis = alignedEnd - monthCount * MONTH_MILLIS
    val points = (0 until monthCount).map { index ->
        val bucketStart = startEpochMillis + index * MONTH_MILLIS
        val bucketEnd = bucketStart + MONTH_MILLIS
        val latestRecord = records
            .filter { it.endEpochMillis in bucketStart until bucketEnd }
            .maxByOrNull { it.endEpochMillis }
        WeightTimelinePoint(
            label = "M${index + 1}",
            weightKg = latestRecord?.value
        )
    }
    return HomeWeightTimeline(range = range, title = "Laatste 6 maanden", points = points)
}

private fun buildAllTimeTimeline(
    records: List<HealthRecord>,
    dayEndEpochMillis: Long,
    range: HomeWeightRange
): HomeWeightTimeline {
    if (records.isEmpty()) {
        return HomeWeightTimeline(range = range, title = "Alle data", points = emptyList())
    }

    val firstEpochMillis = records.first().endEpochMillis
    val totalDays = ((dayEndEpochMillis - firstEpochMillis) / DAY_MILLIS).coerceAtLeast(1L)
    return if (totalDays >= 365L) {
        buildSemesterAverageTimeline(records, firstEpochMillis, dayEndEpochMillis, range)
    } else {
        buildAveragedTimeline(records, dayEndEpochMillis, 12, MONTH_MILLIS, range, "Maandgemiddelde")
            .copy(title = "Alle data")
    }
}

private fun buildSemesterAverageTimeline(
    records: List<HealthRecord>,
    firstEpochMillis: Long,
    dayEndEpochMillis: Long,
    range: HomeWeightRange
): HomeWeightTimeline {
    val totalSemesters = ceil(((dayEndEpochMillis - firstEpochMillis).coerceAtLeast(SEMESTER_MILLIS)) / SEMESTER_MILLIS.toDouble()).toInt()
    val points = (0 until totalSemesters).map { index ->
        val bucketStart = firstEpochMillis + index * SEMESTER_MILLIS
        val bucketEnd = bucketStart + SEMESTER_MILLIS
        val semesterRecords = records.filter { it.endEpochMillis in bucketStart until bucketEnd }
        val average = semesterRecords.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
        WeightTimelinePoint(
            label = "S${index + 1}",
            weightKg = average
        )
    }
    return HomeWeightTimeline(range = range, title = "Alle data per semester", points = points)
}

private fun relativeDayLabel(
    epochMillis: Long,
    dayEndEpochMillis: Long
): String {
    val daysAgo = ((dayEndEpochMillis - epochMillis) / DAY_MILLIS).toInt().coerceAtLeast(0)
    return if (daysAgo == 0) "Vandaag" else "D-$daysAgo"
}
