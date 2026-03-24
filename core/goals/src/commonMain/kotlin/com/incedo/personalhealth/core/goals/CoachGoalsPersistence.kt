package com.incedo.personalhealth.core.goals

internal fun encodeCoachGoals(goals: List<CoachGoal>): String = buildString {
    goals.forEach { goal ->
        appendLine(goal.title)
        appendLine(goal.cadence)
        appendLine(goal.focus)
    }
}

internal fun decodeCoachGoals(raw: String): List<CoachGoal> = raw
    .lineSequence()
    .toList()
    .chunked(3)
    .mapNotNull { chunk ->
        if (chunk.size < 3) null else CoachGoal(
            title = chunk[0],
            cadence = chunk[1],
            focus = chunk[2]
        )
    }
    .filter { it.title.isNotBlank() }
