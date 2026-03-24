package com.incedo.personalhealth.core.goals

internal fun encodeCoachIntake(profile: CoachIntakeProfile): String = buildString {
    appendLine(profile.focusGoal?.name.orEmpty())
    profile.traits.forEach { appendLine(it.name) }
}

internal fun decodeCoachIntake(raw: String): CoachIntakeProfile {
    val lines = raw.lineSequence().filter { it.isNotBlank() }.toList()
    val focus = lines.firstOrNull()?.let {
        runCatching { CoachFocusGoal.valueOf(it) }.getOrNull()
    }
    val traits = lines.drop(1).mapNotNull {
        runCatching { CoachProfileTrait.valueOf(it) }.getOrNull()
    }.toSet()
    return CoachIntakeProfile(focusGoal = focus, traits = traits)
}
