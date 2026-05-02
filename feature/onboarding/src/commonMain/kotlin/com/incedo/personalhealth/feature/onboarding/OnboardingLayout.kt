package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun CompactOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    val spacing = PhTheme.spacing

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.lg, vertical = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        OnboardingHero(
            state = state,
            step = currentStep,
            stepVisual = currentVisual,
            compact = true
        )
        OnboardingStepDetails(
            state = state,
            onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) }
        )
        FooterActions(state = state, onEvent = onEvent)
    }
}

@Composable
internal fun MediumOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    val spacing = PhTheme.spacing

    Row(
        modifier = modifier.padding(spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        StepRail(
            state = state,
            modifier = Modifier
                .weight(0.34f)
                .fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .weight(0.66f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            OnboardingHero(
                state = state,
                step = currentStep,
                stepVisual = currentVisual,
                compact = false
            )
            OnboardingStepDetails(
                state = state,
                onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) }
            )
            FooterActions(state = state, onEvent = onEvent)
        }
    }
}

@Composable
internal fun ExpandedOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    val spacing = PhTheme.spacing

    Row(
        modifier = modifier.padding(spacing.xxl),
        horizontalArrangement = Arrangement.spacedBy(spacing.xl)
    ) {
        StepRail(
            state = state,
            modifier = Modifier
                .weight(0.24f)
                .fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .weight(0.46f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            OnboardingHero(
                state = state,
                step = currentStep,
                stepVisual = currentVisual,
                compact = false
            )
            FooterActions(state = state, onEvent = onEvent)
        }
        OnboardingStepDetails(
            state = state,
            onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) },
            modifier = Modifier
                .weight(0.30f)
                .fillMaxHeight()
                .fillMaxWidth()
        )
    }
}
