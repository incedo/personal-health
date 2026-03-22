package com.incedo.personalhealth.core.coaches

enum class CoachType {
    AI_COACH,
    PERSONAL_TRAINER,
    DIETITIAN,
    LIFESTYLE_COACH,
    OTHER
}

data class CoachProfile(
    val id: String,
    val type: CoachType,
    val name: String,
    val location: String,
    val imageDataUrl: String? = null,
    val isSelected: Boolean = true
)

fun coachTypeLabel(type: CoachType): String = when (type) {
    CoachType.AI_COACH -> "AI coach"
    CoachType.PERSONAL_TRAINER -> "Personal trainer"
    CoachType.DIETITIAN -> "Dietist"
    CoachType.LIFESTYLE_COACH -> "Lifestyle coach"
    CoachType.OTHER -> "Ander type"
}

fun createCoachId(
    name: String,
    location: String,
    type: CoachType,
    index: Int
): String {
    val slug = "$name-$location-${type.name.lowercase()}"
        .lowercase()
        .map { char -> if (char.isLetterOrDigit()) char else '-' }
        .joinToString("")
        .trim('-')
        .ifBlank { "coach" }
    return "$slug-$index"
}

fun upsertCoachProfile(
    profiles: List<CoachProfile>,
    profile: CoachProfile
): List<CoachProfile> {
    val existingIndex = profiles.indexOfFirst { it.id == profile.id }
    if (existingIndex == -1) return profiles + profile
    return profiles.mapIndexed { index, current ->
        if (index == existingIndex) profile else current
    }
}

fun toggleCoachSelection(
    profiles: List<CoachProfile>,
    coachId: String
): List<CoachProfile> = profiles.map { profile ->
    if (profile.id == coachId) profile.copy(isSelected = !profile.isSelected) else profile
}

fun selectedCoachProfiles(profiles: List<CoachProfile>): List<CoachProfile> = profiles.filter { it.isSelected }
