package com.incedo.personalhealth.feature.home

enum class HomeDetailDestination {
    STEPS,
    HEART_RATE,
    WEIGHT,
    HEALTH_DATA,
    FITNESS,
    COACH_INTAKE,
    COACH_GOALS,
    COACH_DETAILS,
    FITNESS_EDITOR_DEBUG
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
    val selectedPrimaryGroup: FitnessPrimaryMuscleGroup? = null,
    val selectedMuscleDetails: Set<FitnessMuscleDetail>,
    val exercises: List<FitnessExerciseDraft>
)

data class FitnessLibrarySummary(
    val sessionCount: Int,
    val totalExercises: Int,
    val totalVolumeKg: Int
)

fun newFitnessSessionDraft(sessionOrdinal: Int): FitnessSessionDraft = FitnessSessionDraft(
    sessionId = null,
    title = "",
    notes = "",
    selectedPrimaryGroup = null,
    selectedMuscleDetails = emptySet(),
    exercises = emptyList()
)

fun fitnessSessionDraftFromSession(session: FitnessActivitySession): FitnessSessionDraft {
    val selectedPrimaryGroup = session.primaryMuscleGroupId
        ?.let { stored -> FitnessPrimaryMuscleGroup.entries.firstOrNull { it.name == stored } }
        ?: session.muscleGroups.firstNotNullOfOrNull { stored ->
            FitnessMuscleDetail.entries.firstOrNull { it.name == stored.id }?.primaryGroup
        }
        ?: session.exercises.firstNotNullOfOrNull { exercise ->
            exercise.detailMuscleId?.let { detailId ->
                FitnessMuscleDetail.entries.firstOrNull { it.name == detailId }?.primaryGroup
            }
        }

    return FitnessSessionDraft(
        sessionId = session.id,
        title = session.title,
        notes = session.notes,
        selectedPrimaryGroup = selectedPrimaryGroup,
        selectedMuscleDetails = session.muscleGroups.mapNotNull { stored ->
            FitnessMuscleDetail.entries.firstOrNull { it.name == stored.id }
        }.toSet(),
        exercises = session.exercises.map { exercise ->
            val template = FitnessExerciseTemplate.entries.firstOrNull { it.label == exercise.name }
                ?: exercise.detailMuscleId?.let { detailId ->
                    FitnessExerciseTemplate.entries.firstOrNull { it.detailMuscle.name == detailId }
                }
                ?: selectedPrimaryGroup?.let { group ->
                    FitnessExerciseTemplate.entries.firstOrNull { it.primaryMuscleGroup == group }
                }
                ?: FitnessExerciseTemplate.BENCH_PRESS
            FitnessExerciseDraft(
                template = template,
                setCount = exercise.setCount,
                repsPerSet = exercise.repsPerSet,
                weightKg = exercise.weightKg
            )
        }
    )
}

fun selectFitnessPrimaryGroup(
    draft: FitnessSessionDraft,
    primaryGroup: FitnessPrimaryMuscleGroup
): FitnessSessionDraft {
    val currentSelection = if (draft.selectedPrimaryGroup == primaryGroup) null else primaryGroup
    val keptDetails = draft.selectedMuscleDetails.filterTo(mutableSetOf()) { it.primaryGroup == currentSelection }
    val keptExercises = draft.exercises.filter { exercise ->
        exercise.template.primaryMuscleGroup == currentSelection &&
            (keptDetails.isEmpty() || exercise.template.detailMuscle in keptDetails)
    }
    return draft.copy(
        selectedPrimaryGroup = currentSelection,
        selectedMuscleDetails = keptDetails,
        exercises = keptExercises
    )
}

fun toggleFitnessMuscleDetail(
    draft: FitnessSessionDraft,
    detail: FitnessMuscleDetail
): FitnessSessionDraft {
    val updatedDetails = if (detail in draft.selectedMuscleDetails) {
        draft.selectedMuscleDetails - detail
    } else {
        draft.selectedMuscleDetails + detail
    }
    val filteredExercises = draft.exercises.filter { exercise ->
        updatedDetails.isEmpty() || exercise.template.detailMuscle in updatedDetails
    }
    return draft.copy(
        selectedPrimaryGroup = detail.primaryGroup,
        selectedMuscleDetails = updatedDetails,
        exercises = filteredExercises
    )
}

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
    val updatedDetails = draft.selectedMuscleDetails + template.detailMuscle
    return draft.copy(
        selectedPrimaryGroup = template.primaryMuscleGroup,
        selectedMuscleDetails = updatedDetails,
        exercises = updatedExercises
    )
}

