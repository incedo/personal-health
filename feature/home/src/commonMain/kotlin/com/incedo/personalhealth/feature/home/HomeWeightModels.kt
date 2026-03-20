package com.incedo.personalhealth.feature.home

import kotlin.math.roundToInt

data class WeightTimelinePoint(
    val label: String,
    val weightKg: Double?
)

data class WeightChartAxis(
    val yAxisLabels: List<String>,
    val minWeightKg: Double,
    val maxWeightKg: Double
)

data class WeightDetailStats(
    val latestWeightKg: Double?,
    val minWeightKg: Double,
    val maxWeightKg: Double,
    val measuredDays: Int,
    val changeKg: Double?
)

internal fun weightDetailStats(points: List<WeightTimelinePoint>): WeightDetailStats {
    val values = points.mapNotNull { it.weightKg }
    val latestWeightKg = values.lastOrNull()
    val firstWeightKg = values.firstOrNull()

    return WeightDetailStats(
        latestWeightKg = latestWeightKg,
        minWeightKg = values.minOrNull() ?: 0.0,
        maxWeightKg = values.maxOrNull() ?: 0.0,
        measuredDays = values.size,
        changeKg = if (latestWeightKg != null && firstWeightKg != null) {
            ((latestWeightKg - firstWeightKg) * 10).roundToInt() / 10.0
        } else {
            null
        }
    )
}

internal fun formatWeightKg(weightKg: Double?): String = weightKg?.let {
    "${((it * 10).roundToInt()) / 10.0} kg"
} ?: "Geen data"

internal fun weightChartAxis(points: List<WeightTimelinePoint>): WeightChartAxis {
    val values = points.mapNotNull { it.weightKg }
    if (values.isEmpty()) {
        return WeightChartAxis(
            yAxisLabels = listOf("0.0 kg", "0.0 kg", "0.0 kg", "0.0 kg"),
            minWeightKg = 0.0,
            maxWeightKg = 0.0
        )
    }

    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: minValue
    val padding = ((maxValue - minValue).coerceAtLeast(1.0)) * 0.12
    val axisMin = ((minValue - padding) * 10).roundToInt() / 10.0
    val axisMax = ((maxValue + padding) * 10).roundToInt() / 10.0
    val step = (axisMax - axisMin) / 3.0

    return WeightChartAxis(
        yAxisLabels = listOf(3, 2, 1, 0).map { index ->
            formatWeightKg(axisMin + step * index)
        },
        minWeightKg = axisMin,
        maxWeightKg = axisMax
    )
}
