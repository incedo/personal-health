package com.incedo.personalhealth.feature.home

data class StepTimelinePoint(
    val label: String,
    val steps: Int
)

enum class QuickActivityType(val label: String) {
    RUNNING("Hardlopen"),
    WALKING("Wandelen"),
    CYCLING("Fietsen"),
    SWIMMING("Zwemmen"),
    FITNESS("Fitness"),
    OTHER("Anders")
}

data class QuickActivityEntry(
    val id: String,
    val type: QuickActivityType,
    val title: String
)

enum class HomeThemeMode(val label: String) {
    SYSTEM("Systeem"),
    DARK("Dark"),
    LIGHT("Light")
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

fun logQuickActivity(
    entries: List<QuickActivityEntry>,
    type: QuickActivityType
): List<QuickActivityEntry> {
    val nextOrdinal = (entries.size + 1).toString().padStart(2, '0')
    val nextEntry = QuickActivityEntry(
        id = "${type.name.lowercase()}-$nextOrdinal",
        type = type,
        title = "${type.label} sessie"
    )
    return listOf(nextEntry) + entries
}

fun quickActivitySummary(entries: List<QuickActivityEntry>): String = when {
    entries.isEmpty() -> "Nog geen activiteiten gelogd"
    entries.size == 1 -> "1 activiteit gelogd"
    else -> "${entries.size} activiteiten gelogd"
}
