package com.incedo.personalhealth.feature.onboarding

import androidx.compose.runtime.saveable.Saver

typealias OnboardingGoal = com.incedo.personalhealth.core.onboarding.OnboardingGoal
typealias OnboardingLayoutClass = com.incedo.personalhealth.core.onboarding.OnboardingLayoutClass
typealias OnboardingStep = com.incedo.personalhealth.core.onboarding.OnboardingStep
typealias OnboardingUiState = com.incedo.personalhealth.core.onboarding.OnboardingUiState

sealed interface OnboardingEvent {
    data object Next : OnboardingEvent
    data object Back : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Finish : OnboardingEvent
    data class GoalSelected(val goal: OnboardingGoal) : OnboardingEvent
}

val OnboardingUiStateSaver: Saver<OnboardingUiState, List<Any?>> = Saver(
    save = { state ->
        listOf(
            state.stepIndex,
            state.selectedGoal?.name,
            state.completed
        )
    },
    restore = { saved ->
        val stepIndex = saved.getOrNull(0) as? Int ?: 0
        val selectedGoal = (saved.getOrNull(1) as? String)?.let(OnboardingGoal::valueOf)
        val completed = saved.getOrNull(2) as? Boolean ?: false
        OnboardingUiState(
            stepIndex = stepIndex,
            selectedGoal = selectedGoal,
            completed = completed
        )
    }
)
val onboardingSteps = com.incedo.personalhealth.core.onboarding.onboardingSteps

fun classifyLayout(maxWidthDp: Float): OnboardingLayoutClass =
    com.incedo.personalhealth.core.onboarding.classifyLayout(maxWidthDp)

fun reduceOnboardingState(
    state: OnboardingUiState,
    event: OnboardingEvent,
    totalSteps: Int = onboardingSteps.size
): OnboardingUiState = com.incedo.personalhealth.core.onboarding.reduceOnboardingState(
    state = state,
    event = event.toCoreEvent(),
    totalSteps = totalSteps
)

private fun OnboardingEvent.toCoreEvent(): com.incedo.personalhealth.core.onboarding.OnboardingEvent {
    return when (this) {
        OnboardingEvent.Next -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.Next
        OnboardingEvent.Back -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.Back
        OnboardingEvent.Skip -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.Skip
        OnboardingEvent.Finish -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.Finish
        is OnboardingEvent.GoalSelected -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.GoalSelected(goal)
    }
}
