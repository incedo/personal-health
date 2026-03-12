package com.incedo.personalhealth.shared

actual object OnboardingPreferenceStore {
    private var completed: Boolean = false

    actual fun isCompleted(): Boolean = completed

    actual fun setCompleted(completed: Boolean) {
        this.completed = completed
    }
}
