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
        description = "A calm, personal guide for long-term health, trained on your data and your pace."
    ),
    OnboardingStep(
        title = "Goals",
        description = "Choose the direction for your first plan so the dashboard can prioritize what matters."
    ),
    OnboardingStep(
        title = "About You",
        description = "Add the basics we need for accurate calorie, strength and cardio calculations."
    ),
    OnboardingStep(
        title = "Activity Level",
        description = "Set a realistic starting point so recommendations match your current load."
    ),
    OnboardingStep(
        title = "Availability",
        description = "Build a weekly rhythm around the days and hours you can actually train."
    ),
    OnboardingStep(
        title = "Devices",
        description = "Connect data sources when you are ready, or continue with manual tracking first."
    ),
    OnboardingStep(
        title = "Nutrition",
        description = "Tell us how you eat so the plan can stay practical instead of theoretical."
    ),
    OnboardingStep(
        title = "Baseline",
        description = "Capture your starting point for weight, sleep and recovery signals."
    ),
    OnboardingStep(
        title = "Ready",
        description = "Review the starting plan and open your dashboard."
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
