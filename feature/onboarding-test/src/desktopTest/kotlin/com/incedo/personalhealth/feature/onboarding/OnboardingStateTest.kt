package com.incedo.personalhealth.feature.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingStateTest {

    @Test
    fun classifyLayout_returnsCompactForPhoneWidth() {
        assertEquals(OnboardingLayoutClass.Compact, classifyLayout(390f))
    }

    @Test
    fun classifyLayout_returnsExpandedForLargeWidth() {
        assertEquals(OnboardingLayoutClass.Expanded, classifyLayout(1024f))
    }

    @Test
    fun reducer_marksFlowCompleteAfterLastStepNext() {
        val atLastStep = OnboardingUiState(stepIndex = onboardingSteps.lastIndex)

        val result = reduceOnboardingState(atLastStep, OnboardingEvent.Next)

        assertTrue(result.completed)
    }

    @Test
    fun reducer_preservesSelectedGoalAcrossNavigation() {
        val selected = reduceOnboardingState(
            state = OnboardingUiState(),
            event = OnboardingEvent.GoalSelected(OnboardingGoal.BetterSleep)
        )
        val moved = reduceOnboardingState(selected, OnboardingEvent.Next)

        assertEquals(OnboardingGoal.BetterSleep, moved.selectedGoal)
        assertFalse(moved.completed)
    }
}
