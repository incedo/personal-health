package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonSize
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun FooterActions(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val spacing = PhTheme.spacing
    val isLastStep = state.stepIndex == onboardingSteps.lastIndex

    PhCard(padding = spacing.lg) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            PhButton(
                text = "Back",
                onClick = { onEvent(OnboardingEvent.Back) },
                enabled = state.stepIndex > 0,
                variant = PhButtonVariant.Outline,
                size = PhButtonSize.Large,
                modifier = Modifier.weight(1f)
            )
            PhButton(
                text = "Skip",
                onClick = { onEvent(OnboardingEvent.Skip) },
                variant = PhButtonVariant.Ghost,
                size = PhButtonSize.Large,
                modifier = Modifier.weight(1f)
            )
            PhButton(
                text = if (isLastStep) "Start dashboard" else "Continue",
                onClick = {
                    if (isLastStep) {
                        onEvent(OnboardingEvent.Finish)
                    } else {
                        onEvent(OnboardingEvent.Next)
                    }
                },
                size = PhButtonSize.Large,
                modifier = Modifier.weight(1.35f)
            )
        }
    }
}

@Composable
internal fun BadgePill(
    label: String,
    background: Color,
    contentColor: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(PhTheme.shapes.lg)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            style = PhTheme.typography.label,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
