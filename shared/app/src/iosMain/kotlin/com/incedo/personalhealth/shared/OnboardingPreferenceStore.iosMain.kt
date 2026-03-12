package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object OnboardingPreferenceStore {
    private const val KEY = "onboarding_completed"

    actual fun isCompleted(): Boolean = NSUserDefaults.standardUserDefaults.boolForKey(KEY)

    actual fun setCompleted(completed: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(completed, forKey = KEY)
    }
}
