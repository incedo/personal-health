package com.incedo.personalhealth.core.goals

import java.io.File

actual object CoachGoalsStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun goals(): List<CoachGoal> = runCatching {
        decodeCoachGoals(preferenceFile.takeIf { it.exists() }?.readText().orEmpty())
    }.getOrDefault(emptyList())

    actual fun setGoals(goals: List<CoachGoal>) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(encodeCoachGoals(goals))
        }
    }

    private fun resolvePreferenceFile(): File {
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/coach-goals.pref")
    }
}
