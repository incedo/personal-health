package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhChoiceCard
import com.incedo.personalhealth.core.designsystem.PhTheme

internal data class StepVisual(
    val kicker: String,
    val eyebrow: String,
    val detailPoints: List<String>,
    val accent: Color,
    val accentSurface: Color
)

@Composable
internal fun OnboardingHero(
    state: OnboardingUiState,
    step: OnboardingStep,
    stepVisual: StepVisual,
    compact: Boolean
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing
    val progress = (state.stepIndex + 1) / onboardingSteps.size.toFloat()

    PhCard(padding = 0.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(stepVisual.accentSurface, colors.surface)
                    )
                )
                .padding(
                    horizontal = if (compact) spacing.xl else spacing.xxxl,
                    vertical = if (compact) spacing.xxl else spacing.xxxl
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                BadgePill(
                    label = stepVisual.kicker,
                    background = stepVisual.accentSurface,
                    contentColor = stepVisual.accent,
                    size = if (compact) 56.dp else 64.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    Text(text = stepVisual.eyebrow, style = PhTheme.typography.label, color = stepVisual.accent)
                    Text(
                        text = step.title,
                        style = if (compact) PhTheme.typography.h1 else PhTheme.typography.display,
                        color = colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = step.description, style = PhTheme.typography.body, color = colors.textMuted)
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(PhTheme.shapes.pill),
                color = stepVisual.accent,
                trackColor = colors.surfaceSunken
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step ${state.stepIndex + 1} of ${onboardingSteps.size}",
                    style = PhTheme.typography.bodySmall,
                    color = colors.textMuted
                )
                Text(text = progressText(state), style = PhTheme.typography.label, color = stepVisual.accent)
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                stepVisual.detailPoints.forEach { point ->
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(stepVisual.accent)
                        )
                        Text(text = point, style = PhTheme.typography.bodySmall, color = colors.text)
                    }
                }
            }
        }
    }
}

@Composable
internal fun StepRail(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing

    PhCard(modifier = modifier, padding = spacing.lg) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(text = "Setup flow", style = PhTheme.typography.h2, color = colors.text)
            Text(
                text = "Keep the same route model, only adapt the layout density.",
                style = PhTheme.typography.bodySmall,
                color = colors.textMuted
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            onboardingSteps.forEachIndexed { index, step ->
                StepRailItem(index = index, step = step, selected = index == state.stepIndex)
            }
        }
    }
}

@Composable
private fun StepRailItem(
    index: Int,
    step: OnboardingStep,
    selected: Boolean
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing
    val visual = stepVisualFor(index)

    Surface(
        shape = PhTheme.shapes.lg,
        color = if (selected) visual.accentSurface else colors.surfaceMuted,
        border = BorderStroke(1.dp, if (selected) visual.accent else colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                BadgePill(
                    label = "${index + 1}",
                    background = if (selected) visual.accent else colors.surfaceSunken,
                    contentColor = if (selected) colors.onPrimary else colors.text,
                    size = 36.dp
                )
                Text(text = step.title, style = PhTheme.typography.h3, color = colors.text)
            }
            Text(text = step.description, style = PhTheme.typography.caption, color = colors.textMuted)
        }
    }
}

@Composable
internal fun GoalSelector(
    selectedGoal: OnboardingGoal?,
    onGoalSelected: (OnboardingGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    GoalSelectorPanel(
        selectedGoal = selectedGoal,
        onGoalSelected = onGoalSelected,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
internal fun GoalSelectorPanel(
    selectedGoal: OnboardingGoal?,
    onGoalSelected: (OnboardingGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing

    PhCard(modifier = modifier, padding = spacing.xl) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(text = "Choose your first focus", style = PhTheme.typography.h2, color = colors.text)
            Text(
                text = "This only affects what we highlight first. You can change it later in profile settings.",
                style = PhTheme.typography.bodySmall,
                color = colors.textMuted
            )
            OnboardingGoal.entries.forEach { goal ->
                GoalOption(goal = goal, selected = selectedGoal == goal, onClick = { onGoalSelected(goal) })
            }
        }
    }
}

@Composable
private fun GoalOption(
    goal: OnboardingGoal,
    selected: Boolean,
    onClick: () -> Unit
) {
    PhChoiceCard(
        selected = selected,
        onClick = onClick,
        leading = {
            BadgePill(
                label = goalCode(goal),
                background = goalAccentSoft(goal),
                contentColor = goalAccent(goal),
                size = 44.dp
            )
        },
        title = goalLabel(goal),
        description = goalDescription(goal),
        accent = goalAccent(goal),
        selectedBackground = goalAccentSoft(goal)
    )
}
