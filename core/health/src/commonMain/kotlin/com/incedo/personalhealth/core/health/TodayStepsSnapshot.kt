package com.incedo.personalhealth.core.health

data class TodayStepsSnapshot(
    val totalSteps: Int,
    val buckets: List<TodayStepBucket>
)

data class TodayStepBucket(
    val label: String,
    val steps: Int
)

fun buildTodayStepsSnapshot(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long,
    bucketSizeHours: Int = 1
): TodayStepsSnapshot {
    require(dayStartEpochMillis <= dayEndEpochMillis) {
        "dayStartEpochMillis must be before or equal to dayEndEpochMillis"
    }
    val safeBucketHours = bucketSizeHours.coerceAtLeast(1)
    val bucketSizeMillis = safeBucketHours * 60L * 60L * 1000L
    val bucketCount = ((24L + safeBucketHours - 1L) / safeBucketHours).toInt()
    val bucketValues = MutableList(bucketCount) { 0 }

    records.asSequence()
        .filter { it.metric == HealthMetricType.STEPS }
        .forEach { record ->
            val effectiveEpochMillis = record.endEpochMillis
                .coerceAtLeast(dayStartEpochMillis)
                .coerceAtMost(dayEndEpochMillis)
            val bucketIndex = ((effectiveEpochMillis - dayStartEpochMillis) / bucketSizeMillis)
                .toInt()
                .coerceIn(0, bucketValues.lastIndex)
            bucketValues[bucketIndex] += record.value.toInt()
        }

    return TodayStepsSnapshot(
        totalSteps = bucketValues.sum(),
        buckets = bucketValues.mapIndexed { index, steps ->
            val startHour = index * safeBucketHours
            TodayStepBucket(
                label = startHour.toStepHourLabel(),
                steps = steps
            )
        }
    )
}

private fun Int.toStepHourLabel(): String = buildString {
    append(toString().padStart(2, '0'))
    append(":00")
}
