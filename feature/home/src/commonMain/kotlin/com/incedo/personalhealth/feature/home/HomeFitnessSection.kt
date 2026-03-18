package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
internal fun FitnessActivityDetailScreen(
    sessions: List<FitnessActivitySession>,
    onBack: () -> Unit,
    onSaveSession: (FitnessActivitySession) -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val summary = fitnessLibrarySummary(sessions)
    val spacing = if (compact) 14.dp else 18.dp
    var draft by remember(sessions.size) { mutableStateOf(newFitnessSessionDraft(sessions.size + 1)) }
    var editingSessionId by remember(sessions) { mutableStateOf<String?>(null) }
    var selectedSessionId by remember(sessions) { mutableStateOf(sessions.firstOrNull()?.id) }
    val selectedSession = sessions.firstOrNull { it.id == selectedSessionId } ?: sessions.firstOrNull()

    LaunchedEffect(sessions) {
        if (selectedSessionId == null || sessions.none { it.id == selectedSessionId }) {
            selectedSessionId = sessions.firstOrNull()?.id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Fitness",
            title = "Krachttraining en oefeningen",
            subtitle = "Bewaar je sessies lokaal en bekijk per training welke oefeningen, sets en belasting je hebt gedaan.",
            accent = palette.warm,
            compact = compact,
            sideContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = summary.sessionCount.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "sessies lokaal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        )

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.surfaceRaised,
                contentColor = palette.textPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Terug naar home")
        }

        FitnessSessionComposerCard(
            draft = draft,
            editing = editingSessionId != null,
            onTitleChanged = { draft = updateFitnessDraftTitle(draft, it) },
            onNotesChanged = { draft = updateFitnessDraftNotes(draft, it) },
            onMuscleGroupToggled = { muscleGroup -> draft = toggleFitnessMuscleGroup(draft, muscleGroup) },
            onTemplateToggled = { template -> draft = toggleFitnessExercise(draft, template) },
            onSetCountChanged = { template, delta ->
                draft = updateFitnessExerciseDraft(draft, template) { exercise ->
                    exercise.copy(setCount = (exercise.setCount + delta).coerceAtLeast(1))
                }
            },
            onRepsChanged = { template, delta ->
                draft = updateFitnessExerciseDraft(draft, template) { exercise ->
                    exercise.copy(repsPerSet = (exercise.repsPerSet + delta).coerceAtLeast(1))
                }
            },
            onWeightChanged = { template, delta ->
                draft = updateFitnessExerciseDraft(draft, template) { exercise ->
                    exercise.copy(weightKg = (exercise.weightKg + delta).coerceAtLeast(0))
                }
            },
            onCancelEdit = {
                editingSessionId = null
                draft = newFitnessSessionDraft(sessions.size + 1)
            },
            onSave = {
                val now = currentFitnessEpochMillis()
                val session = buildFitnessSession(
                    draft = draft,
                    sessionId = draft.sessionId ?: "fitness-$now",
                    startedAtEpochMillis = selectedSession
                        ?.takeIf { it.id == editingSessionId }
                        ?.startedAtEpochMillis
                        ?: now,
                    completedAtEpochMillis = now
                )
                onSaveSession(session)
                selectedSessionId = session.id
                editingSessionId = null
                draft = newFitnessSessionDraft(sessions.size + 2)
            }
        )

        HomePanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Overzicht",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${summary.totalExercises} oefeningen opgeslagen in ${summary.sessionCount} sessies",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Totaal volume: ${formatSteps(summary.totalVolumeKg)} kg",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }

        FitnessSessionListCard(
            sessions = sessions,
            selectedSessionId = selectedSessionId,
            onSelectSession = { selectedSessionId = it }
        )

        if (selectedSession != null) {
            SelectedFitnessSessionCard(
                session = selectedSession,
                onEdit = {
                    editingSessionId = selectedSession.id
                    draft = fitnessSessionDraftFromSession(selectedSession)
                }
            )
        }
    }
}

