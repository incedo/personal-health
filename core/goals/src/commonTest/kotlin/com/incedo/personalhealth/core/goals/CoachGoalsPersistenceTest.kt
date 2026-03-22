package com.incedo.personalhealth.core.goals

import kotlin.test.Test
import kotlin.test.assertEquals

class CoachGoalsPersistenceTest {

    @Test
    fun encodeAndDecodeCoachGoals_roundTripsGoals() {
        val goals = listOf(
            CoachGoal("8.000 stappen halen", "Vandaag", "Beweging"),
            CoachGoal("23:00 in bed liggen", "Vanavond", "Slaap")
        )

        val encoded = encodeCoachGoals(goals)
        val decoded = decodeCoachGoals(encoded)

        assertEquals(goals, decoded)
    }
}
