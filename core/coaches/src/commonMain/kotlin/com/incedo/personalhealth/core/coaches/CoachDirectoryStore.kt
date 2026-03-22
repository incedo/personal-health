package com.incedo.personalhealth.core.coaches

expect object CoachDirectoryStore {
    fun coaches(): List<CoachProfile>
    fun setCoaches(coaches: List<CoachProfile>)
}
