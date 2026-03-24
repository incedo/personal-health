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
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()
        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/coach-goals.pref")
    }
}
