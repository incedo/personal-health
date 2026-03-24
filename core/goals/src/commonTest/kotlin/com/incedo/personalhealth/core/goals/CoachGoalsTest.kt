package com.incedo.personalhealth.core.goals

import kotlin.test.Test
import kotlin.test.assertEquals

class CoachGoalsTest {

    @Test
    fun addCoachGoal_trimsAndPreventsDuplicates() {
        val initial = CoachGoalsState(
            goals = listOf(
                CoachGoal(
                    title = "8.000 stappen halen",
                    cadence = "Vandaag",
                    focus = "Beweging"
                )
            ),
            draftTitle = "  2 liter water drinken  "
        )

        val added = addCoachGoal(initial, initial.draftTitle, cadence = "Vandaag", focus = "Hydratatie")
        assertEquals(2, added.goals.size)
        assertEquals("2 liter water drinken", added.goals.last().title)
        assertEquals("", added.draftTitle)

        val duplicate = addCoachGoal(added, "2 LITER WATER DRINKEN")
        assertEquals(2, duplicate.goals.size)
        assertEquals("", duplicate.draftTitle)
    }
}
