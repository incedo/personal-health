package com.incedo.personalhealth.core.goals

expect object CoachGoalsStore {
    fun goals(): List<CoachGoal>
    fun setGoals(goals: List<CoachGoal>)
}
