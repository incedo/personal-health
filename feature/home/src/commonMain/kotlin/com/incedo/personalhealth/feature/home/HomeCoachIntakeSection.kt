package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.goals.CoachIntakeProfile
import com.incedo.personalhealth.core.goals.CoachProfileTrait
import com.incedo.personalhealth.core.goals.CoachRecommendation
import com.incedo.personalhealth.core.goals.coachFocusLabel
import com.incedo.personalhealth.core.goals.coachProfileTraitLabel
import com.incedo.personalhealth.core.goals.coachProtocolById

@Composable
internal fun CoachIntakeCard(
    onboardingFocusGoal: CoachFocusGoal?,
    intake: CoachIntakeProfile,
    recommendation: CoachRecommendation,
    onFocusSelected: (CoachFocusGoal) -> Unit,
    onTraitToggled: (CoachProfileTrait) -> Unit
) {
    val palette = homePalette()
    val effectiveFocusGoal = recommendation.effectiveFocusGoal
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Coach intake",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Neem de onboarding-focus mee en definieer wat voor type leefstijl of ritme bij iemand past, zodat coach een beter leefprotocol kan voorstellen.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        if (onboardingFocusGoal != null && intake.focusGoal == null) {
            Spacer(modifier = Modifier.height(12.dp))
            CoachProtocolStripe(
                title = "Uit onboarding",
                value = coachFocusLabel(onboardingFocusGoal)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Focusdoel",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CoachFocusGoal.entries.forEach { goal ->
                FilterChip(
                    selected = effectiveFocusGoal == goal,
                    onClick = { onFocusSelected(goal) },
                    label = { Text(coachFocusLabel(goal)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Profieldefinitie",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CoachProfileTrait.entries.chunked(2).forEach { rowTraits ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTraits.forEach { trait ->
                        FilterChip(
                            selected = trait in intake.traits,
                            onClick = { onTraitToggled(trait) },
                            label = { Text(coachProfileTraitLabel(trait)) }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CoachProtocolStripe(
            title = "Voorgesteld protocol",
            value = coachProtocolById(recommendation.protocolId).title
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recommendation.rationale.firstOrNull() ?: "Coach wacht nog op meer intake-informatie.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
    }
}
