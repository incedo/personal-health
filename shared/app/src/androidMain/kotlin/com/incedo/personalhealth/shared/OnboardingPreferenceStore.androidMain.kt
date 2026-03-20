package com.incedo.personalhealth.shared

import java.io.File

actual object OnboardingPreferenceStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun isCompleted(): Boolean = runCatching {
        readState().completed
    }.getOrDefault(false)

    actual fun setCompleted(completed: Boolean) {
        runCatching {
            writeState(readState().copy(completed = completed))
        }
    }

    actual fun stepIndex(): Int = runCatching { readState().stepIndex }.getOrDefault(0)

    actual fun setStepIndex(stepIndex: Int) {
        runCatching {
            writeState(readState().copy(stepIndex = stepIndex.coerceAtLeast(0)))
        }
    }

    actual fun selectedGoalId(): String? = runCatching { readState().goalId }.getOrNull()

    actual fun setSelectedGoalId(goalId: String?) {
        runCatching {
            writeState(readState().copy(goalId = goalId))
        }
    }

    private data class OnboardingPreferenceState(
        val completed: Boolean = false,
        val stepIndex: Int = 0,
        val goalId: String? = null
    )

    private fun readState(): OnboardingPreferenceState {
        val lines = preferenceFile.takeIf { it.exists() }?.readLines().orEmpty()
        val completed = lines.getOrNull(0)?.trim()?.toBooleanStrictOrNull() ?: false
        val stepIndex = lines.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
        val goalId = lines.getOrNull(2)?.trim()?.takeIf { it.isNotBlank() }
        return OnboardingPreferenceState(
            completed = completed,
            stepIndex = stepIndex,
            goalId = goalId
        )
    }

    private fun writeState(state: OnboardingPreferenceState) {
        preferenceFile.parentFile?.mkdirs()
        preferenceFile.writeText(
            buildString {
                appendLine(state.completed)
                appendLine(state.stepIndex)
                append(state.goalId.orEmpty())
            }
        )
    }

    private fun resolvePreferenceFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()

        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/onboarding.pref")
    }
}
