package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val OnboardingBackground = Color(0xFF071A29)
private val OnboardingSurface = Color(0xFF10293D)
private val OnboardingSurfaceRaised = Color(0xFF14334A)
private val OnboardingOutline = Color(0xFF24506D)
private val OnboardingTextPrimary = Color(0xFFF4F7FB)
private val OnboardingTextSecondary = Color(0xFF94A9BC)
private val OnboardingAccent = Color(0xFF18C2B7)
private val OnboardingAccentSoft = Color(0xFF133F4B)
private val OnboardingAccentStrong = Color(0xFF2BE390)
private val OnboardingWarm = Color(0xFFF7C948)
private val OnboardingWarmSoft = Color(0xFF413517)

private data class StepVisual(
    val kicker: String,
    val eyebrow: String,
    val detailPoints: List<String>,
    val accent: Color,
    val accentSurface: Color
)

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
    val currentStep = onboardingSteps[state.stepIndex]
    val currentVisual = stepVisualFor(stepIndex = state.stepIndex)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(OnboardingBackground, Color(0xFF0B2234), OnboardingBackground)
                )
            )
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

@Composable
private fun CompactOnboardingLayout(
    state: OnboardingUiState,
    currentStep: OnboardingStep,
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OnboardingHero(
            state = state,
            step = currentStep,
            stepVisual = currentVisual,
            compact = true
        )
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
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier.padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingHero(
                state = state,
                step = currentStep,
                stepVisual = currentVisual,
                compact = false
            )
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
    currentVisual: StepVisual,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OnboardingHero(
                state = state,
                step = currentStep,
                stepVisual = currentVisual,
                compact = false
            )
            FooterActions(state = state, onEvent = onEvent)
        }
        GoalSelectorPanel(
            selectedGoal = state.selectedGoal,
            onGoalSelected = { onEvent(OnboardingEvent.GoalSelected(it)) },
            modifier = Modifier
                .weight(0.30f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun OnboardingHero(
    state: OnboardingUiState,
    step: OnboardingStep,
    stepVisual: StepVisual,
    compact: Boolean
) {
    val progress = (state.stepIndex + 1) / onboardingSteps.size.toFloat()

    Card(
        shape = RoundedCornerShape(if (compact) 24.dp else 28.dp),
        colors = CardDefaults.cardColors(containerColor = OnboardingSurface),
        border = BorderStroke(1.dp, OnboardingOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(stepVisual.accentSurface, OnboardingSurface)
                    )
                )
                .padding(horizontal = if (compact) 20.dp else 28.dp, vertical = if (compact) 22.dp else 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BadgePill(
                    label = stepVisual.kicker,
                    background = stepVisual.accentSurface,
                    contentColor = stepVisual.accent,
                    size = if (compact) 58.dp else 68.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stepVisual.eyebrow,
                        style = MaterialTheme.typography.labelLarge,
                        color = stepVisual.accent
                    )
                    Text(
                        text = step.title,
                        style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                        color = OnboardingTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnboardingTextSecondary
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = stepVisual.accent,
                trackColor = OnboardingOutline
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step ${state.stepIndex + 1} of ${onboardingSteps.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnboardingTextSecondary
                )
                Text(
                    text = progressText(state = state),
                    style = MaterialTheme.typography.labelLarge,
                    color = stepVisual.accent
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                stepVisual.detailPoints.forEach { point ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(stepVisual.accent)
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnboardingTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepRail(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = OnboardingSurface),
        border = BorderStroke(1.dp, OnboardingOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Setup flow",
                style = MaterialTheme.typography.titleLarge,
                color = OnboardingTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Keep the same route model, only adapt the layout density.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            onboardingSteps.forEachIndexed { index, step ->
                val selected = index == state.stepIndex
                val visual = stepVisualFor(stepIndex = index)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selected) visual.accentSurface else OnboardingSurfaceRaised,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) visual.accent else OnboardingOutline
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BadgePill(
                                label = "${index + 1}",
                                background = if (selected) visual.accent else OnboardingOutline,
                                contentColor = if (selected) OnboardingBackground else OnboardingTextPrimary,
                                size = 36.dp
                            )
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = OnboardingTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnboardingTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalSelector(
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
private fun GoalSelectorPanel(
    selectedGoal: OnboardingGoal?,
    onGoalSelected: (OnboardingGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = OnboardingSurface),
        border = BorderStroke(1.dp, OnboardingOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose your first focus",
                style = MaterialTheme.typography.headlineSmall,
                color = OnboardingTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "This only affects what we highlight first. You can change it later in profile settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnboardingTextSecondary
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
}

@Composable
private fun GoalOption(
    goal: OnboardingGoal,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val accent = goalAccent(goal)

    val backgroundColor = when {
        selected -> accent.copy(alpha = 0.16f)
        hovered -> OnboardingSurfaceRaised
        else -> OnboardingSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null
            )
            .focusable(interactionSource = interactionSource),
        shape = RoundedCornerShape(22.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) accent else OnboardingOutline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgePill(
                label = goalCode(goal),
                background = accent.copy(alpha = 0.18f),
                contentColor = accent,
                size = 44.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = goalLabel(goal),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnboardingTextPrimary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
                Text(
                    text = goalDescription(goal),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnboardingTextSecondary
                )
            }
            SelectionIndicator(selected = selected, accent = accent)
        }
    }
}

@Composable
private fun SelectionIndicator(
    selected: Boolean,
    accent: Color
) {
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = if (selected) accent else Color.Transparent,
        border = BorderStroke(2.dp, if (selected) accent else OnboardingTextSecondary)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(OnboardingBackground)
                )
            }
        }
    }
}

@Composable
private fun FooterActions(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val isLastStep = state.stepIndex == onboardingSteps.lastIndex

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = OnboardingSurface),
        border = BorderStroke(1.dp, OnboardingOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onEvent(OnboardingEvent.Back) },
                enabled = state.stepIndex > 0,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnboardingTextPrimary),
                border = BorderStroke(1.dp, OnboardingOutline)
            ) {
                Text("Back")
            }

            OutlinedButton(
                onClick = { onEvent(OnboardingEvent.Skip) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnboardingTextSecondary),
                border = BorderStroke(1.dp, OnboardingOutline)
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
                modifier = Modifier
                    .weight(1.35f)
                    .heightIn(min = 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnboardingAccent,
                    contentColor = OnboardingBackground
                )
            ) {
                Text(
                    text = if (isLastStep) "Start dashboard" else "Continue",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BadgePill(
    label: String,
    background: Color,
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

private fun progressText(state: OnboardingUiState): String {
    return when (state.stepIndex) {
        0 -> "Profile setup"
        1 -> "Insight preview"
        else -> "Goal selection"
    }
}

private fun stepVisualFor(stepIndex: Int): StepVisual {
    return when (stepIndex) {
        0 -> StepVisual(
            kicker = "01",
            eyebrow = "Personal setup",
            detailPoints = listOf(
                "Start with a calm, guided flow that works on phone, tablet and desktop.",
                "We keep onboarding lightweight so users reach the dashboard fast.",
                "The same route remains valid when layout density changes."
            ),
            accent = OnboardingAccentStrong,
            accentSurface = OnboardingAccentSoft
        )

        1 -> StepVisual(
            kicker = "02",
            eyebrow = "Clear signals",
            detailPoints = listOf(
                "Show activity, sleep and health sync as one readable story.",
                "Prioritize key metrics instead of scattering technical modules.",
                "Use visual grouping so sections feel intentional, not improvised."
            ),
            accent = OnboardingWarm,
            accentSurface = OnboardingWarmSoft
        )

        else -> StepVisual(
            kicker = "03",
            eyebrow = "Guided focus",
            detailPoints = listOf(
                "Choose what matters first and let the app adapt the emphasis.",
                "Keep this as a preference, not a permanent lock-in.",
                "Move secondary preferences to profile settings after onboarding."
            ),
            accent = OnboardingAccent,
            accentSurface = OnboardingAccentSoft
        )
    }
}

private fun goalLabel(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "Increase activity"
        OnboardingGoal.BetterSleep -> "Sleep better"
        OnboardingGoal.Nutrition -> "Improve nutrition"
    }
}

private fun goalDescription(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "Highlight movement, trends and quick daily actions."
        OnboardingGoal.BetterSleep -> "Surface recovery signals and better nightly routines."
        OnboardingGoal.Nutrition -> "Bring food logging and energy balance forward."
    }
}

private fun goalCode(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.Activity -> "ACT"
        OnboardingGoal.BetterSleep -> "SLP"
        OnboardingGoal.Nutrition -> "NTR"
    }
}

private fun goalAccent(goal: OnboardingGoal): Color {
    return when (goal) {
        OnboardingGoal.Activity -> OnboardingAccentStrong
        OnboardingGoal.BetterSleep -> Color(0xFF5EC2FF)
        OnboardingGoal.Nutrition -> OnboardingWarm
    }
}