@Composable
private fun FitnessSessionComposerCard(
    draft: FitnessSessionDraft,
    editing: Boolean,
    onTitleChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onMuscleGroupToggled: (FitnessMuscleGroup) -> Unit,
    onTemplateToggled: (FitnessExerciseTemplate) -> Unit,
    onSetCountChanged: (FitnessExerciseTemplate, Int) -> Unit,
    onRepsChanged: (FitnessExerciseTemplate, Int) -> Unit,
    onWeightChanged: (FitnessExerciseTemplate, Int) -> Unit,
    onCancelEdit: () -> Unit,
    onSave: () -> Unit
) {
    val palette = homePalette()
    val availableTemplates = availableExerciseTemplates(draft.selectedMuscleGroups)
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (editing) "Bewerk fitnesssessie" else "Nieuwe fitnesssessie",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Kies oefeningen en stel per oefening sets, reps en gewicht in. Deze sessie wordt lokaal opgeslagen.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = draft.title,
            onValueChange = onTitleChanged,
            label = { Text("Titel") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = draft.notes,
            onValueChange = onNotesChanged,
            label = { Text("Notities") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MuscleGroupBodySelector(
            selectedMuscleGroups = draft.selectedMuscleGroups,
            onToggleMuscleGroup = onMuscleGroupToggled
        )
        Spacer(modifier = Modifier.height(16.dp))
        availableTemplates.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { template ->
                    val selected = draft.exercises.any { it.template == template }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTemplateToggled(template) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) palette.warmSoft else palette.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) palette.warm else palette.surfaceMuted
                        )
                    ) {
                        Text(
                            text = template.label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            color = palette.textPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (draft.exercises.isNotEmpty()) {
            draft.exercises.forEach { exercise ->
                FitnessExerciseDraftRow(
                    exercise = exercise,
                    onSetCountChanged = { delta -> onSetCountChanged(exercise.template, delta) },
                    onRepsChanged = { delta -> onRepsChanged(exercise.template, delta) },
                    onWeightChanged = { delta -> onWeightChanged(exercise.template, delta) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Button(
            onClick = onSave,
            enabled = fitnessDraftCanSave(draft),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.warm,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                if (editing) "Werk lokale fitnesssessie bij" else "Sla fitnesssessie lokaal op",
                fontWeight = FontWeight.SemiBold
            )
        }
        if (editing) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onCancelEdit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.surfaceRaised,
                    contentColor = palette.textPrimary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Annuleer bewerken")
            }
        }
    }
}

@Composable
private fun FitnessExerciseDraftRow(
    exercise: FitnessExerciseDraft,
    onSetCountChanged: (Int) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onWeightChanged: (Int) -> Unit
) {
    val palette = homePalette()
    Surface(
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = exercise.template.label,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DraftMetricControl("Sets", exercise.setCount, onSetCountChanged, modifier = Modifier.weight(1f))
                DraftMetricControl("Reps", exercise.repsPerSet, onRepsChanged, modifier = Modifier.weight(1f))
                DraftMetricControl("Kg", exercise.weightKg, onWeightChanged, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DraftMetricControl(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Surface(
        modifier = modifier,
        color = palette.surface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = palette.textSecondary)
            Text(value.toString(), style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricButton("-", onClick = { onChange(-1) })
                MetricButton("+", onClick = { onChange(1) })
            }
        }
    }
}

@Composable
private fun MetricButton(
    label: String,
    onClick: () -> Unit
) {
    val palette = homePalette()
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
            Text(label, color = palette.textPrimary, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun FitnessSessionListCard(
    sessions: List<FitnessActivitySession>,
    selectedSessionId: String?,
    onSelectSession: (String) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Recente sessies",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (sessions.isEmpty()) {
            Text(
                text = "Nog geen lokale fitnesssessies opgeslagen.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        } else {
            sessions.forEach { session ->
                val selected = session.id == selectedSessionId
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSession(session.id) },
                    color = if (selected) palette.warmSoft else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) palette.warm else palette.surfaceMuted
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${fitnessSessionExerciseCount(session)} oefeningen • ${formatSteps(fitnessSessionVolumeKg(session))} kg volume",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                        Text(
                            text = formatMuscleGroupSummary(session.muscleGroups),
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SelectedFitnessSessionCard(
    session: FitnessActivitySession,
    onEdit: () -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.warm,
                    contentColor = palette.buttonContent
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Bewerk")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatMuscleGroupSummary(session.muscleGroups),
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${fitnessSessionExerciseCount(session)} oefeningen • ${formatSteps(fitnessSessionVolumeKg(session))} kg volume",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        if (session.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = session.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        session.exercises.forEach { exercise ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = palette.surfaceRaised,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${exercise.setCount} sets x ${exercise.repsPerSet} reps",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                    Text(
                        text = "${exercise.weightKg} kg",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
