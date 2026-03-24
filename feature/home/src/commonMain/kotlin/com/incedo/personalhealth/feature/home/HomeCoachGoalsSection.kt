package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachGoal
import com.incedo.personalhealth.core.goals.suggestedCoachGoals

@Composable
internal fun CoachGoalsOverviewCard(
    goals: List<CoachGoal>
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Jouw doelen",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coach houdt hier je actieve doelen bij, zodat je direct ziet waar je vandaag of deze week op stuurt.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        goals.forEachIndexed { index, goal ->
            CoachGoalCard(goal = goal)
            if (index != goals.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
internal fun CoachGoalAddCard(
    draftTitle: String,
    onDraftChanged: (String) -> Unit,
    onAddGoal: () -> Unit,
    onAddSuggestion: (CoachGoal) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Doel toevoegen",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Voeg een eigen doel toe of kies een snelle suggestie om coach meteen bruikbaar te maken.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = draftTitle,
            onValueChange = onDraftChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nieuw doel") },
            placeholder = { Text("Bijv. 3 keer krachttraining deze week") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.warning,
                focusedLabelColor = palette.warning,
                cursorColor = palette.warning
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAddGoal,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.warning,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Doel toevoegen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            suggestedCoachGoals.forEach { suggestion ->
                OutlinedButton(
                    onClick = { onAddSuggestion(suggestion) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(suggestion.title)
                }
            }
        }
    }
}

@Composable
private fun CoachGoalCard(
    goal: CoachGoal
) {
    val palette = homePalette()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CoachGoalTag(label = goal.cadence)
                CoachGoalTag(label = goal.focus)
            }
        }
    }
}

@Composable
private fun CoachGoalTag(
    label: String
) {
    val palette = homePalette()
    Surface(
        color = palette.warningSoft,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = palette.textPrimary
        )
    }
}
