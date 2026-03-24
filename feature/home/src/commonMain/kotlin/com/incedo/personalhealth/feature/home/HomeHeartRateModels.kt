package com.incedo.personalhealth.feature.home

import com.incedo.personalhealth.core.designsystem.LineChartAxis

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

internal fun heartRateChartAxis(points: List<HeartRateTimelinePoint>): LineChartAxis {
    if (points.isEmpty()) {
        return LineChartAxis(
            yAxisLabels = listOf("0 bpm", "0 bpm", "0 bpm", "0 bpm"),
            minValue = 0.0,
            maxValue = 0.0
        )
    }
    val minValue = points.minOf { it.bpm }
    val maxValue = points.maxOf { it.bpm }
    val padding = ((maxValue - minValue).coerceAtLeast(6)) * 0.2
    val axisMin = (minValue - padding).coerceAtLeast(0.0)
    val axisMax = maxValue + padding
    val step = (axisMax - axisMin) / 3.0
    return LineChartAxis(
        yAxisLabels = listOf(3, 2, 1, 0).map { index ->
            "${(axisMin + step * index).toInt()} bpm"
        },
        minValue = axisMin,
        maxValue = axisMax
    )
}
