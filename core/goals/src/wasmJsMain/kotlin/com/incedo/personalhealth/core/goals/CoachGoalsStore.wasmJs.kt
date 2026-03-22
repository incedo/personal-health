package com.incedo.personalhealth.core.goals

private const val COACH_GOALS_KEY = "personal-health.coach-goals"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

actual object CoachGoalsStore {
    actual fun goals(): List<CoachGoal> = decodeCoachGoals(
        localStorageGetItem(COACH_GOALS_KEY).orEmpty()
    )

    actual fun setGoals(goals: List<CoachGoal>) {
        localStorageSetItem(COACH_GOALS_KEY, encodeCoachGoals(goals))
    }
}
