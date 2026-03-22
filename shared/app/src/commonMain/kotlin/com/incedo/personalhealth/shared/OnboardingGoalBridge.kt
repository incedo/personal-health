package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.onboarding.OnboardingGoal

internal fun onboardingGoalFromId(goalId: String?): OnboardingGoal? {
    return goalId?.let { storedId -> OnboardingGoal.entries.firstOrNull { it.name == storedId } }
}

internal fun OnboardingGoal.toCoachFocusGoal(): CoachFocusGoal = when (this) {
    OnboardingGoal.Activity -> CoachFocusGoal.ACTIVITY
    OnboardingGoal.BetterSleep -> CoachFocusGoal.BETTER_SLEEP
    OnboardingGoal.Nutrition -> CoachFocusGoal.NUTRITION
}
