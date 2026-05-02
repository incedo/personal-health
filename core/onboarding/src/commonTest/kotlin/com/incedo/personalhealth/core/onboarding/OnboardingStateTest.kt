package com.incedo.personalhealth.core.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingStateTest {

    @Test
    fun classifyLayout_mapsBreakpointsCorrectly() {
        assertEquals(OnboardingLayoutClass.Compact, classifyLayout(320f))
        assertEquals(OnboardingLayoutClass.Medium, classifyLayout(700f))
        assertEquals(OnboardingLayoutClass.Expanded, classifyLayout(900f))
    }

    @Test
    fun onboardingSteps_matchDesignFlowLength() {
        assertEquals(9, onboardingSteps.size)
    }

    @Test
    fun reducer_preservesSelectedGoalAcrossNext() {
        val selected = reduceOnboardingState(
            state = OnboardingUiState(),
            event = OnboardingEvent.GoalSelected(OnboardingGoal.BetterSleep)
        )
        val moved = reduceOnboardingState(selected, OnboardingEvent.Next)

        assertEquals(OnboardingGoal.BetterSleep, moved.selectedGoal)
        assertFalse(moved.completed)
    }

    @Test
    fun reducer_preservesProfileAcrossNext() {
        val profile = OnboardingProfile(
            gender = OnboardingGender.Female,
            ageYears = "35",
            heightCm = "171",
            weightKg = "68"
        )
        val updated = reduceOnboardingState(OnboardingUiState(), OnboardingEvent.ProfileChanged(profile))
        val moved = reduceOnboardingState(updated, OnboardingEvent.Next)

        assertEquals(profile, moved.profile)
        assertFalse(moved.completed)
    }

    @Test
    fun reducer_normalizesAvailability() {
        val result = reduceOnboardingState(
            state = OnboardingUiState(),
            event = OnboardingEvent.AvailabilityChanged(
                OnboardingAvailability(
                    days = listOf(OnboardingDay.Monday, OnboardingDay.Monday, OnboardingDay.Friday),
                    weeklyHours = 42
                )
            )
        )

        assertEquals(listOf(OnboardingDay.Monday, OnboardingDay.Friday), result.availability.days)
        assertEquals(20, result.availability.weeklyHours)
    }

    @Test
    fun reducer_togglesDevices() {
        val connected = reduceOnboardingState(OnboardingUiState(), OnboardingEvent.DeviceToggled(OnboardingDevice.Wearable))
        val disconnected = reduceOnboardingState(connected, OnboardingEvent.DeviceToggled(OnboardingDevice.Wearable))

        assertEquals(listOf(OnboardingDevice.Wearable), connected.devices)
        assertEquals(emptyList(), disconnected.devices)
    }

    @Test
    fun reducer_normalizesNutritionRestrictions() {
        val result = reduceOnboardingState(
            state = OnboardingUiState(),
            event = OnboardingEvent.NutritionChanged(
                OnboardingNutrition(
                    style = OnboardingNutritionStyle.PlantForward,
                    restrictions = listOf(
                        OnboardingDietaryRestriction.Vegan,
                        OnboardingDietaryRestriction.Vegan,
                        OnboardingDietaryRestriction.NutFree
                    )
                )
            )
        )

        assertEquals(OnboardingNutritionStyle.PlantForward, result.nutrition.style)
        assertEquals(
            listOf(OnboardingDietaryRestriction.Vegan, OnboardingDietaryRestriction.NutFree),
            result.nutrition.restrictions
        )
    }

    @Test
    fun reducer_updatesBaseline() {
        val baseline = OnboardingBaseline(
            sleepHours = "7.5",
            restingHeartRateBpm = "58",
            bodyWeightKg = "68"
        )

        val result = reduceOnboardingState(OnboardingUiState(), OnboardingEvent.BaselineChanged(baseline))

        assertEquals(baseline, result.baseline)
    }

    @Test
    fun reducer_marksFlowCompleteAfterLastStepNext() {
        val result = reduceOnboardingState(
            state = OnboardingUiState(stepIndex = onboardingSteps.lastIndex),
            event = OnboardingEvent.Next
        )

        assertTrue(result.completed)
    }
}
