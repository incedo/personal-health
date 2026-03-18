package com.incedo.personalhealth.feature.home

enum class HomeDetailDestination {
    STEPS,
    FITNESS
}

enum class FitnessMuscleGroup(
    val label: String,
    val region: MuscleGroupRegion
) {
    CHEST("Borst", MuscleGroupRegion.FRONT),
    SHOULDERS_FRONT("Schouders", MuscleGroupRegion.FRONT),
    BICEPS("Biceps", MuscleGroupRegion.FRONT),
    CORE("Core", MuscleGroupRegion.FRONT),
    QUADS("Quadriceps", MuscleGroupRegion.FRONT),
    CALVES("Kuiten", MuscleGroupRegion.FRONT),
    UPPER_BACK("Bovenrug", MuscleGroupRegion.BACK),
    LATS("Lats", MuscleGroupRegion.BACK),
    TRICEPS("Triceps", MuscleGroupRegion.BACK),
    GLUTES("Billen", MuscleGroupRegion.BACK),
    HAMSTRINGS("Hamstrings", MuscleGroupRegion.BACK),
    CALVES_BACK("Kuiten achter", MuscleGroupRegion.BACK)
}

enum class FitnessExerciseTemplate(
    val label: String,
    val defaultSets: Int,
    val defaultReps: Int,
    val defaultWeightKg: Int,
    val primaryMuscleGroup: FitnessMuscleGroup
) {
    SQUAT("Squat", 4, 6, 80, FitnessMuscleGroup.QUADS),
    BENCH_PRESS("Bench press", 4, 8, 60, FitnessMuscleGroup.CHEST),
    DEADLIFT("Deadlift", 4, 5, 100, FitnessMuscleGroup.HAMSTRINGS),
    SHOULDER_PRESS("Shoulder press", 3, 10, 22, FitnessMuscleGroup.SHOULDERS_FRONT),
    BARBELL_ROW("Barbell row", 4, 8, 55, FitnessMuscleGroup.UPPER_BACK),
    PLANK("Plank", 3, 1, 0, FitnessMuscleGroup.CORE)
}

data class FitnessExerciseDraft(
    val template: FitnessExerciseTemplate,
    val setCount: Int,
    val repsPerSet: Int,
    val weightKg: Int
)

data class FitnessSessionDraft(
    val sessionId: String? = null,
    val title: String,
    val notes: String,
    val selectedMuscleGroups: Set<FitnessMuscleGroup>,
    val exercises: List<FitnessExerciseDraft>
)

data class FitnessLibrarySummary(
    val sessionCount: Int,
    val totalExercises: Int,
    val totalVolumeKg: Int
)

fun newFitnessSessionDraft(sessionOrdinal: Int): FitnessSessionDraft = FitnessSessionDraft(
    sessionId = null,
    title = "Fitness sessie $sessionOrdinal",
    notes = "",
    selectedMuscleGroups = emptySet(),
    exercises = emptyList()
)

fun fitnessSessionDraftFromSession(session: FitnessActivitySession): FitnessSessionDraft = FitnessSessionDraft(
    sessionId = session.id,
    title = session.title,
    notes = session.notes,
    selectedMuscleGroups = session.muscleGroups.mapNotNull { stored ->
        FitnessMuscleGroup.entries.firstOrNull { it.name == stored.id }
    }.toSet(),
    exercises = session.exercises.map { exercise ->
        val template = FitnessExerciseTemplate.entries.firstOrNull { it.label == exercise.name }
            ?: FitnessExerciseTemplate.entries.firstOrNull { it.primaryMuscleGroup.name == exercise.primaryMuscleGroupId }
            ?: FitnessExerciseTemplate.PLANK
        FitnessExerciseDraft(
            template = template,
            setCount = exercise.setCount,
            repsPerSet = exercise.repsPerSet,
            weightKg = exercise.weightKg
        )
    }
)

fun toggleFitnessExercise(
    draft: FitnessSessionDraft,
    template: FitnessExerciseTemplate
): FitnessSessionDraft {
    val existing = draft.exercises.firstOrNull { it.template == template }
    val updatedExercises = if (existing == null) {
        draft.exercises + FitnessExerciseDraft(
            template = template,
            setCount = template.defaultSets,
            repsPerSet = template.defaultReps,
            weightKg = template.defaultWeightKg
        )
    } else {
        draft.exercises.filterNot { it.template == template }
    }
    return draft.copy(exercises = updatedExercises)
}

