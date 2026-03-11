package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingRoute(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by rememberSaveable { mutableStateOf(OnboardingUiState()) }

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
    val currentStep = onboardingSteps[state.stepIndex]

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                    onEvent(OnboardingEvent.Next)
                    true
                } else {
                    false
                }
            }
    ) {
        val layoutClass = classifyLayout(maxWidth.value)

        when (layoutClass) {
            OnboardingLayoutClass.Compact -> CompactOnboardingLayout(
                state = state,
                currentStep = currentStep,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
            OnboardingLayoutClass.Medium -> MediumOnboardingLayout(
                state = state,
                currentStep = currentStep,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize().padding(20.dp)
            )
            OnboardingLayoutClass.Expanded -> ExpandedOnboardingLayout(
                state = state,
                currentStep = currentStep,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize().padding(24.dp)
            )
        }
    }
}

@Composable
private fun CompactOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(state = state)
        StepCard(step = currentStep)
        GoalSelector(
            selectedGoal = state.selectedGoal,
            onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) }
        )
        FooterActions(state = state, onEvent = onEvent)
    }
}

@Composable
private fun MediumOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepRail(
            state = state,
            modifier = Modifier.weight(0.35f).fillMaxHeight()
        )
        Column(
            modifier = Modifier.weight(0.65f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepCard(step = currentStep)
            GoalSelector(
                selectedGoal = state.selectedGoal,
                onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) }
            )
            FooterActions(state = state, onEvent = onEvent)
        }
    }
}

@Composable
private fun ExpandedOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StepRail(
            state = state,
            modifier = Modifier.weight(0.25f).fillMaxHeight()
        )
        StepCard(
            step = currentStep,
            modifier = Modifier.weight(0.45f).fillMaxHeight()
        )
        Column(
            modifier = Modifier.weight(0.30f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GoalSelector(
                selectedGoal = state.selectedGoal,
                onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) },
                modifier = Modifier.weight(1f)
            )
            FooterActions(state = state, onEvent = onEvent)
        }
    }
}

@Composable
private fun Header(state: OnboardingUiState) {
    val progress = (state.stepIndex + 1) / onboardingSteps.size.toFloat()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Set up your health dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Step ${state.stepIndex + 1} of ${onboardingSteps.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StepRail(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Onboarding",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            onboardingSteps.forEachIndexed { index, step ->
                val selected = index == state.stepIndex
                val containerColor = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                val contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = containerColor
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${index + 1}. ${step.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = contentColor
                        )
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(
    step: OnboardingStep,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GoalSelector(
    selectedGoal: OnboardingGoal?,
    onGoalSelected: (OnboardingGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "What do you want to improve first?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        OnboardingGoal.entries.forEach { goal ->
            GoalOption(
                goal = goal,
                selected = selectedGoal == goal,
                onClick = { onGoalSelected(goal) }
            )
        }
    }
}

@Composable
private fun GoalOption(
    goal: OnboardingGoal,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        selected -> MaterialTheme.colorScheme.secondaryContainer
        hovered -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null
            )
            .focusable(interactionSource = interactionSource),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        tonalElevation = if (selected) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = goalLabel(goal),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun FooterActions(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val isLastStep = state.stepIndex == onboardingSteps.lastIndex

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = { onEvent(OnboardingEvent.Back) },
            enabled = state.stepIndex > 0,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
        ) {
            Text("Back")
        }

        OutlinedButton(
            onClick = { onEvent(OnboardingEvent.Skip) },
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
        ) {
            Text("Skip")
        }

        Button(
            onClick = {
                if (isLastStep) {
                    onEvent(OnboardingEvent.Finish)
                } else {
                    onEvent(OnboardingEvent.Next)
                }
            },
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
        ) {
            Text(if (isLastStep) "Start" else "Next")
        }
    }
}

private fun goalLabel(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "Increase activity"
        OnboardingGoal.BetterSleep -> "Sleep better"
        OnboardingGoal.Nutrition -> "Improve nutrition"
    }
}
