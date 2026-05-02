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

enum class OnboardingGender {
    Female,
    Male,
    Other
}

enum class OnboardingActivityLevel {
    Starter,
    Recreational,
    Regular,
    Athletic
}

enum class OnboardingDay {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

enum class OnboardingDevice {
    AppleHealth,
    HealthConnect,
    Wearable,
    Manual
}

enum class OnboardingNutritionStyle {
    Balanced,
    HighProtein,
    PlantForward,
    Flexible
}

enum class OnboardingDietaryRestriction {
    Vegetarian,
    Vegan,
    GlutenFree,
    DairyFree,
    NutFree
}

data class OnboardingStep(
    val title: String,
    val description: String
)

data class OnboardingProfile(
    val gender: OnboardingGender? = null,
    val ageYears: String = "",
    val heightCm: String = "",
    val weightKg: String = ""
)

data class OnboardingAvailability(
    val days: List<OnboardingDay> = emptyList(),
    val weeklyHours: Int = 4
)

data class OnboardingNutrition(
    val style: OnboardingNutritionStyle? = null,
    val restrictions: List<OnboardingDietaryRestriction> = emptyList()
)

data class OnboardingBaseline(
    val sleepHours: String = "",
    val restingHeartRateBpm: String = "",
    val bodyWeightKg: String = ""
)

data class OnboardingUiState(
    val stepIndex: Int = 0,
    val selectedGoal: OnboardingGoal? = null,
    val profile: OnboardingProfile = OnboardingProfile(),
    val activityLevel: OnboardingActivityLevel? = null,
    val availability: OnboardingAvailability = OnboardingAvailability(),
    val devices: List<OnboardingDevice> = emptyList(),
    val nutrition: OnboardingNutrition = OnboardingNutrition(),
    val baseline: OnboardingBaseline = OnboardingBaseline(),
    val completed: Boolean = false
)

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
        is OnboardingEvent.ProfileChanged -> state.copy(profile = event.profile)
        is OnboardingEvent.ActivityLevelSelected -> state.copy(activityLevel = event.activityLevel)
        is OnboardingEvent.AvailabilityChanged -> state.copy(availability = event.availability.normalized())
        is OnboardingEvent.DeviceToggled -> state.copy(devices = state.devices.toggle(event.device))
        is OnboardingEvent.NutritionChanged -> state.copy(nutrition = event.nutrition.normalized())
        is OnboardingEvent.BaselineChanged -> state.copy(baseline = event.baseline)
    }
}

private fun OnboardingAvailability.normalized(): OnboardingAvailability {
    return copy(days = days.distinct(), weeklyHours = weeklyHours.coerceIn(1, 20))
}

private fun OnboardingNutrition.normalized(): OnboardingNutrition {
    return copy(restrictions = restrictions.distinct())
}

private fun <T> List<T>.toggle(value: T): List<T> {
    return if (contains(value)) filterNot { it == value } else this + value
}
