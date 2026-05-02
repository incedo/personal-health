package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object OnboardingPreferenceStore {
    private const val COMPLETED_KEY = "onboarding_completed"
    private const val STEP_INDEX_KEY = "onboarding_step_index"
    private const val GOAL_ID_KEY = "onboarding_goal_id"
    private const val STATE_PAYLOAD_KEY = "onboarding_state_payload"

    actual fun isCompleted(): Boolean = NSUserDefaults.standardUserDefaults.boolForKey(COMPLETED_KEY)

    actual fun setCompleted(completed: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(completed, forKey = COMPLETED_KEY)
    }

    actual fun stepIndex(): Int = NSUserDefaults.standardUserDefaults.integerForKey(STEP_INDEX_KEY).toInt()

    actual fun setStepIndex(stepIndex: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(stepIndex.toLong(), forKey = STEP_INDEX_KEY)
    }

    actual fun selectedGoalId(): String? = NSUserDefaults.standardUserDefaults.stringForKey(GOAL_ID_KEY)

    actual fun setSelectedGoalId(goalId: String?) {
        if (goalId == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(GOAL_ID_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(goalId, forKey = GOAL_ID_KEY)
        }
    }

    actual fun statePayload(): String? = NSUserDefaults.standardUserDefaults.stringForKey(STATE_PAYLOAD_KEY)

    actual fun setStatePayload(payload: String?) {
        if (payload.isNullOrBlank()) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(STATE_PAYLOAD_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(payload, forKey = STATE_PAYLOAD_KEY)
        }
    }
}
