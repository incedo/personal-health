package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.coaches.CoachProfile
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.goals.CoachGoal
import com.incedo.personalhealth.core.goals.CoachIntakeProfile
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachProtocolId
import com.incedo.personalhealth.core.goals.CoachProfileTrait
import com.incedo.personalhealth.core.goals.CoachRecommendation
import com.incedo.personalhealth.core.goals.coachFocusLabel

@Composable
internal fun CoachSubScreen(
    compact: Boolean,
    page: CoachPage,
    recommendation: CoachRecommendation,
    onboardingFocusGoal: CoachFocusGoal?,
    selectedProtocol: CoachProtocol,
    goals: List<CoachGoal>,
    draftGoalTitle: String,
    coaches: List<CoachProfile>,
    intakeProfile: CoachIntakeProfile,
    onBack: () -> Unit,
    onProtocolSelected: (CoachProtocolId) -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenLogbook: () -> Unit,
    onOpenProfile: () -> Unit,
    onFocusSelected: (CoachFocusGoal) -> Unit,
    onTraitToggled: (CoachProfileTrait) -> Unit,
    onDraftChanged: (String) -> Unit,
    onAddGoal: () -> Unit,
    onAddSuggestion: (com.incedo.personalhealth.core.goals.CoachGoal) -> Unit,
    onAddCoach: () -> Unit,
    onEditCoach: (CoachProfile) -> Unit,
    onToggleCoachSelection: (String) -> Unit,
    onDeleteCoach: (String) -> Unit
) {
    val title = when (page) {
        CoachPage.INTAKE -> "Coach intake"
        CoachPage.GOALS -> "Persoonlijke doelen"
        CoachPage.DETAILS -> "Persoonlijke details"
        CoachPage.TRAINING_PROGRAM -> "Trainingsprogramma"
        CoachPage.OVERVIEW -> "Coach"
    }
    val subtitle = when (page) {
        CoachPage.INTAKE -> "Werk je profieldefinitie en protocolvoorstel bij."
        CoachPage.GOALS -> "Beheer al je persoonlijke doelen in een los scherm."
        CoachPage.DETAILS -> "Beheer je coachkeuzes en persoonlijke accenten."
        CoachPage.TRAINING_PROGRAM -> "Bekijk de trainingsopbouw die coach aan je protocol koppelt."
        CoachPage.OVERVIEW -> ""
    }

    Box {
        HomeSectionScreen(
            tab = HomeTab.COACH,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Coach",
                    title = title,
                    subtitle = subtitle,
                    accent = homePalette().warning,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Protocol",
                            value = selectedProtocol.title
                        )
                    }
                )
            },
            bodyContent = {
                CoachScreenBackButton(onBack = onBack)
                Spacer(modifier = Modifier.height(18.dp))
                when (page) {
                    CoachPage.INTAKE -> {
                        CoachConversationCard(
                            onboardingFocusText = onboardingFocusGoal?.let(::coachFocusLabel),
                            intakeProfile = intakeProfile,
                            recommendation = recommendation
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        CoachIntakeCard(
                            onboardingFocusGoal = onboardingFocusGoal,
                            intake = intakeProfile,
                            recommendation = recommendation,
                            onFocusSelected = onFocusSelected,
                            onTraitToggled = onTraitToggled
                        )
                    }
                    CoachPage.GOALS -> {
                        CoachGoalsOverviewCard(goals = goals)
                        Spacer(modifier = Modifier.height(18.dp))
                        CoachGoalAddCard(
                            draftTitle = draftGoalTitle,
                            onDraftChanged = onDraftChanged,
                            onAddGoal = onAddGoal,
                            onAddSuggestion = onAddSuggestion
                        )
                    }
                    CoachPage.DETAILS -> {
                        CoachDirectoryCard(
                            coaches = coaches,
                            onAddCoach = onAddCoach,
                            onEditCoach = onEditCoach,
                            onToggleCoachSelection = onToggleCoachSelection,
                            onDeleteCoach = onDeleteCoach
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        CoachProtocolCard(
                            selectedProtocol = selectedProtocol,
                            supportTabs = recommendation.supportTabs,
                            onProtocolSelected = onProtocolSelected,
                            onOpenDashboard = onOpenDashboard,
                            onOpenLogbook = onOpenLogbook,
                            onOpenProfile = onOpenProfile
                        )
                    }
                    CoachPage.TRAINING_PROGRAM -> {
                        CoachTrainingProgramContent(
                            recommendation = recommendation,
                            selectedProtocol = selectedProtocol
                        )
                    }
                    CoachPage.OVERVIEW -> Unit
                }
            }
        )
        FloatingCoachDock(
            coaches = coaches,
            onAddCoach = onAddCoach,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = if (compact) 16.dp else 24.dp,
                    bottom = if (compact) 108.dp else 124.dp
                )
        )
    }
}

@Composable
private fun CoachScreenBackButton(
    onBack: () -> Unit
) {
    OutlinedButton(
        onClick = onBack,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("Terug")
    }
}
