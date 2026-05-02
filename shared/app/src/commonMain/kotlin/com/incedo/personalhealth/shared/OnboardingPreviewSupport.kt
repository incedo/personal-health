package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.onboarding.OnboardingActivityLevel
import com.incedo.personalhealth.feature.onboarding.OnboardingAvailability
import com.incedo.personalhealth.feature.onboarding.OnboardingBaseline
import com.incedo.personalhealth.feature.onboarding.OnboardingDay
import com.incedo.personalhealth.feature.onboarding.OnboardingDevice
import com.incedo.personalhealth.feature.onboarding.OnboardingDietaryRestriction
import com.incedo.personalhealth.feature.onboarding.OnboardingGender
import com.incedo.personalhealth.feature.onboarding.OnboardingGoal
import com.incedo.personalhealth.feature.onboarding.OnboardingNutrition
import com.incedo.personalhealth.feature.onboarding.OnboardingNutritionStyle
import com.incedo.personalhealth.feature.onboarding.OnboardingProfile
import com.incedo.personalhealth.feature.onboarding.OnboardingUiState

fun buildInitialOnboardingState(preview: Boolean): OnboardingUiState {
    return if (preview) {
        OnboardingUiState()
    } else {
        decodeOnboardingState(OnboardingPreferenceStore.statePayload()) ?: OnboardingUiState(
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
        OnboardingPreferenceStore.setStatePayload(encodeOnboardingState(state))
    }
}

fun persistOnboardingFinished(preview: Boolean, onPersisted: () -> Unit) {
    if (!preview) {
        onPersisted()
        OnboardingPreferenceStore.setCompleted(true)
    }
}

private fun encodeOnboardingState(state: OnboardingUiState): String {
    return buildString {
        appendLine("version=1")
        appendLine("stepIndex=${state.stepIndex}")
        appendLine("completed=${state.completed}")
        appendLine("goal=${state.selectedGoal?.name.orEmpty()}")
        appendLine("gender=${state.profile.gender?.name.orEmpty()}")
        appendLine("ageYears=${state.profile.ageYears}")
        appendLine("heightCm=${state.profile.heightCm}")
        appendLine("weightKg=${state.profile.weightKg}")
        appendLine("activityLevel=${state.activityLevel?.name.orEmpty()}")
        appendLine("availabilityDays=${state.availability.days.joinToString(",") { it.name }}")
        appendLine("weeklyHours=${state.availability.weeklyHours}")
        appendLine("devices=${state.devices.joinToString(",") { it.name }}")
        appendLine("nutritionStyle=${state.nutrition.style?.name.orEmpty()}")
        appendLine("restrictions=${state.nutrition.restrictions.joinToString(",") { it.name }}")
        appendLine("sleepHours=${state.baseline.sleepHours}")
        appendLine("restingHeartRateBpm=${state.baseline.restingHeartRateBpm}")
        appendLine("bodyWeightKg=${state.baseline.bodyWeightKg}")
    }
}

private fun decodeOnboardingState(payload: String?): OnboardingUiState? {
    val values = payload?.lineSequence()
        ?.mapNotNull { line ->
            val separator = line.indexOf('=').takeIf { it >= 0 } ?: return@mapNotNull null
            line.substring(0, separator) to line.substring(separator + 1)
        }
        ?.toMap()
        .orEmpty()

    if (values["version"] != "1") {
        return null
    }

    return OnboardingUiState(
        stepIndex = values["stepIndex"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0,
        selectedGoal = enumValue(values["goal"], OnboardingGoal::valueOf),
        profile = OnboardingProfile(
            gender = enumValue(values["gender"], OnboardingGender::valueOf),
            ageYears = values["ageYears"].orEmpty(),
            heightCm = values["heightCm"].orEmpty(),
            weightKg = values["weightKg"].orEmpty()
        ),
        activityLevel = enumValue(values["activityLevel"], OnboardingActivityLevel::valueOf),
        availability = OnboardingAvailability(
            days = enumList(values["availabilityDays"], OnboardingDay::valueOf),
            weeklyHours = values["weeklyHours"]?.toIntOrNull()?.coerceIn(1, 20) ?: 4
        ),
        devices = enumList(values["devices"], OnboardingDevice::valueOf),
        nutrition = OnboardingNutrition(
            style = enumValue(values["nutritionStyle"], OnboardingNutritionStyle::valueOf),
            restrictions = enumList(values["restrictions"], OnboardingDietaryRestriction::valueOf)
        ),
        baseline = OnboardingBaseline(
            sleepHours = values["sleepHours"].orEmpty(),
            restingHeartRateBpm = values["restingHeartRateBpm"].orEmpty(),
            bodyWeightKg = values["bodyWeightKg"].orEmpty()
        ),
        completed = values["completed"]?.toBooleanStrictOrNull() ?: false
    )
}

private fun <T> enumValue(value: String?, parse: (String) -> T): T? {
    return value?.takeIf { it.isNotBlank() }?.let { runCatching { parse(it) }.getOrNull() }
}

private fun <T> enumList(value: String?, parse: (String) -> T): List<T> {
    return value.orEmpty()
        .split(',')
        .filter { it.isNotBlank() }
        .mapNotNull { item -> runCatching { parse(item) }.getOrNull() }
        .distinct()
}
