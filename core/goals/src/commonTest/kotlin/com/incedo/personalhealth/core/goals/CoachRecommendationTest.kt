package com.incedo.personalhealth.core.goals

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoachRecommendationTest {

    @Test
    fun effectiveCoachIntakeProfile_usesOnboardingGoalWhenIntakeIsEmpty() {
        val effective = effectiveCoachIntakeProfile(
            intakeProfile = CoachIntakeProfile(),
            onboardingFocusGoal = CoachFocusGoal.BETTER_SLEEP
        )

        assertEquals(CoachFocusGoal.BETTER_SLEEP, effective.focusGoal)
    }

    @Test
    fun buildCoachRecommendation_prioritizesNutritionForMetabolicPlan() {
        val recommendation = buildCoachRecommendation(
            CoachRecommendationInput(
                onboardingFocusGoal = CoachFocusGoal.NUTRITION,
                intakeProfile = CoachIntakeProfile(
                    traits = setOf(CoachProfileTrait.DATA_DRIVEN)
                )
            )
        )

        assertEquals(CoachProtocolId.COMPOSITION, recommendation.protocolId)
        assertEquals(CoachSupportTab.LOGBOOK, recommendation.supportTabs.first())
        assertTrue(recommendation.rationale.first().contains("voeding", ignoreCase = true))
    }
}
