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

data class VitalityInsight(
    val title: String,
    val description: String,
    val tone: HomeInsightTone
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

fun buildVitalityInsights(
    fitScore: Int,
    heartRateBpm: Int,
    steps: Int,
    activityMinutes: Int
): List<VitalityInsight> {
    val recoveryInsight = when {
        fitScore >= 80 && heartRateBpm <= 64 -> VitalityInsight(
            title = "Herstel sterk",
            description = "Je basis is stabiel. Een geplande workout past goed in je dag.",
            tone = HomeInsightTone.ACCENT
        )

        heartRateBpm >= 75 -> VitalityInsight(
            title = "Herstel bewaken",
            description = "Je hartslag ligt hoger dan ideaal. Kies vandaag liever voor licht werk of extra rust.",
            tone = HomeInsightTone.WARNING
        )

        else -> VitalityInsight(
            title = "Herstel in balans",
            description = "Je dagstart is stabiel. Bouw rustig op richting je hoofdactiviteit.",
            tone = HomeInsightTone.ACCENT
        )
    }

    val movementInsight = when {
        activityMinutes >= 45 -> VitalityInsight(
            title = "Activiteit op koers",
            description = "Je actieve minuten lopen goed. Gebruik je stappen nu vooral om je ritme vast te houden.",
            tone = HomeInsightTone.ACCENT
        )

        activityMinutes >= 20 || steps >= 4_000 -> VitalityInsight(
            title = "Activiteit bouwt op",
            description = "Je bent al begonnen. Nog een kort actief blok later vandaag tilt je ring zichtbaar verder op.",
            tone = HomeInsightTone.WARM
        )

        else -> VitalityInsight(
            title = "Meer actieve tijd nodig",
            description = "Start een wandeling, run of gymsessie zodat je actieve minutenring vandaag niet achterblijft.",
            tone = HomeInsightTone.WARM
        )
    }

    val nutritionInsight = if (fitScore >= 75) {
        VitalityInsight(
            title = "Voeding klaarzetten",
            description = "Log nutrition en mik op eiwit plus langzame koolhydraten om herstel en training te ondersteunen.",
            tone = HomeInsightTone.WARM
        )
    } else {
        VitalityInsight(
            title = "Brandstof aanvullen",
            description = "Log nutrition en voeg een eiwitrijke snack of lunch toe om je dag sneller te stabiliseren.",
            tone = HomeInsightTone.WARNING
        )
    }

    return listOf(recoveryInsight, movementInsight, nutritionInsight)
}
