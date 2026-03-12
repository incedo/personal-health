package com.incedo.personalhealth.shared

expect object OnboardingPreferenceStore {
    fun isCompleted(): Boolean
    fun setCompleted(completed: Boolean)
}
