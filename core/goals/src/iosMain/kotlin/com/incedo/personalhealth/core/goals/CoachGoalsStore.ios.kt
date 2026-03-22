package com.incedo.personalhealth.core.goals

import platform.Foundation.NSUserDefaults

private const val COACH_GOALS_KEY = "coach_goals"

actual object CoachGoalsStore {
    actual fun goals(): List<CoachGoal> = decodeCoachGoals(
        NSUserDefaults.standardUserDefaults.stringForKey(COACH_GOALS_KEY).orEmpty()
    )

    actual fun setGoals(goals: List<CoachGoal>) {
        NSUserDefaults.standardUserDefaults.setObject(
            encodeCoachGoals(goals),
            forKey = COACH_GOALS_KEY
        )
    }
}
