package com.incedo.personalhealth.core.goals

import kotlin.test.Test
import kotlin.test.assertEquals

class CoachIntakeTest {

    @Test
    fun recommendCoachProtocol_prefersRecoveryForRecoveryProfile() {
        assertEquals(
            CoachProtocolId.RECOVERY,
            recommendCoachProtocol(
                CoachIntakeProfile(
                    focusGoal = CoachFocusGoal.ACTIVITY,
                    traits = setOf(CoachProfileTrait.RECOVERY_MINDED)
                )
            )
        )
    }

    @Test
    fun encodeAndDecodeCoachIntake_roundTrips() {
        val profile = CoachIntakeProfile(
            focusGoal = CoachFocusGoal.NUTRITION,
            traits = setOf(CoachProfileTrait.DATA_DRIVEN, CoachProfileTrait.STRUCTURE_SEEKER)
        )
        assertEquals(profile, decodeCoachIntake(encodeCoachIntake(profile)))
    }
}
