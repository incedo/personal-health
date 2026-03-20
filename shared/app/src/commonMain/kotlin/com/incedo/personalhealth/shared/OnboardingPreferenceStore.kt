package com.incedo.personalhealth.shared

expect object OnboardingPreferenceStore {
    fun isCompleted(): Boolean
    fun setCompleted(completed: Boolean)
    fun stepIndex(): Int
    fun setStepIndex(stepIndex: Int)
    fun selectedGoalId(): String?
    fun setSelectedGoalId(goalId: String?)
}
