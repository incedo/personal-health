package com.incedo.personalhealth.core.coaches

internal fun encodeCoachProfiles(profiles: List<CoachProfile>): String = buildString {
    profiles.forEach { profile ->
        appendLine(profile.id)
        appendLine(profile.type.name)
        appendLine(profile.name)
        appendLine(profile.companyName)
        appendLine(profile.location)
        appendLine(profile.imageDataUrl.orEmpty())
        appendLine(profile.isSelected.toString())
    }
}

internal fun decodeCoachProfiles(raw: String): List<CoachProfile> = raw
    .split('\n')
    .dropLastWhile { it.isEmpty() }
    .let(::decodeCoachProfileChunks)
    .filter { it.id.isNotBlank() && it.name.isNotBlank() }

private fun decodeCoachProfileChunks(lines: List<String>): List<CoachProfile> = when {
    lines.size % 7 == 0 -> lines.chunked(7).mapNotNull(::decodeCoachProfileChunkV2)
    else -> lines.chunked(6).mapNotNull(::decodeCoachProfileChunkV1)
}

private fun decodeCoachProfileChunkV2(chunk: List<String>): CoachProfile? {
    if (chunk.size < 7) return null
    val type = runCatching { CoachType.valueOf(chunk[1]) }.getOrNull() ?: return null
    return CoachProfile(
        id = chunk[0],
        type = type,
        name = chunk[2],
        companyName = chunk[3],
        location = chunk[4],
        imageDataUrl = chunk[5].ifBlank { null },
        isSelected = chunk[6].toBooleanStrictOrNull() ?: true
    )
}

private fun decodeCoachProfileChunkV1(chunk: List<String>): CoachProfile? {
    if (chunk.size < 6) return null
    val type = runCatching { CoachType.valueOf(chunk[1]) }.getOrNull() ?: return null
    return CoachProfile(
        id = chunk[0],
        type = type,
        name = chunk[2],
        location = chunk[3],
        imageDataUrl = chunk[4].ifBlank { null },
        isSelected = chunk[5].toBooleanStrictOrNull() ?: true
    )
}
