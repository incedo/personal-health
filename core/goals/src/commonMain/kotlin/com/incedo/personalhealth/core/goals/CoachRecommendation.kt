package com.incedo.personalhealth.core.goals

enum class CoachSupportTab {
    DASHBOARD,
    LOGBOOK,
    PROFILE
}

data class CoachRecommendationInput(
    val onboardingFocusGoal: CoachFocusGoal? = null,
    val intakeProfile: CoachIntakeProfile = CoachIntakeProfile()
)

data class CoachRecommendation(
    val protocolId: CoachProtocolId,
    val effectiveFocusGoal: CoachFocusGoal?,
    val rationale: List<String>,
    val supportTabs: List<CoachSupportTab>
)

fun effectiveCoachIntakeProfile(
    intakeProfile: CoachIntakeProfile,
    onboardingFocusGoal: CoachFocusGoal?
): CoachIntakeProfile {
    return if (intakeProfile.focusGoal != null || onboardingFocusGoal == null) {
        intakeProfile
    } else {
        intakeProfile.copy(focusGoal = onboardingFocusGoal)
    }
}

fun buildCoachRecommendation(
    input: CoachRecommendationInput
): CoachRecommendation {
    val effectiveIntake = effectiveCoachIntakeProfile(
        intakeProfile = input.intakeProfile,
        onboardingFocusGoal = input.onboardingFocusGoal
    )
    val effectiveFocus = effectiveIntake.focusGoal

    return CoachRecommendation(
        protocolId = recommendCoachProtocol(effectiveIntake),
        effectiveFocusGoal = effectiveFocus,
        rationale = buildRecommendationRationale(effectiveIntake),
        supportTabs = recommendedSupportTabs(effectiveIntake)
    )
}

private fun buildRecommendationRationale(
    intakeProfile: CoachIntakeProfile
): List<String> {
    return buildList {
        when (intakeProfile.focusGoal) {
            CoachFocusGoal.ACTIVITY -> add("Je focus ligt op meer beweging en dagelijkse activatie.")
            CoachFocusGoal.BETTER_SLEEP -> add("Je focus ligt op slaap, herstel en een sterker dag-nachtritme.")
            CoachFocusGoal.NUTRITION -> add("Je focus ligt op voeding, timing en body-composition gedrag.")
            null -> add("Er is nog geen scherp focusdoel gekozen, dus coach stuurt voorlopig op balans.")
        }
        intakeProfile.traits.take(2).forEach { trait ->
            add(
                when (trait) {
                    CoachProfileTrait.STRUCTURE_SEEKER -> "Je zoekt een duidelijk ritme met voorspelbare ankers."
                    CoachProfileTrait.FLEXIBLE_RHYTHM -> "Je leefritme is wisselend, dus het protocol moet houvast geven zonder star te worden."
                    CoachProfileTrait.DATA_DRIVEN -> "Je wilt actief meten en beslissen op basis van signalen."
                    CoachProfileTrait.TRAINING_FOCUSED -> "Training is een belangrijke motor in je leefpatroon."
                    CoachProfileTrait.RECOVERY_MINDED -> "Herstel en prikkelregulatie hebben nu prioriteit."
                }
            )
        }
    }
}

private fun recommendedSupportTabs(
    intakeProfile: CoachIntakeProfile
): List<CoachSupportTab> {
    return when {
        intakeProfile.focusGoal == CoachFocusGoal.NUTRITION -> {
            listOf(CoachSupportTab.LOGBOOK, CoachSupportTab.DASHBOARD, CoachSupportTab.PROFILE)
        }
        intakeProfile.focusGoal == CoachFocusGoal.BETTER_SLEEP -> {
            listOf(CoachSupportTab.DASHBOARD, CoachSupportTab.PROFILE, CoachSupportTab.LOGBOOK)
        }
        CoachProfileTrait.TRAINING_FOCUSED in intakeProfile.traits -> {
            listOf(CoachSupportTab.DASHBOARD, CoachSupportTab.LOGBOOK, CoachSupportTab.PROFILE)
        }
        else -> listOf(CoachSupportTab.DASHBOARD, CoachSupportTab.LOGBOOK, CoachSupportTab.PROFILE)
    }
}
