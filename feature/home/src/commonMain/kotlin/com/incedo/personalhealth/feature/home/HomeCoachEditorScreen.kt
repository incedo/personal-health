package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.coaches.CoachType
import com.incedo.personalhealth.core.coaches.coachTypeLabel

@Composable
internal fun CoachEditorScreen(
    compact: Boolean,
    editorState: CoachEditorState,
    onBack: () -> Unit,
    onTypeSelected: (CoachType) -> Unit,
    onNameChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onPickImage: () -> Unit,
    onSave: () -> Unit
) {
    val palette = homePalette()
    HomeSectionScreen(
        tab = HomeTab.COACH,
        compact = compact,
        leadingContent = {
            HomeHeroCard(
                eyebrow = "Coach",
                title = if (editorState.editingCoachId == null) "Coach toevoegen" else "Coach bewerken",
                subtitle = "Voeg een naam, locatie, type en afbeelding toe. De coach komt daarna direct terug in je coachdirectory en kan actief worden gekozen.",
                accent = palette.warning,
                compact = compact,
                sideContent = {
                    HomeStatusBadge(
                        label = "Type",
                        value = coachTypeLabel(editorState.selectedType)
                    )
                }
            )
        },
        bodyContent = {
            HomePanel(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                        Text("Terug")
                    }
                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(16.dp),
                        enabled = editorState.name.isNotBlank() && editorState.location.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.warning,
                            contentColor = palette.buttonContent
                        )
                    ) {
                        Text("Opslaan")
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Type coach",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                CoachTypePicker(
                    selectedType = editorState.selectedType,
                    onTypeSelected = onTypeSelected
                )
                Spacer(modifier = Modifier.height(18.dp))
                OutlinedTextField(
                    value = editorState.name,
                    onValueChange = onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Naam") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = coachEditorFieldColors()
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = editorState.location,
                    onValueChange = onLocationChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Locatie") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = coachEditorFieldColors()
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Afbeelding",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (editorState.imageDataUrl != null) {
                    CoachAvatar(
                        coach = com.incedo.personalhealth.core.coaches.CoachProfile(
                            id = editorState.editingCoachId ?: "draft",
                            type = editorState.selectedType,
                            name = editorState.name.ifBlank { "Coach" },
                            location = editorState.location,
                            imageDataUrl = editorState.imageDataUrl
                        ),
                        size = 92.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Button(
                    onClick = onPickImage,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.buttonContent
                    )
                ) {
                    Text(if (editorState.imageDataUrl == null) "Afbeelding kiezen" else "Afbeelding vervangen")
                }
            }
        }
    )
}

@Composable
private fun CoachTypePicker(
    selectedType: CoachType,
    onTypeSelected: (CoachType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CoachType.entries.forEach { type ->
            OutlinedButton(
                onClick = { onTypeSelected(type) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (type == selectedType) "${coachTypeLabel(type)} · gekozen" else coachTypeLabel(type)
                )
            }
        }
    }
}

@Composable
private fun coachEditorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = homePalette().warning,
    focusedLabelColor = homePalette().warning,
    cursorColor = homePalette().warning
)
