package com.incedo.personalhealth.feature.onboarding

import androidx.compose.runtime.saveable.Saver

typealias OnboardingGoal = com.incedo.personalhealth.core.onboarding.OnboardingGoal
typealias OnboardingGender = com.incedo.personalhealth.core.onboarding.OnboardingGender
typealias OnboardingActivityLevel = com.incedo.personalhealth.core.onboarding.OnboardingActivityLevel
typealias OnboardingAvailability = com.incedo.personalhealth.core.onboarding.OnboardingAvailability
typealias OnboardingBaseline = com.incedo.personalhealth.core.onboarding.OnboardingBaseline
typealias OnboardingDay = com.incedo.personalhealth.core.onboarding.OnboardingDay
typealias OnboardingDevice = com.incedo.personalhealth.core.onboarding.OnboardingDevice
typealias OnboardingLayoutClass = com.incedo.personalhealth.core.onboarding.OnboardingLayoutClass
typealias OnboardingNutrition = com.incedo.personalhealth.core.onboarding.OnboardingNutrition
typealias OnboardingNutritionStyle = com.incedo.personalhealth.core.onboarding.OnboardingNutritionStyle
typealias OnboardingDietaryRestriction = com.incedo.personalhealth.core.onboarding.OnboardingDietaryRestriction
typealias OnboardingProfile = com.incedo.personalhealth.core.onboarding.OnboardingProfile
typealias OnboardingStep = com.incedo.personalhealth.core.onboarding.OnboardingStep
typealias OnboardingUiState = com.incedo.personalhealth.core.onboarding.OnboardingUiState

sealed interface OnboardingEvent {
    data object Next : OnboardingEvent
    data object Back : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Finish : OnboardingEvent
    data class GoalSelected(val goal: OnboardingGoal) : OnboardingEvent
    data class ProfileChanged(val profile: OnboardingProfile) : OnboardingEvent
    data class ActivityLevelSelected(val activityLevel: OnboardingActivityLevel) : OnboardingEvent
    data class AvailabilityChanged(val availability: OnboardingAvailability) : OnboardingEvent
    data class DeviceToggled(val device: OnboardingDevice) : OnboardingEvent
    data class NutritionChanged(val nutrition: OnboardingNutrition) : OnboardingEvent
    data class BaselineChanged(val baseline: OnboardingBaseline) : OnboardingEvent
}

val OnboardingUiStateSaver: Saver<OnboardingUiState, List<Any?>> = Saver(
    save = { state ->
        listOf(
            state.stepIndex,
            state.selectedGoal?.name,
            state.profile.gender?.name,
            state.profile.ageYears,
            state.profile.heightCm,
            state.profile.weightKg,
            state.activityLevel?.name,
            state.availability.days.map { it.name },
            state.availability.weeklyHours,
            state.devices.map { it.name },
            state.nutrition.style?.name,
            state.nutrition.restrictions.map { it.name },
            state.baseline.sleepHours,
            state.baseline.restingHeartRateBpm,
            state.baseline.bodyWeightKg,
            state.completed
        )
    },
    restore = { saved ->
        val stepIndex = saved.getOrNull(0) as? Int ?: 0
        val selectedGoal = (saved.getOrNull(1) as? String)?.let(OnboardingGoal::valueOf)
        val profile = OnboardingProfile(
            gender = (saved.getOrNull(2) as? String)?.let(OnboardingGender::valueOf),
            ageYears = saved.getOrNull(3) as? String ?: "",
            heightCm = saved.getOrNull(4) as? String ?: "",
            weightKg = saved.getOrNull(5) as? String ?: ""
        )
        val activityLevel = (saved.getOrNull(6) as? String)?.let(OnboardingActivityLevel::valueOf)
        val availability = OnboardingAvailability(
            days = enumList(saved.getOrNull(7), OnboardingDay::valueOf),
            weeklyHours = saved.getOrNull(8) as? Int ?: 4
        )
        val devices = enumList(saved.getOrNull(9), OnboardingDevice::valueOf)
        val nutrition = OnboardingNutrition(
            style = (saved.getOrNull(10) as? String)?.let(OnboardingNutritionStyle::valueOf),
            restrictions = enumList(saved.getOrNull(11), OnboardingDietaryRestriction::valueOf)
        )
        val baseline = OnboardingBaseline(
            sleepHours = saved.getOrNull(12) as? String ?: "",
            restingHeartRateBpm = saved.getOrNull(13) as? String ?: "",
            bodyWeightKg = saved.getOrNull(14) as? String ?: ""
        )
        val completed = saved.getOrNull(15) as? Boolean ?: false
        OnboardingUiState(
            stepIndex = stepIndex,
            selectedGoal = selectedGoal,
            profile = profile,
            activityLevel = activityLevel,
            availability = availability,
            devices = devices,
            nutrition = nutrition,
            baseline = baseline,
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
        is OnboardingEvent.ProfileChanged -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.ProfileChanged(profile)
        is OnboardingEvent.ActivityLevelSelected ->
            com.incedo.personalhealth.core.onboarding.OnboardingEvent.ActivityLevelSelected(activityLevel)
        is OnboardingEvent.AvailabilityChanged ->
            com.incedo.personalhealth.core.onboarding.OnboardingEvent.AvailabilityChanged(availability)
        is OnboardingEvent.DeviceToggled -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.DeviceToggled(device)
        is OnboardingEvent.NutritionChanged -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.NutritionChanged(nutrition)
        is OnboardingEvent.BaselineChanged -> com.incedo.personalhealth.core.onboarding.OnboardingEvent.BaselineChanged(baseline)
    }
}

private fun <T> enumList(value: Any?, parse: (String) -> T): List<T> {
    return (value as? List<*>).orEmpty()
        .mapNotNull { item -> (item as? String)?.let { runCatching { parse(it) }.getOrNull() } }
        .distinct()
}
