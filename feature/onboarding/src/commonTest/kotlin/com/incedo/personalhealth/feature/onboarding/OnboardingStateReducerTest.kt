package com.incedo.personalhealth.feature.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingStateReducerTest {

    @Test
    fun classifyLayout_mapsBreakpointsCorrectly() {
        assertEquals(OnboardingLayoutClass.Compact, classifyLayout(320f))
        assertEquals(OnboardingLayoutClass.Medium, classifyLayout(700f))
        assertEquals(OnboardingLayoutClass.Expanded, classifyLayout(900f))
    }

    @Test
    fun reducer_backDoesNotGoBelowZero() {
        val result = reduceOnboardingState(OnboardingUiState(stepIndex = 0), OnboardingEvent.Back)
        assertEquals(0, result.stepIndex)
    }

    @Test
    fun reducer_skipCompletesFlowImmediately() {
        val result = reduceOnboardingState(OnboardingUiState(), OnboardingEvent.Skip)
        assertTrue(result.completed)
    }

    @Test
    fun reducer_doesNotMutateCompletedState() {
        val completed = OnboardingUiState(stepIndex = 2, selectedGoal = OnboardingGoal.Activity, completed = true)
        val result = reduceOnboardingState(completed, OnboardingEvent.Next)
        assertEquals(completed, result)
    }

    @Test
    fun reducer_goalSelectionPersistsAcrossNext() {
        val selected = reduceOnboardingState(
            state = OnboardingUiState(),
            event = OnboardingEvent.GoalSelected(OnboardingGoal.BetterSleep)
        )
        val moved = reduceOnboardingState(selected, OnboardingEvent.Next)

        assertEquals(OnboardingGoal.BetterSleep, moved.selectedGoal)
        assertFalse(moved.completed)
    }

    @Test
    fun reducer_bridgesFullOnboardingStateEvents() {
        val profile = OnboardingProfile(
            gender = OnboardingGender.Male,
            ageYears = "42",
            heightCm = "184",
            weightKg = "86"
        )
        val baseline = OnboardingBaseline(
            sleepHours = "7",
            restingHeartRateBpm = "62",
            bodyWeightKg = "86"
        )

        val result = listOf(
            OnboardingEvent.ProfileChanged(profile),
            OnboardingEvent.ActivityLevelSelected(OnboardingActivityLevel.Regular),
            OnboardingEvent.AvailabilityChanged(
                OnboardingAvailability(days = listOf(OnboardingDay.Tuesday), weeklyHours = 6)
            ),
            OnboardingEvent.DeviceToggled(OnboardingDevice.HealthConnect),
            OnboardingEvent.NutritionChanged(
                OnboardingNutrition(
                    style = OnboardingNutritionStyle.HighProtein,
                    restrictions = listOf(OnboardingDietaryRestriction.DairyFree)
                )
            ),
            OnboardingEvent.BaselineChanged(baseline)
        ).fold(OnboardingUiState()) { state, event -> reduceOnboardingState(state, event) }

        assertEquals(profile, result.profile)
        assertEquals(OnboardingActivityLevel.Regular, result.activityLevel)
        assertEquals(listOf(OnboardingDay.Tuesday), result.availability.days)
        assertEquals(listOf(OnboardingDevice.HealthConnect), result.devices)
        assertEquals(OnboardingNutritionStyle.HighProtein, result.nutrition.style)
        assertEquals(listOf(OnboardingDietaryRestriction.DairyFree), result.nutrition.restrictions)
        assertEquals(baseline, result.baseline)
    }
}