fun updateFitnessDraftTitle(
    draft: FitnessSessionDraft,
    title: String
): FitnessSessionDraft = draft.copy(title = title)

fun updateFitnessDraftNotes(
    draft: FitnessSessionDraft,
    notes: String
): FitnessSessionDraft = draft.copy(notes = notes)

fun toggleFitnessMuscleGroup(
    draft: FitnessSessionDraft,
    muscleGroup: FitnessMuscleGroup
): FitnessSessionDraft {
    val updated = if (muscleGroup in draft.selectedMuscleGroups) {
        draft.selectedMuscleGroups - muscleGroup
    } else {
        draft.selectedMuscleGroups + muscleGroup
    }
    return draft.copy(selectedMuscleGroups = updated)
}

fun updateFitnessExerciseDraft(
    draft: FitnessSessionDraft,
    template: FitnessExerciseTemplate,
    transform: (FitnessExerciseDraft) -> FitnessExerciseDraft
): FitnessSessionDraft = draft.copy(
    exercises = draft.exercises.map { exercise ->
        if (exercise.template == template) transform(exercise) else exercise
    }
)

fun fitnessDraftCanSave(draft: FitnessSessionDraft): Boolean =
    draft.title.isNotBlank() && draft.exercises.isNotEmpty()

fun buildFitnessSession(
    draft: FitnessSessionDraft,
    sessionId: String,
    startedAtEpochMillis: Long,
    completedAtEpochMillis: Long
): FitnessActivitySession = FitnessActivitySession(
    id = sessionId,
    title = draft.title.trim(),
    startedAtEpochMillis = startedAtEpochMillis,
    completedAtEpochMillis = completedAtEpochMillis,
    notes = draft.notes.trim(),
    muscleGroups = draft.selectedMuscleGroups.map { group ->
        MuscleGroup(
            id = group.name,
            label = group.label,
            region = group.region
        )
    },
    exercises = draft.exercises.map { exercise ->
        FitnessExercise(
            id = "$sessionId-${exercise.template.name.lowercase()}",
            name = exercise.template.label,
            setCount = exercise.setCount,
            repsPerSet = exercise.repsPerSet,
            weightKg = exercise.weightKg,
            primaryMuscleGroupId = exercise.template.primaryMuscleGroup.name
        )
    }
)

fun fitnessLibrarySummary(sessions: List<FitnessActivitySession>): FitnessLibrarySummary =
    FitnessLibrarySummary(
        sessionCount = sessions.size,
        totalExercises = sessions.sumOf { it.exercises.size },
        totalVolumeKg = sessions.sumOf(::fitnessSessionVolumeKg)
    )

fun fitnessSessionVolumeKg(session: FitnessActivitySession): Int =
    session.exercises.sumOf { exercise ->
        exercise.setCount * exercise.repsPerSet * exercise.weightKg
    }

fun fitnessSessionExerciseCount(session: FitnessActivitySession): Int = session.exercises.size

fun availableExerciseTemplates(
    selectedMuscleGroups: Set<FitnessMuscleGroup>
): List<FitnessExerciseTemplate> = if (selectedMuscleGroups.isEmpty()) {
    FitnessExerciseTemplate.entries
} else {
    FitnessExerciseTemplate.entries.filter { it.primaryMuscleGroup in selectedMuscleGroups }
}

fun formatMuscleGroupSummary(muscleGroups: List<MuscleGroup>): String = when {
    muscleGroups.isEmpty() -> "Geen spiergroepen gekozen"
    muscleGroups.size == 1 -> muscleGroups.single().label
    else -> muscleGroups.joinToString(", ") { it.label }
}

fun fitnessSessionToQuickActivityEntry(session: FitnessActivitySession): QuickActivityEntry =
    QuickActivityEntry(
        id = session.id,
        type = QuickActivityType.FITNESS,
        title = "${session.title} • ${session.exercises.size} oefeningen"
    )
