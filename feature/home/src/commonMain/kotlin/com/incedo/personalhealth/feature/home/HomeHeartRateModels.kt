package com.incedo.personalhealth.feature.home

data class HeartRateTimelinePoint(
    val label: String,
    val bpm: Int
)

data class HeartRateDetailStats(
    val averageBpm: Int,
    val minBpm: Int,
    val maxBpm: Int,
    val recoveryLabel: String
)

fun fallbackHeartRateTimeline(
    averageBpm: Int,
    sampleCount: Int = 0
): List<HeartRateTimelinePoint> {
    val labels = listOf("06:00", "09:00", "12:00", "15:00", "18:00", "Nu")
    val offsets = listOf(4, -2, 3, 1, 5, -1)
    val baseline = averageBpm.coerceIn(45, 120)
    val safeSampleCount = sampleCount.coerceAtLeast(0)

    return labels.mapIndexed { index, label ->
        val offset = offsets[(index + safeSampleCount) % offsets.size]
        HeartRateTimelinePoint(
            label = label,
            bpm = (baseline + offset).coerceIn(42, 165)
        )
    }
}

fun heartRateDetailStats(points: List<HeartRateTimelinePoint>): HeartRateDetailStats {
    val fallbackPoint = HeartRateTimelinePoint(label = "Nu", bpm = 0)
    val minPoint = points.minByOrNull { it.bpm } ?: fallbackPoint
    val maxPoint = points.maxByOrNull { it.bpm } ?: fallbackPoint
    val averageBpm = if (points.isEmpty()) 0 else points.sumOf { it.bpm } / points.size

    return HeartRateDetailStats(
        averageBpm = averageBpm,
        minBpm = minPoint.bpm,
        maxBpm = maxPoint.bpm,
        recoveryLabel = when {
            averageBpm == 0 -> "Geen data"
            averageBpm <= 60 -> "Sterk herstel"
            averageBpm <= 70 -> "Stabiel herstel"
            averageBpm <= 80 -> "Licht verhoogd"
            else -> "Herstel bewaken"
        }
    )
}
