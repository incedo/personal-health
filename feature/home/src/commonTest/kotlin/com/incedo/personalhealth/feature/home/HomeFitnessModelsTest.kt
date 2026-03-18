package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeFitnessModelsTest {

    @Test
    fun toggleFitnessExercise_addsAndRemovesTemplate() {
        val initial = newFitnessSessionDraft(sessionOrdinal = 1)

        val added = toggleFitnessExercise(initial, FitnessExerciseTemplate.SQUAT)
        val removed = toggleFitnessExercise(added, FitnessExerciseTemplate.SQUAT)

        assertEquals(listOf(FitnessExerciseTemplate.SQUAT), added.exercises.map { it.template })
        assertTrue(removed.exercises.isEmpty())
    }

    @Test
    fun fitnessDraftCanSave_requiresTitleAndExercise() {
        val empty = newFitnessSessionDraft(sessionOrdinal = 1).copy(title = "")
        val filled = toggleFitnessExercise(
            toggleFitnessMuscleGroup(newFitnessSessionDraft(sessionOrdinal = 1), FitnessMuscleGroup.CHEST),
            FitnessExerciseTemplate.BENCH_PRESS
        )

        assertFalse(fitnessDraftCanSave(empty))
        assertTrue(fitnessDraftCanSave(filled))
    }

    @Test
    fun buildFitnessSession_mapsDraftIntoPersistedSession() {
        val draft = updateFitnessDraftNotes(
            updateFitnessDraftTitle(
                toggleFitnessExercise(
                    toggleFitnessMuscleGroup(newFitnessSessionDraft(sessionOrdinal = 3), FitnessMuscleGroup.HAMSTRINGS),
                    FitnessExerciseTemplate.DEADLIFT
                ),
                "Zware pulls"
            ),
            "Voelde stabiel"
        )

        val session = buildFitnessSession(
            draft = draft,
            sessionId = "fitness-123",
            startedAtEpochMillis = 100L,
            completedAtEpochMillis = 200L
        )

        assertEquals("Zware pulls", session.title)
        assertEquals("Voelde stabiel", session.notes)
        assertEquals(1, session.exercises.size)
        assertEquals(listOf("Hamstrings"), session.muscleGroups.map { it.label })
        assertEquals("Deadlift", session.exercises.single().name)
        assertEquals("fitness-123-deadlift", session.exercises.single().id)
        assertEquals(FitnessMuscleGroup.HAMSTRINGS.name, session.exercises.single().primaryMuscleGroupId)
    }

    @Test
    fun fitnessLibrarySummary_accumulatesExerciseAndVolumeTotals() {
        val first = buildFitnessSession(
            draft = toggleFitnessExercise(
                toggleFitnessMuscleGroup(newFitnessSessionDraft(sessionOrdinal = 1), FitnessMuscleGroup.QUADS),
                FitnessExerciseTemplate.SQUAT
            ),
            sessionId = "fitness-1",
            startedAtEpochMillis = 100L,
            completedAtEpochMillis = 200L
        )
        val second = buildFitnessSession(
            draft = toggleFitnessExercise(
                toggleFitnessExercise(
                    toggleFitnessMuscleGroup(newFitnessSessionDraft(sessionOrdinal = 2), FitnessMuscleGroup.CHEST),
                    FitnessExerciseTemplate.BENCH_PRESS
                ),
                FitnessExerciseTemplate.BARBELL_ROW
            ),
            sessionId = "fitness-2",
            startedAtEpochMillis = 300L,
            completedAtEpochMillis = 400L
        )

        val summary = fitnessLibrarySummary(listOf(first, second))

        assertEquals(2, summary.sessionCount)
        assertEquals(3, summary.totalExercises)
        assertTrue(summary.totalVolumeKg > 0)
    }

    @Test
    fun toggleFitnessMuscleGroup_addsAndRemovesSelection() {
        val added = toggleFitnessMuscleGroup(newFitnessSessionDraft(1), FitnessMuscleGroup.CORE)
        val removed = toggleFitnessMuscleGroup(added, FitnessMuscleGroup.CORE)

        assertEquals(setOf(FitnessMuscleGroup.CORE), added.selectedMuscleGroups)
        assertTrue(removed.selectedMuscleGroups.isEmpty())
    }

    @Test
    fun fitnessSessionDraftFromSession_restoresEditableState() {
        val session = buildFitnessSession(
            draft = toggleFitnessExercise(
                toggleFitnessMuscleGroup(newFitnessSessionDraft(1), FitnessMuscleGroup.UPPER_BACK),
                FitnessExerciseTemplate.BARBELL_ROW
            ),
            sessionId = "fitness-22",
            startedAtEpochMillis = 10L,
            completedAtEpochMillis = 20L
        )

        val draft = fitnessSessionDraftFromSession(session)

        assertEquals("fitness-22", draft.sessionId)
        assertEquals(setOf(FitnessMuscleGroup.UPPER_BACK), draft.selectedMuscleGroups)
        assertEquals(listOf(FitnessExerciseTemplate.BARBELL_ROW), draft.exercises.map { it.template })
    }

    @Test
    fun availableExerciseTemplates_filtersBySelectedMuscleGroups() {
        val templates = availableExerciseTemplates(setOf(FitnessMuscleGroup.CHEST))

        assertEquals(listOf(FitnessExerciseTemplate.BENCH_PRESS), templates)
    }
}
