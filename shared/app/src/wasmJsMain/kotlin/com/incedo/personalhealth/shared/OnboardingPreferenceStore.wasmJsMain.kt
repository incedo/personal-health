package com.incedo.personalhealth.shared

private const val ONBOARDING_COMPLETED_KEY = "personal-health.onboarding.completed"
private const val ONBOARDING_STEP_INDEX_KEY = "personal-health.onboarding.step-index"
private const val ONBOARDING_GOAL_ID_KEY = "personal-health.onboarding.goal-id"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun localStorageRemoveItem(key: String)

actual object OnboardingPreferenceStore {
    actual fun isCompleted(): Boolean = localStorageGetItem(ONBOARDING_COMPLETED_KEY) == "true"

    actual fun setCompleted(completed: Boolean) {
        localStorageSetItem(ONBOARDING_COMPLETED_KEY, completed.toString())
    }

    actual fun stepIndex(): Int = localStorageGetItem(ONBOARDING_STEP_INDEX_KEY)?.toIntOrNull() ?: 0

    actual fun setStepIndex(stepIndex: Int) {
        localStorageSetItem(ONBOARDING_STEP_INDEX_KEY, stepIndex.coerceAtLeast(0).toString())
    }

    actual fun selectedGoalId(): String? = localStorageGetItem(ONBOARDING_GOAL_ID_KEY)?.takeIf { it.isNotBlank() }

    actual fun setSelectedGoalId(goalId: String?) {
        if (goalId.isNullOrBlank()) {
            localStorageRemoveItem(ONBOARDING_GOAL_ID_KEY)
        } else {
            localStorageSetItem(ONBOARDING_GOAL_ID_KEY, goalId)
        }
    }
}
