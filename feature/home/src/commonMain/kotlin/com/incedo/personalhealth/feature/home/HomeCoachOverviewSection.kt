package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachGoal
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachRecommendation

@Composable
internal fun CoachOverviewContent(
    recommendation: CoachRecommendation,
    selectedProtocol: CoachProtocol,
    goals: List<CoachGoal>,
    onOpenIntake: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenDetails: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenLogbook: () -> Unit,
    onOpenProfile: () -> Unit
) {
    CoachProtocolCard(
        selectedProtocol = selectedProtocol,
        supportTabs = recommendation.supportTabs,
        onProtocolSelected = {},
        onOpenDashboard = onOpenDashboard,
        onOpenLogbook = onOpenLogbook,
        onOpenProfile = onOpenProfile,
        selectionEnabled = false
    )
    Spacer(modifier = Modifier.height(18.dp))
    CoachGoalsOverviewCard(goals = goals)
    Spacer(modifier = Modifier.height(18.dp))
    CoachActionsCard(
        onOpenIntake = onOpenIntake,
        onOpenGoals = onOpenGoals,
        onOpenDetails = onOpenDetails
    )
}

@Composable
private fun CoachActionsCard(
    onOpenIntake: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenDetails: () -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Acties",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Werk de intake bij, stuur je doelen bij of open persoonlijke details zoals je gekozen coaches.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CoachActionButton(
                title = "Intake openen",
                subtitle = "Werk focus, profieldefinitie en voorstel bij.",
                primary = true,
                onClick = onOpenIntake
            )
            CoachActionButton(
                title = "Persoonlijke doelen",
                subtitle = "Bekijk of voeg doelen toe in een eigen scherm.",
                onClick = onOpenGoals
            )
            CoachActionButton(
                title = "Persoonlijke details",
                subtitle = "Beheer je coachkeuzes en andere persoonlijke accenten.",
                onClick = onOpenDetails
            )
        }
    }
}

@Composable
private fun CoachActionButton(
    title: String,
    subtitle: String,
    primary: Boolean = false,
    onClick: () -> Unit
) {
    val palette = homePalette()
    val shape = RoundedCornerShape(18.dp)
    if (primary) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.warning,
                contentColor = palette.buttonContent
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = shape
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
