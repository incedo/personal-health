package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.coaches.CoachProfile
import com.incedo.personalhealth.core.coaches.coachTypeLabel
import com.incedo.personalhealth.core.coaches.selectedCoachProfiles

@Composable
internal fun CoachDirectoryCard(
    coaches: List<CoachProfile>,
    onAddCoach: () -> Unit,
    onEditCoach: (CoachProfile) -> Unit,
    onToggleCoachSelection: (String) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Jouw coaches",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Voeg meerdere coaches toe, kies wie actief meeloopt en houd AI, voeding, training of lifestyle naast elkaar beschikbaar.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddCoach,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Coach toevoegen")
        }
        if (coaches.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nog geen coaches toegevoegd. Start linksboven met de grote plus of voeg hier direct je eerste coach toe.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                coaches.forEach { coach ->
                    CoachDirectoryItem(
                        coach = coach,
                        onEditCoach = { onEditCoach(coach) },
                        onToggleCoachSelection = { onToggleCoachSelection(coach.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun FloatingCoachDock(
    coaches: List<CoachProfile>,
    onAddCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCoaches = remember(coaches) { selectedCoachProfiles(coaches) }
    val palette = homePalette()
    Surface(
        modifier = modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = onAddCoach
        ),
        shadowElevation = 10.dp,
        color = palette.surface,
        shape = RoundedCornerShape(32.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedCoaches.isEmpty()) {
                LargeCoachAddButton(onClick = onAddCoach)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    selectedCoaches.take(3).forEach { coach ->
                        CoachAvatar(coach = coach, size = 52.dp)
                    }
                }
                SmallCoachAddBadge()
            }
        }
    }
}

@Composable
private fun CoachDirectoryItem(
    coach: CoachProfile,
    onEditCoach: () -> Unit,
    onToggleCoachSelection: () -> Unit
) {
    val palette = homePalette()
    Surface(
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoachAvatar(coach = coach, size = 58.dp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = coach.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${coachTypeLabel(coach.type)} · ${coach.location}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onToggleCoachSelection,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (coach.isSelected) palette.warning else palette.surface,
                        contentColor = if (coach.isSelected) palette.buttonContent else palette.textPrimary
                    )
                ) {
                    Text(if (coach.isSelected) "Gekozen" else "Kies coach")
                }
                OutlinedButton(onClick = onEditCoach, shape = RoundedCornerShape(16.dp)) {
                    Text("Bewerken")
                }
            }
        }
    }
}

@Composable
private fun LargeCoachAddButton(
    onClick: () -> Unit
) {
    val palette = homePalette()
    Button(
        onClick = onClick,
        modifier = Modifier.size(88.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.warning,
            contentColor = palette.buttonContent
        )
    ) {
        Text(
            text = "+",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SmallCoachAddBadge() {
    val palette = homePalette()
    Surface(
        color = palette.warning,
        shape = CircleShape
    ) {
        Text(
            text = "+",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.titleMedium,
            color = palette.buttonContent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun CoachAvatar(
    coach: CoachProfile,
    size: androidx.compose.ui.unit.Dp
) {
    val imageBitmap = remember(coach.imageDataUrl) { decodeNutritionPhotoBitmap(coach.imageDataUrl) }
    val palette = homePalette()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (coach.isSelected) palette.warningSoft else palette.accentSoft),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = coach.name,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = coach.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
