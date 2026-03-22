package com.incedo.personalhealth.core.goals

data class CoachGoal(
    val title: String,
    val cadence: String,
    val focus: String
)

data class CoachGoalsState(
    val goals: List<CoachGoal>,
    val draftTitle: String = ""
)

val defaultCoachGoals: List<CoachGoal> = listOf(
    CoachGoal(
        title = "8.000 stappen halen",
        cadence = "Vandaag",
        focus = "Beweging"
    ),
    CoachGoal(
        title = "23:00 in bed liggen",
        cadence = "Vanavond",
        focus = "Slaap"
    )
)

val suggestedCoachGoals: List<CoachGoal> = listOf(
    CoachGoal(
        title = "2 liter water drinken",
        cadence = "Vandaag",
        focus = "Hydratatie"
    ),
    CoachGoal(
        title = "30 min wandelen na lunch",
        cadence = "Middag",
        focus = "Herstel"
    ),
    CoachGoal(
        title = "Eiwitrijk diner plannen",
        cadence = "Vanavond",
        focus = "Voeding"
    )
)

fun addCoachGoal(
    state: CoachGoalsState,
    title: String,
    cadence: String = "Deze week",
    focus: String = "Coach"
): CoachGoalsState {
    val normalizedTitle = title.trim()
    if (normalizedTitle.isBlank()) return state
    val duplicate = state.goals.any { it.title.equals(normalizedTitle, ignoreCase = true) }
    if (duplicate) return state.copy(draftTitle = "")
    return state.copy(
        goals = state.goals + CoachGoal(
            title = normalizedTitle,
            cadence = cadence,
            focus = focus
        ),
        draftTitle = ""
    )
}
