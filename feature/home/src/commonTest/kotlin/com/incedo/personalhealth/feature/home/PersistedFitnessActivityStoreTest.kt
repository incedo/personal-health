package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersistedFitnessActivityStoreTest {

    @Test
    fun upsertAndReadSessions_returnsSessionsSortedByStartTime() {
        val driver = InMemoryFitnessDriver()
        val store = PersistedFitnessActivityStore(driver)

        store.upsertSession(session(id = "session-1", startedAt = 100L))
        store.upsertSession(session(id = "session-2", startedAt = 300L))

        val sessions = store.readSessions()

        assertEquals(listOf("session-2", "session-1"), sessions.map { it.id })
    }

    @Test
    fun upsertSession_replacesExistingSession() {
        val driver = InMemoryFitnessDriver()
        val store = PersistedFitnessActivityStore(driver)

        store.upsertSession(session(id = "session-1", title = "Lower body"))
        store.upsertSession(session(id = "session-1", title = "Full body"))

        val sessions = store.readSessions()

        assertEquals(1, sessions.size)
        assertEquals("Full body", sessions.single().title)
    }

    @Test
    fun deleteAndClear_removePersistedSessions() {
        val driver = InMemoryFitnessDriver()
        val store = PersistedFitnessActivityStore(driver)
        store.upsertSession(session(id = "session-1"))
        store.upsertSession(session(id = "session-2"))

        store.deleteSession("session-1")
        assertEquals(listOf("session-2"), store.readSessions().map { it.id })

        store.clear()
        assertTrue(store.readSessions().isEmpty())
    }

    private fun session(
        id: String,
        title: String = "Krachttraining",
        startedAt: Long = 100L
    ): FitnessActivitySession = FitnessActivitySession(
        id = id,
        title = title,
        startedAtEpochMillis = startedAt,
        completedAtEpochMillis = startedAt + 3_600_000L,
        muscleGroups = listOf(
            MuscleGroup(
                id = "CHEST",
                label = "Borst",
                region = MuscleGroupRegion.FRONT
            )
        ),
        exercises = listOf(
            FitnessExercise(
                id = "$id-squat",
                name = "Squat",
                setCount = 4,
                repsPerSet = 6,
                weightKg = 80,
                primaryMuscleGroupId = "CHEST"
            )
        )
    )

    private class InMemoryFitnessDriver : FitnessActivityPersistenceDriver {
        private var payload: String? = null

        override fun read(): String? = payload

        override fun write(payload: String) {
            this.payload = payload
        }

        override fun clear() {
            payload = null
        }
    }
}
