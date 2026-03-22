package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.coaches.CoachDirectoryStore
import com.incedo.personalhealth.core.coaches.CoachProfile
import com.incedo.personalhealth.core.coaches.createCoachId
import com.incedo.personalhealth.core.coaches.toggleCoachSelection
import com.incedo.personalhealth.core.coaches.upsertCoachProfile
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.goals.CoachGoalsState
import com.incedo.personalhealth.core.goals.CoachGoalsStore
import com.incedo.personalhealth.core.goals.CoachIntakeProfile
import com.incedo.personalhealth.core.goals.CoachIntakeStore
import com.incedo.personalhealth.core.goals.CoachProtocolStore
import com.incedo.personalhealth.core.goals.CoachRecommendationInput
import com.incedo.personalhealth.core.goals.addCoachGoal
import com.incedo.personalhealth.core.goals.buildCoachRecommendation
import com.incedo.personalhealth.core.goals.coachFocusLabel
import com.incedo.personalhealth.core.goals.coachProtocolById
import com.incedo.personalhealth.core.goals.defaultCoachGoals

@Composable
internal fun CoachSectionScreen(
    page: CoachPage,
    compact: Boolean,
    onboardingFocusGoal: CoachFocusGoal?,
    onCloseCoachDetail: () -> Unit,
    onOpenCoachIntake: () -> Unit,
    onOpenCoachGoals: () -> Unit,
    onOpenCoachDetails: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenLogbook: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val initialGoals = remember { CoachGoalsStore.goals().ifEmpty { defaultCoachGoals } }
    val initialCoaches = remember { CoachDirectoryStore.coaches() }
    var intakeProfile by rememberSaveable(stateSaver = coachIntakeProfileSaver()) {
        mutableStateOf(CoachIntakeStore.profile())
    }
    var selectedProtocolId by rememberSaveable(stateSaver = coachProtocolIdSaver()) {
        mutableStateOf(CoachProtocolStore.selectedProtocolId())
    }
    var goalsState by rememberSaveable(stateSaver = coachGoalsStateSaver()) {
        mutableStateOf(CoachGoalsState(goals = initialGoals))
    }
    var coachProfiles by rememberSaveable(stateSaver = coachProfilesSaver()) {
        mutableStateOf(initialCoaches)
    }
    var editorState by rememberSaveable(stateSaver = coachEditorStateSaver()) {
        mutableStateOf<CoachEditorState?>(null)
    }
    val recommendation = remember(onboardingFocusGoal, intakeProfile) {
        buildCoachRecommendation(
            CoachRecommendationInput(
                onboardingFocusGoal = onboardingFocusGoal,
                intakeProfile = intakeProfile
            )
        )
    }
    val selectedProtocol = coachProtocolById(selectedProtocolId ?: recommendation.protocolId)
    fun applySuggestedProtocol(updatedProfile: CoachIntakeProfile) {
        val suggested = buildCoachRecommendation(
            CoachRecommendationInput(
                onboardingFocusGoal = onboardingFocusGoal,
                intakeProfile = updatedProfile
            )
        )
        selectedProtocolId = suggested.protocolId
        CoachProtocolStore.setSelectedProtocolId(suggested.protocolId)
    }
    fun openNewCoachEditor() {
        editorState = CoachEditorState()
    }
    fun openCoachEditor(coach: CoachProfile) {
        editorState = CoachEditorState(
            editingCoachId = coach.id,
            selectedType = coach.type,
            name = coach.name,
            location = coach.location,
            imageDataUrl = coach.imageDataUrl
        )
    }
    fun saveCoachEditor() {
        val draft = editorState ?: return
        val existingProfile = coachProfiles.firstOrNull { it.id == draft.editingCoachId }
        val nextId = draft.editingCoachId ?: createCoachId(
            name = draft.name,
            location = draft.location,
            type = draft.selectedType,
            index = coachProfiles.size + 1
        )
        val updatedProfiles = upsertCoachProfile(
            profiles = coachProfiles,
            profile = CoachProfile(
                id = nextId,
                type = draft.selectedType,
                name = draft.name.trim(),
                location = draft.location.trim(),
                imageDataUrl = draft.imageDataUrl,
                isSelected = existingProfile?.isSelected ?: true
            )
        )
        coachProfiles = updatedProfiles
        CoachDirectoryStore.setCoaches(updatedProfiles)
        editorState = null
    }

    if (editorState != null) {
        CoachEditorScreen(
            compact = compact,
            editorState = editorState ?: CoachEditorState(),
            onBack = { editorState = null },
            onTypeSelected = { selectedType ->
                editorState = editorState?.copy(selectedType = selectedType)
            },
            onNameChanged = { name ->
                editorState = editorState?.copy(name = name)
            },
            onLocationChanged = { location ->
                editorState = editorState?.copy(location = location)
            },
            onPickImage = {
                NutritionImagePicker.pickImage { uploaded ->
                    editorState = editorState?.copy(imageDataUrl = uploaded?.dataUrl)
                }
            },
            onSave = ::saveCoachEditor
        )
        return
    }

    if (page != CoachPage.OVERVIEW) {
        CoachSubScreen(
            compact = compact,
            page = page,
            recommendation = recommendation,
            onboardingFocusGoal = onboardingFocusGoal,
            selectedProtocol = selectedProtocol,
            goals = goalsState.goals,
            draftGoalTitle = goalsState.draftTitle,
            coaches = coachProfiles,
            intakeProfile = intakeProfile,
            onBack = onCloseCoachDetail,
            onProtocolSelected = {
                selectedProtocolId = it
                CoachProtocolStore.setSelectedProtocolId(it)
            },
            onOpenDashboard = onOpenDashboard,
            onOpenLogbook = onOpenLogbook,
            onOpenProfile = onOpenProfile,
            onFocusSelected = {
                val updatedProfile = intakeProfile.copy(focusGoal = it)
                intakeProfile = updatedProfile
                CoachIntakeStore.setProfile(updatedProfile)
                applySuggestedProtocol(updatedProfile)
            },
            onTraitToggled = { trait ->
                val updatedTraits = if (trait in intakeProfile.traits) intakeProfile.traits - trait else intakeProfile.traits + trait
                val updatedProfile = intakeProfile.copy(traits = updatedTraits)
                intakeProfile = updatedProfile
                CoachIntakeStore.setProfile(updatedProfile)
                applySuggestedProtocol(updatedProfile)
            },
            onDraftChanged = { goalsState = goalsState.copy(draftTitle = it) },
            onAddGoal = {
                goalsState = addCoachGoal(state = goalsState, title = goalsState.draftTitle)
                CoachGoalsStore.setGoals(goalsState.goals)
            },
            onAddSuggestion = { suggestion ->
                goalsState = addCoachGoal(
                    state = goalsState,
                    title = suggestion.title,
                    cadence = suggestion.cadence,
                    focus = suggestion.focus
                )
                CoachGoalsStore.setGoals(goalsState.goals)
            },
            onAddCoach = ::openNewCoachEditor,
            onEditCoach = ::openCoachEditor,
            onToggleCoachSelection = { coachId ->
                val updatedProfiles = toggleCoachSelection(coachProfiles, coachId)
                coachProfiles = updatedProfiles
                CoachDirectoryStore.setCoaches(updatedProfiles)
            }
        )
        return
    }

    Box {
        HomeSectionScreen(
            tab = HomeTab.COACH,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Coach",
                    title = "Je dagelijkse begeleiding",
                    subtitle = "Zie in een oogopslag je protocol en doelen, en open losse coachschermen voor intake, persoonlijke doelen en persoonlijke details.",
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
                CoachOverviewContent(
                    recommendation = recommendation,
                    selectedProtocol = selectedProtocol,
                    goals = goalsState.goals,
                    onOpenIntake = onOpenCoachIntake,
                    onOpenGoals = onOpenCoachGoals,
                    onOpenDetails = onOpenCoachDetails,
                    onOpenDashboard = onOpenDashboard,
                    onOpenLogbook = onOpenLogbook,
                    onOpenProfile = onOpenProfile
                )
            }
        )
        FloatingCoachDock(
            coaches = coachProfiles,
            onAddCoach = ::openNewCoachEditor,
            modifier = Modifier.padding(top = if (compact) 10.dp else 16.dp, start = 10.dp)
        )
    }
}
