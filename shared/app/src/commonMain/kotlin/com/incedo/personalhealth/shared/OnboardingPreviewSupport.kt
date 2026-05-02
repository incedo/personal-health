package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.onboarding.OnboardingUiState

fun buildInitialOnboardingState(preview: Boolean): OnboardingUiState {
    return if (preview) {
        OnboardingUiState()
    } else {
        OnboardingUiState(
            stepIndex = OnboardingPreferenceStore.stepIndex(),
            selectedGoal = onboardingGoalFromId(OnboardingPreferenceStore.selectedGoalId()),
            completed = OnboardingPreferenceStore.isCompleted()
        )
    }
}

fun persistOnboardingState(preview: Boolean, state: OnboardingUiState) {
    if (!preview) {
        OnboardingPreferenceStore.setStepIndex(state.stepIndex)
        OnboardingPreferenceStore.setSelectedGoalId(state.selectedGoal?.name)
        OnboardingPreferenceStore.setCompleted(state.completed)
    }
}

fun persistOnboardingFinished(preview: Boolean, onPersisted: () -> Unit) {
    if (!preview) {
        onPersisted()
        OnboardingPreferenceStore.setCompleted(true)
    }
}
