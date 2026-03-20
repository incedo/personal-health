package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeActivityTrackingModelsTest {

    @Test
    fun totalTrackedActivityMinutes_countsCompletedAndActiveOverlapWithinDay() {
        val dayWindow = LocalDayWindow(
            startEpochMillis = 0L,
            endEpochMillisExclusive = 7_200_000L
        )

        val minutes = totalTrackedActivityMinutes(
            completedSessions = listOf(
                CompletedQuickActivitySession(
                    id = "run-1",
                    type = QuickActivityType.RUNNING,
                    startedAtEpochMillis = 0L,
                    completedAtEpochMillis = 1_800_000L
                )
            ),
            activeSession = ActiveQuickActivitySession(
                id = "walk-1",
                type = QuickActivityType.WALKING,
                startedAtEpochMillis = 4_500_000L
            ),
            nowEpochMillis = 7_500_000L,
            dayWindow = dayWindow
        )

        assertEquals(75, minutes)
    }

    @Test
    fun completedSession_mapsDurationIntoQuickEntry() {
        val entry = CompletedQuickActivitySession(
            id = "cycle-1",
            type = QuickActivityType.CYCLING,
            startedAtEpochMillis = 1_000L,
            completedAtEpochMillis = 4_600_000L
        ).toQuickActivityEntry()

        assertEquals("cycle-1", entry.id)
        assertEquals(QuickActivityType.CYCLING, entry.type)
        assertEquals(4_599_000L, entry.durationMillis)
    }
}
