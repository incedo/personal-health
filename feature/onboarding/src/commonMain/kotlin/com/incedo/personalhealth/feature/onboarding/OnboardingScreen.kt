package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
fun OnboardingRoute(
    initialState: OnboardingUiState = OnboardingUiState(),
    onStateChanged: (OnboardingUiState) -> Unit = {},
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by rememberSaveable(stateSaver = OnboardingUiStateSaver) { mutableStateOf(initialState) }

    LaunchedEffect(state) {
        onStateChanged(state)
    }

    LaunchedEffect(state.completed) {
        if (state.completed) {
            onFinished()
        }
    }

    OnboardingScreen(
        state = state,
        onEvent = { event -> state = reduceOnboardingState(state, event) },
        modifier = modifier
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PhTheme.colors
    val currentStep = onboardingSteps[state.stepIndex]
    val currentVisual = stepVisualFor(stepIndex = state.stepIndex)
    val primaryAction = if (state.stepIndex == onboardingSteps.lastIndex) {
        OnboardingEvent.Finish
    } else {
        OnboardingEvent.Next
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.backgroundSoft, colors.background, colors.backgroundSoft)
                )
            )
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                    onEvent(primaryAction)
                    true
                } else {
                    false
                }
            }
    ) {
        when (classifyLayout(maxWidth.value)) {
            OnboardingLayoutClass.Compact -> CompactOnboardingLayout(
                state = state,
                currentStep = currentStep,
                currentVisual = currentVisual,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize()
            )

            OnboardingLayoutClass.Medium -> MediumOnboardingLayout(
                state = state,
                currentStep = currentStep,
                currentVisual = currentVisual,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize()
            )

            OnboardingLayoutClass.Expanded -> ExpandedOnboardingLayout(
                state = state,
                currentStep = currentStep,
                currentVisual = currentVisual,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
