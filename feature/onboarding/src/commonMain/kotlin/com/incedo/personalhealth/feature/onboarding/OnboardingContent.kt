package com.incedo.personalhealth.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.incedo.personalhealth.core.designsystem.PhTheme

internal fun progressText(state: OnboardingUiState): String {
    return when (state.stepIndex) {
        0 -> "Profile setup"
        1 -> "Insight preview"
        else -> "Goal selection"
    }
}

@Composable
internal fun stepVisualFor(stepIndex: Int): StepVisual {
    val colors = PhTheme.colors
    return when (stepIndex) {
        0 -> StepVisual(
            kicker = "01",
            eyebrow = "Personal setup",
            detailPoints = listOf(
                "Start with a calm, guided flow that works on phone, tablet and desktop.",
                "We keep onboarding lightweight so users reach the dashboard fast.",
                "The same route remains valid when layout density changes."
            ),
            accent = colors.success,
            accentSurface = colors.successSoft
        )

        1 -> StepVisual(
            kicker = "02",
            eyebrow = "Clear signals",
            detailPoints = listOf(
                "Show activity, sleep and health sync as one readable story.",
                "Prioritize key metrics instead of scattering technical modules.",
                "Use visual grouping so sections feel intentional, not improvised."
            ),
            accent = colors.warning,
            accentSurface = colors.warningSoft
        )

        else -> StepVisual(
            kicker = "03",
            eyebrow = "Guided focus",
            detailPoints = listOf(
                "Choose what matters first and let the app adapt the emphasis.",
                "Keep this as a preference, not a permanent lock-in.",
                "Move secondary preferences to profile settings after onboarding."
            ),
            accent = colors.info,
            accentSurface = colors.infoSoft
        )
    }
}

@Composable
internal fun goalAccent(goal: OnboardingGoal): Color {
    return when (goal) {
        OnboardingGoal.Activity -> PhTheme.colors.success
        OnboardingGoal.BetterSleep -> PhTheme.colors.info
        OnboardingGoal.Nutrition -> PhTheme.colors.warning
    }
}

@Composable
internal fun goalAccentSoft(goal: OnboardingGoal): Color {
    return when (goal) {
        OnboardingGoal.Activity -> PhTheme.colors.successSoft
        OnboardingGoal.BetterSleep -> PhTheme.colors.infoSoft
        OnboardingGoal.Nutrition -> PhTheme.colors.warningSoft
    }
}

internal fun goalLabel(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "Increase activity"
        OnboardingGoal.BetterSleep -> "Sleep better"
        OnboardingGoal.Nutrition -> "Improve nutrition"
    }
}

internal fun goalDescription(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "Highlight movement, trends and quick daily actions."
        OnboardingGoal.BetterSleep -> "Surface recovery signals and better nightly routines."
        OnboardingGoal.Nutrition -> "Bring food logging and energy balance forward."
    }
}

internal fun goalCode(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "ACT"
        OnboardingGoal.BetterSleep -> "SLP"
        OnboardingGoal.Nutrition -> "NTR"
    }
}
