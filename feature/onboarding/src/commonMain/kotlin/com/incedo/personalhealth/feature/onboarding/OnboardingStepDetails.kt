package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun OnboardingStepDetails(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state.stepIndex) {
        1 -> GoalSelectorPanel(
            selectedGoal = state.selectedGoal,
            onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) },
            modifier = modifier
        )
        2 -> ProfilePanel(state.profile, onEvent, modifier)
        3 -> ActivityPanel(state.activityLevel, onEvent, modifier)
        4 -> AvailabilityPanel(state.availability, onEvent, modifier)
        5 -> DevicesPanel(state.devices, onEvent, modifier)
        6 -> NutritionPanel(state.nutrition, onEvent, modifier)
        7 -> BaselinePanel(state.baseline, onEvent, modifier)
        else -> OnboardingInfoPanel(
            title = detailTitle(state),
            description = detailDescription(state),
            items = detailItems(state),
            modifier = modifier
        )
    }
}

@Composable
private fun OnboardingInfoPanel(
    title: String,
    description: String,
    items: List<OnboardingDetailItem>,
    modifier: Modifier = Modifier
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing

    PhCard(modifier = modifier, padding = spacing.xl) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(text = title, style = PhTheme.typography.h2, color = colors.text)
            Text(text = description, style = PhTheme.typography.bodySmall, color = colors.textMuted)
            items.forEach { item ->
                DetailRow(item = item)
            }
        }
    }
}

@Composable
private fun DetailRow(item: OnboardingDetailItem) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PhTheme.shapes.lg,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier.padding(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            BadgePill(
                label = item.code,
                background = item.tone.background,
                contentColor = item.tone.content,
                size = 44.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(text = item.title, style = PhTheme.typography.h3, color = colors.text)
                Text(text = item.description, style = PhTheme.typography.bodySmall, color = colors.textMuted)
            }
        }
    }
}

@Composable
private fun detailItems(state: OnboardingUiState): List<OnboardingDetailItem> {
    val colors = PhTheme.colors
    val primary = OnboardingTone(colors.primarySoft, colors.primary)
    val info = OnboardingTone(colors.infoSoft, colors.info)
    val warning = OnboardingTone(colors.warningSoft, colors.warning)
    val success = OnboardingTone(colors.successSoft, colors.success)

    return when (state.stepIndex) {
        0 -> listOf(
            OnboardingDetailItem("HRV", "Understands your body", "HRV, sleep, training and nutrition together.", primary),
            OnboardingDetailItem("ADP", "Plan that adapts", "Not recovered? We scale the day automatically.", info),
            OnboardingDetailItem("PRI", "Privacy first", "You decide what gets connected and shared.", success)
        )
        2 -> listOf(
            OnboardingDetailItem("AGE", "Age", "Used for heart-rate zones and energy estimates.", primary),
            OnboardingDetailItem("HGT", "Height", "Improves body-composition and cardio calculations.", info),
            OnboardingDetailItem("WGT", "Weight", "Sets the baseline for trends and plan calibration.", warning)
        )
        3 -> listOf(
            OnboardingDetailItem("NEW", "Starter", "New or returning after a longer pause.", success),
            OnboardingDetailItem("REG", "Regular", "Training with intent three to four times a week.", primary),
            OnboardingDetailItem("ATH", "Athletic", "Performance-focused with high weekly load.", warning)
        )
        4 -> listOf(
            OnboardingDetailItem("DAYS", "Training days", "Pick realistic days instead of an ideal week.", primary),
            OnboardingDetailItem("HRS", "Weekly hours", "The plan scales around your available time.", info),
            OnboardingDetailItem("FLEX", "Flexible rhythm", "Resize the week without losing your progress.", success)
        )
        5 -> listOf(
            OnboardingDetailItem("KIT", "Wearables", "Apple Health, Health Connect or watch data can enrich the plan.", primary),
            OnboardingDetailItem("MAN", "Manual first", "You can start without connecting a device.", info),
            OnboardingDetailItem("SYNC", "Live sync", "New data will flow into dashboard events later.", success)
        )
        6 -> listOf(
            OnboardingDetailItem("BAL", "Balanced", "Keep nutrition guidance simple and sustainable.", primary),
            OnboardingDetailItem("PRO", "Protein focus", "Support strength and body-composition goals.", success),
            OnboardingDetailItem("REST", "Restrictions", "Account for preferences and foods you avoid.", warning)
        )
        7 -> listOf(
            OnboardingDetailItem("SLP", "Sleep baseline", "Use duration and regularity as recovery signals.", info),
            OnboardingDetailItem("RHR", "Resting heart rate", "Ground the plan in cardiovascular trend data.", primary),
            OnboardingDetailItem("WGT", "Body trend", "Track change over time, not day-to-day noise.", warning)
        )
        else -> listOf(
            OnboardingDetailItem("GOAL", goalLabel(state.selectedGoal ?: OnboardingGoal.Activity), "Your first dashboard emphasis is set.", primary),
            OnboardingDetailItem("PLAN", "Adaptive weekly plan", "The app starts conservative and responds to your signals.", success),
            OnboardingDetailItem("NEXT", "Open dashboard", "Continue into the home experience when you are ready.", info)
        )
    }
}

private fun detailTitle(state: OnboardingUiState): String {
    return when (state.stepIndex) {
        0 -> "Welkom"
        2 -> "Over jou"
        3 -> "Hoe actief ben je?"
        4 -> "Wanneer kun je?"
        5 -> "Welke data wil je koppelen?"
        6 -> "Hoe eet je meestal?"
        7 -> "Je startpunt"
        else -> "Je plan staat klaar"
    }
}

private fun detailDescription(state: OnboardingUiState): String {
    return when (state.stepIndex) {
        0 -> "Een rustige gids voor je gezondheid op de lange termijn, met jouw tempo als uitgangspunt."
        2 -> "Deze basisinfo helpt om berekeningen straks realistischer te maken."
        3 -> "We starten liever iets te haalbaar dan te agressief."
        4 -> "Je beste plan is het plan dat in je week past."
        5 -> "Koppelen kan nu of later; de flow blijft bruikbaar zonder device."
        6 -> "Voeding wordt praktischer als we je voorkeuren kennen."
        7 -> "Een baseline maakt vooruitgang zichtbaar zonder ruis."
        else -> "Dit is de samenvatting voordat je naar het dashboard gaat."
    }
}

private data class OnboardingDetailItem(
    val code: String,
    val title: String,
    val description: String,
    val tone: OnboardingTone
)

private data class OnboardingTone(
    val background: Color,
    val content: Color
)
