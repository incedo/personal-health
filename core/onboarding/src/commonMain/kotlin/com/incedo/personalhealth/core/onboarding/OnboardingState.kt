package com.incedo.personalhealth.core.onboarding

import kotlin.math.max
import kotlin.math.min

enum class OnboardingLayoutClass {
    Compact,
    Medium,
    Expanded
}

enum class OnboardingGoal {
    Activity,
    BetterSleep,
    Nutrition
}

data class OnboardingStep(
    val title: String,
    val description: String
)

data class OnboardingUiState(
    val stepIndex: Int = 0,
    val selectedGoal: OnboardingGoal? = null,
    val completed: Boolean = false
)

sealed interface OnboardingEvent {
    data object Next : OnboardingEvent
    data object Back : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Finish : OnboardingEvent
    data class GoalSelected(val goal: OnboardingGoal) : OnboardingEvent
}

val onboardingSteps: List<OnboardingStep> = listOf(
    OnboardingStep(
        title = "Welcome",
        description = "Track daily habits and build routines that improve your energy and focus."
    ),
    OnboardingStep(
        title = "Insights",
        description = "See clear trends for activity, sleep, and hydration in one shared dashboard."
    ),
    OnboardingStep(
        title = "Your Goal",
        description = "Pick your current focus so the app can prioritize the metrics that matter most."
    )
)

fun classifyLayout(maxWidthDp: Float): OnboardingLayoutClass {
    return when {
        maxWidthDp < 600f -> OnboardingLayoutClass.Compact
        maxWidthDp < 840f -> OnboardingLayoutClass.Medium
        else -> OnboardingLayoutClass.Expanded
    }
}

fun reduceOnboardingState(
    state: OnboardingUiState,
    event: OnboardingEvent,
    totalSteps: Int = onboardingSteps.size
): OnboardingUiState {
    if (state.completed) {
        return state
    }

    val maxStepIndex = max(0, totalSteps - 1)

    return when (event) {
        OnboardingEvent.Back -> state.copy(stepIndex = max(0, state.stepIndex - 1))
        OnboardingEvent.Next -> {
            if (state.stepIndex >= maxStepIndex) {
                state.copy(completed = true)
            } else {
                state.copy(stepIndex = min(maxStepIndex, state.stepIndex + 1))
            }
        }
        OnboardingEvent.Skip,
        OnboardingEvent.Finish -> state.copy(completed = true)
        is OnboardingEvent.GoalSelected -> state.copy(selectedGoal = event.goal)
    }
}
