package com.incedo.personalhealth.core.goals

expect object CoachIntakeStore {
    fun profile(): CoachIntakeProfile
    fun setProfile(profile: CoachIntakeProfile)
}
