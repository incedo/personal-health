package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable

data class StepTimelinePoint(
    val label: String,
    val steps: Int
)

data class StepDetailStats(
    val totalSteps: Int,
    val peakHourLabel: String,
    val peakHourSteps: Int,
    val activeHours: Int
)

@Serializable
enum class QuickActivityType(val label: String) {
    RUNNING("Hardlopen"),
    WALKING("Wandelen"),
    CYCLING("Fietsen"),
    SWIMMING("Zwemmen"),
    FITNESS("Fitness"),
    NUTRITION("Nutrition"),
    OTHER("Anders")
}

data class QuickActivityEntry(
    val id: String,
    val type: QuickActivityType,
    val title: String,
    val createdAtEpochMillis: Long,
    val durationMillis: Long? = null
)

enum class HomeThemeMode(val label: String) {
    SYSTEM("Systeem"),
    DARK("Dark"),
    LIGHT("Light")
}

enum class HomeInsightTone {
    ACCENT,
    WARM,
    WARNING
}

fun estimateFitScore(stepCount: Int, heartRateBpm: Int): Int {
    val stepComponent = (stepCount / 100).coerceIn(0, 70)
    val heartRatePenalty = ((heartRateBpm - 58).coerceAtLeast(0) / 2).coerceIn(0, 20)
    val score = 30 + stepComponent - heartRatePenalty
    return score.coerceIn(0, 100)
}

fun formatSteps(stepCount: Int): String = stepCount.toString()
    .reversed()
    .chunked(3)
    .joinToString(".")
    .reversed()

fun fallbackStepTimeline(stepCount: Int): List<StepTimelinePoint> = listOf(
    StepTimelinePoint(
        label = "Nu",
        steps = stepCount.coerceAtLeast(0)
    )
)

fun summarizeStepTimeline(
    points: List<StepTimelinePoint>,
    pointsPerBucket: Int = 3
): List<StepTimelinePoint> {
    val safeBucketSize = pointsPerBucket.coerceAtLeast(1)
    return points
        .chunked(safeBucketSize)
        .map { chunk ->
            val startLabel = chunk.first().label
            val endLabel = chunk.last().label
            StepTimelinePoint(
                label = if (startLabel == endLabel) startLabel else "$startLabel-$endLabel",
                steps = chunk.sumOf { it.steps }
            )
        }
}

fun stepDetailStats(points: List<StepTimelinePoint>): StepDetailStats {
    val peakPoint = points.maxByOrNull { it.steps } ?: StepTimelinePoint(label = "Geen data", steps = 0)
    return StepDetailStats(
        totalSteps = points.sumOf { it.steps },
        peakHourLabel = peakPoint.label,
        peakHourSteps = peakPoint.steps,
        activeHours = points.count { it.steps > 0 }
    )
}

fun quickActivitySummary(entries: List<QuickActivityEntry>): String = when {
    entries.isEmpty() -> "Nog geen activiteiten gelogd"
    entries.size == 1 -> "1 activiteit gelogd"
    else -> "${entries.size} activiteiten gelogd"
}
