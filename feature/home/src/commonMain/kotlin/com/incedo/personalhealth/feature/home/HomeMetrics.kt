package com.incedo.personalhealth.feature.home

data class StepTimelinePoint(
    val label: String,
    val steps: Int
)

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
