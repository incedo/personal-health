package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
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
import com.incedo.personalhealth.core.coaches.CoachProfile
import com.incedo.personalhealth.core.coaches.CoachSearchItem
import com.incedo.personalhealth.core.coaches.coachTypeLabel

@Composable
internal fun CoachEditorScreen(
    compact: Boolean,
    editorState: CoachEditorState,
    searchResults: List<CoachSearchItem>,
    onBack: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchResultSelected: (CoachSearchItem) -> Unit,
    onNameChanged: (String) -> Unit,
    onCompanyNameChanged: (String) -> Unit,
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
                subtitle = "Zoek eerst een coach op naam, bedrijfsnaam of type. Daarna kun je de details nog aanpassen voordat de coach lokaal wordt opgeslagen.",
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
                        enabled = editorState.name.isNotBlank(),
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
                    text = "Coach zoeken",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                CoachSearchSection(
                    query = editorState.searchQuery,
                    selectedItemId = editorState.selectedSearchItemId,
                    results = searchResults,
                    onQueryChanged = onSearchQueryChanged,
                    onSelectItem = onSearchResultSelected
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
                    value = editorState.companyName,
                    onValueChange = onCompanyNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Bedrijfsnaam") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = coachEditorFieldColors()
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = editorState.location,
                    onValueChange = onLocationChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Locatie (optioneel)") },
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
                        coach = CoachProfile(
                            id = editorState.editingCoachId ?: "draft",
                            type = editorState.selectedType,
                            name = editorState.name.ifBlank { "Coach" },
                            companyName = editorState.companyName,
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
internal fun coachEditorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = homePalette().warning,
    focusedLabelColor = homePalette().warning,
    cursorColor = homePalette().warning
)
