package com.incedo.personalhealth.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.incedo.personalhealth.core.designsystem.PhTheme

internal fun progressText(state: OnboardingUiState): String {
    return when (state.stepIndex) {
        0 -> "Welcome"
        1 -> "Goals"
        2 -> "Profile basics"
        3 -> "Activity level"
        4 -> "Availability"
        5 -> "Devices"
        6 -> "Nutrition"
        7 -> "Baseline"
        else -> "Ready"
    }
}

@Composable
internal fun stepVisualFor(stepIndex: Int): StepVisual {
    val colors = PhTheme.colors
    return when (stepIndex) {
        0 -> StepVisual(
            kicker = "01",
            eyebrow = "Welcome",
            detailPoints = listOf(
                "A calm, personal guide for long-term health.",
                "Training, sleep, nutrition and recovery in one flow.",
                "Privacy stays explicit before any device connection."
            ),
            accent = colors.success,
            accentSurface = colors.successSoft
        )

        1 -> StepVisual(
            kicker = "02",
            eyebrow = "Goal direction",
            detailPoints = listOf(
                "Pick the first dashboard emphasis.",
                "Keep it changeable after onboarding.",
                "Use goals to shape plan intensity and content."
            ),
            accent = colors.primary,
            accentSurface = colors.primarySoft
        )

        2 -> StepVisual(
            kicker = "03",
            eyebrow = "Profile basics",
            detailPoints = listOf(
                "Age, height and weight improve estimates.",
                "The app can calculate ranges without exposing raw provider records.",
                "These fields can move to profile settings later."
            ),
            accent = colors.warning,
            accentSurface = colors.warningSoft
        )

        3 -> StepVisual(
            kicker = "04",
            eyebrow = "Current level",
            detailPoints = listOf(
                "Beginner through athletic starting points.",
                "Recommendations start realistic and progress gradually.",
                "Load can adapt when recovery drops."
            ),
            accent = colors.info,
            accentSurface = colors.infoSoft
        )

        4 -> StepVisual(
            kicker = "05",
            eyebrow = "Weekly rhythm",
            detailPoints = listOf(
                "Choose days and available hours.",
                "The plan should fit real life, not an ideal calendar.",
                "Compact and expanded layouts keep the same state."
            ),
            accent = colors.success,
            accentSurface = colors.successSoft
        )

        5 -> StepVisual(
            kicker = "06",
            eyebrow = "Device data",
            detailPoints = listOf(
                "Connect wearables when ready.",
                "Manual entry remains available.",
                "Provider data maps to the canonical health model."
            ),
            accent = colors.primary,
            accentSurface = colors.primarySoft
        )

        6 -> StepVisual(
            kicker = "07",
            eyebrow = "Nutrition style",
            detailPoints = listOf(
                "Capture preferences and restrictions.",
                "Keep nutrition support practical and lightweight.",
                "Later screens can add richer logging."
            ),
            accent = colors.warning,
            accentSurface = colors.warningSoft
        )

        7 -> StepVisual(
            kicker = "08",
            eyebrow = "Baseline",
            detailPoints = listOf(
                "Starting signals make progress visible.",
                "Sleep, resting heart rate and weight are enough to begin.",
                "Noise is reduced by looking at trends."
            ),
            accent = colors.info,
            accentSurface = colors.infoSoft
        )

        else -> StepVisual(
            kicker = "09",
            eyebrow = "Ready",
            detailPoints = listOf(
                "Your first focus is ready for the dashboard.",
                "The weekly plan starts conservative.",
                "You can refine everything from profile settings."
            ),
            accent = colors.success,
            accentSurface = colors.successSoft
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
