package com.incedo.personalhealth.core.health

import kotlin.math.roundToInt

data class TodayHeartRateSnapshot(
    val latestHeartRateBpm: Int?,
    val averageHeartRateBpm: Int?,
    val buckets: List<TodayHeartRateBucket>
)

data class TodayHeartRateBucket(
    val label: String,
    val averageHeartRateBpm: Int
)

fun buildTodayHeartRateSnapshot(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long,
    bucketSizeHours: Int = 1
): TodayHeartRateSnapshot {
    require(dayStartEpochMillis <= dayEndEpochMillis) {
        "dayStartEpochMillis must be before or equal to dayEndEpochMillis"
    }
    val safeBucketHours = bucketSizeHours.coerceAtLeast(1)
    val bucketSizeMillis = safeBucketHours * 60L * 60L * 1000L
    val bucketCount = ((24L + safeBucketHours - 1L) / safeBucketHours).toInt()
    val bucketSamples = MutableList(bucketCount) { mutableListOf<Double>() }
    val heartRateRecords = records.filter { it.metric == HealthMetricType.HEART_RATE_BPM }

    heartRateRecords.forEach { record ->
        val effectiveEpochMillis = record.endEpochMillis
            .coerceAtLeast(dayStartEpochMillis)
            .coerceAtMost(dayEndEpochMillis)
        val bucketIndex = ((effectiveEpochMillis - dayStartEpochMillis) / bucketSizeMillis)
            .toInt()
            .coerceIn(0, bucketSamples.lastIndex)
        bucketSamples[bucketIndex] += record.value
    }

    return TodayHeartRateSnapshot(
        latestHeartRateBpm = heartRateRecords.maxByOrNull { it.endEpochMillis }?.value?.roundToInt(),
        averageHeartRateBpm = heartRateRecords.takeIf { it.isNotEmpty() }
            ?.let { samples -> (samples.sumOf { it.value } / samples.size).roundToInt() },
        buckets = bucketSamples.mapIndexed { index, samples ->
            val startHour = index * safeBucketHours
            TodayHeartRateBucket(
                label = startHour.toStepHourLabel(),
                averageHeartRateBpm = if (samples.isEmpty()) 0 else (samples.average()).roundToInt()
            )
        }
    )
}
