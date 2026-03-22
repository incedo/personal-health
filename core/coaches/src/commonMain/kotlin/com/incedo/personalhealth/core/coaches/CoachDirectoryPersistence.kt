package com.incedo.personalhealth.core.coaches

internal fun encodeCoachProfiles(profiles: List<CoachProfile>): String = buildString {
    profiles.forEach { profile ->
        appendLine(profile.id)
        appendLine(profile.type.name)
        appendLine(profile.name)
        appendLine(profile.location)
        appendLine(profile.imageDataUrl.orEmpty())
        appendLine(profile.isSelected.toString())
    }
}

internal fun decodeCoachProfiles(raw: String): List<CoachProfile> = raw
    .lineSequence()
    .toList()
    .chunked(6)
    .mapNotNull { chunk ->
        if (chunk.size < 6) {
            null
        } else {
            val type = runCatching { CoachType.valueOf(chunk[1]) }.getOrNull() ?: return@mapNotNull null
            CoachProfile(
                id = chunk[0],
                type = type,
                name = chunk[2],
                location = chunk[3],
                imageDataUrl = chunk[4].ifBlank { null },
                isSelected = chunk[5].toBooleanStrictOrNull() ?: true
            )
        }
    }
    .filter { it.id.isNotBlank() && it.name.isNotBlank() }
