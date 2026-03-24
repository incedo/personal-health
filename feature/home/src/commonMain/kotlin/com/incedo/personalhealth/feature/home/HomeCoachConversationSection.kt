package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachIntakeProfile
import com.incedo.personalhealth.core.goals.CoachRecommendation
import com.incedo.personalhealth.core.goals.coachFocusLabel
import com.incedo.personalhealth.core.goals.coachProfileTraitLabel
import com.incedo.personalhealth.core.goals.coachProtocolById

@Composable
internal fun CoachConversationCard(
    onboardingFocusText: String?,
    intakeProfile: CoachIntakeProfile,
    recommendation: CoachRecommendation
) {
    val palette = homePalette()
    val protocol = coachProtocolById(recommendation.protocolId)
    val currentFocusText = recommendation.effectiveFocusGoal?.let(::coachFocusLabel)
        ?: "Nog geen focus gekozen"
    val profileText = intakeProfile.traits.takeIf { it.isNotEmpty() }
        ?.joinToString { coachProfileTraitLabel(it) }
        ?: "Nog geen profielaccent gekozen"

    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Coach gesprek",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coach combineert onboarding en intake tot een eerste, uitlegbaar voorstel. Dat model is al klaar voor een latere AI-laag, maar draait nu nog volledig deterministisch.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        ConversationBubble(
            speaker = "Coach",
            body = "Waar wil je primair naartoe werken?"
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConversationBubble(
            speaker = "Jij",
            body = onboardingFocusText?.let { "Onboarding-focus: $it. Huidige richting: $currentFocusText." }
                ?: currentFocusText,
            emphasized = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConversationBubble(
            speaker = "Coach",
            body = "Hoe wil je leven en waarop mag ik sturen?"
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConversationBubble(
            speaker = "Jij",
            body = profileText,
            emphasized = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        CoachProtocolStripe(
            title = "Coach stelt voor",
            value = protocol.title
        )
    }
}

@Composable
private fun ConversationBubble(
    speaker: String,
    body: String,
    emphasized: Boolean = false
) {
    val palette = homePalette()
    Surface(
        color = if (emphasized) palette.warningSoft else palette.surface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = speaker,
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textPrimary
            )
        }
    }
}
