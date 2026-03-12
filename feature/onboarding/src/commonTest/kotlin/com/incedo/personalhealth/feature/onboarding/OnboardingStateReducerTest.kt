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
}