fun updateFitnessDraftTitle(
    draft: FitnessSessionDraft,
    title: String
): FitnessSessionDraft = draft.copy(title = title)

fun updateFitnessDraftNotes(
    draft: FitnessSessionDraft,
    notes: String
): FitnessSessionDraft = draft.copy(notes = notes)

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
    draft.selectedPrimaryGroup != null &&
        draft.selectedMuscleDetails.isNotEmpty() &&
        draft.exercises.isNotEmpty()

fun generatedFitnessSessionTitle(draft: FitnessSessionDraft): String {
    val primaryLabel = draft.selectedPrimaryGroup?.label ?: "Fitness"
    val detailSummary = draft.selectedMuscleDetails
        .take(2)
        .joinToString(" + ") { it.label }
        .ifBlank { "sessie" }
    return "$primaryLabel • $detailSummary"
}

fun buildFitnessSession(
    draft: FitnessSessionDraft,
    sessionId: String,
    startedAtEpochMillis: Long,
    completedAtEpochMillis: Long
): FitnessActivitySession = FitnessActivitySession(
    id = sessionId,
    title = draft.title.trim().ifBlank { generatedFitnessSessionTitle(draft) },
    startedAtEpochMillis = startedAtEpochMillis,
    completedAtEpochMillis = completedAtEpochMillis,
    notes = draft.notes.trim(),
    primaryMuscleGroupId = draft.selectedPrimaryGroup?.name,
    muscleGroups = draft.selectedMuscleDetails.map { detail ->
        MuscleGroup(
            id = detail.name,
            label = detail.label,
            region = detail.region,
            primaryGroupId = detail.primaryGroup.name,
            focusCue = detail.focusCue
        )
    },
    exercises = draft.exercises.map { exercise ->
        FitnessExercise(
            id = "$sessionId-${exercise.template.name.lowercase()}",
            name = exercise.template.label,
            setCount = exercise.setCount,
            repsPerSet = exercise.repsPerSet,
            weightKg = exercise.weightKg,
            primaryMuscleGroupId = exercise.template.primaryMuscleGroup.name,
            detailMuscleId = exercise.template.detailMuscle.name
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

fun totalFitnessActivityMinutes(
    sessions: List<FitnessActivitySession>,
    dayWindow: LocalDayWindow
): Int = sessions.sumOf { session ->
    val overlapStart = maxOf(session.startedAtEpochMillis, dayWindow.startEpochMillis)
    val overlapEnd = minOf(session.completedAtEpochMillis, dayWindow.endEpochMillisExclusive)
    ((overlapEnd - overlapStart).coerceAtLeast(0L) / 60_000L).toInt()
}

fun formatMuscleGroupSummary(muscleGroups: List<MuscleGroup>): String = when {
    muscleGroups.isEmpty() -> "Geen spierfocus gekozen"
    muscleGroups.size == 1 -> muscleGroups.single().label
    else -> muscleGroups.joinToString(", ") { it.label }
}

fun fitnessPrimaryGroupLabel(primaryGroupId: String?): String =
    primaryGroupId
        ?.let { id -> FitnessPrimaryMuscleGroup.entries.firstOrNull { it.name == id }?.label }
        ?: "Onbekende focus"

fun fitnessSessionToQuickActivityEntry(session: FitnessActivitySession): QuickActivityEntry =
    QuickActivityEntry(
        id = session.id,
        type = QuickActivityType.FITNESS,
        title = "${session.title} • ${session.exercises.size} oefeningen",
        createdAtEpochMillis = session.completedAtEpochMillis,
        durationMillis = (session.completedAtEpochMillis - session.startedAtEpochMillis).coerceAtLeast(0L)
    )
