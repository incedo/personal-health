package com.incedo.personalhealth.core.goals

enum class CoachFocusGoal {
    ACTIVITY,
    BETTER_SLEEP,
    NUTRITION
}

enum class CoachProfileTrait {
    STRUCTURE_SEEKER,
    FLEXIBLE_RHYTHM,
    DATA_DRIVEN,
    TRAINING_FOCUSED,
    RECOVERY_MINDED
}

data class CoachIntakeProfile(
    val focusGoal: CoachFocusGoal? = null,
    val traits: Set<CoachProfileTrait> = emptySet()
)

fun coachFocusLabel(goal: CoachFocusGoal): String = when (goal) {
    CoachFocusGoal.ACTIVITY -> "Meer bewegen"
    CoachFocusGoal.BETTER_SLEEP -> "Beter slapen"
    CoachFocusGoal.NUTRITION -> "Voeding verbeteren"
}

fun coachProfileTraitLabel(trait: CoachProfileTrait): String = when (trait) {
    CoachProfileTrait.STRUCTURE_SEEKER -> "Wil een strak ritme"
    CoachProfileTrait.FLEXIBLE_RHYTHM -> "Leeft onregelmatig"
    CoachProfileTrait.DATA_DRIVEN -> "Wil veel meten"
    CoachProfileTrait.TRAINING_FOCUSED -> "Training staat centraal"
    CoachProfileTrait.RECOVERY_MINDED -> "Herstel eerst"
}

fun recommendCoachProtocol(
    intake: CoachIntakeProfile
): CoachProtocolId {
    if (CoachProfileTrait.RECOVERY_MINDED in intake.traits) return CoachProtocolId.RECOVERY
    if (CoachProfileTrait.DATA_DRIVEN in intake.traits && CoachProfileTrait.STRUCTURE_SEEKER in intake.traits) {
        return CoachProtocolId.PERFORMANCE
    }
    return when (intake.focusGoal) {
        CoachFocusGoal.BETTER_SLEEP -> CoachProtocolId.BALANCE
        CoachFocusGoal.NUTRITION -> CoachProtocolId.COMPOSITION
        CoachFocusGoal.ACTIVITY -> if (CoachProfileTrait.TRAINING_FOCUSED in intake.traits) {
            CoachProtocolId.PERFORMANCE
        } else if (CoachProfileTrait.FLEXIBLE_RHYTHM in intake.traits) {
            CoachProtocolId.BALANCE
        } else {
            CoachProtocolId.PERFORMANCE
        }
        null -> CoachProtocolId.BALANCE
    }
}
