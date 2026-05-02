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
    fun reducer_marksFlowCompleteAfterLastStepNext() {
        val result = reduceOnboardingState(
            state = OnboardingUiState(stepIndex = onboardingSteps.lastIndex),
            event = OnboardingEvent.Next
        )

        assertTrue(result.completed)
    }
}
