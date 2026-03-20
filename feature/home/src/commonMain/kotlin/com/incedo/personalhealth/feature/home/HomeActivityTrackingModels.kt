package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable

@Serializable
data class ActiveQuickActivitySession(
    val id: String,
    val type: QuickActivityType,
    val startedAtEpochMillis: Long
)

@Serializable
data class CompletedQuickActivitySession(
    val id: String,
    val type: QuickActivityType,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long
)

data class ActivityTrackingSnapshot(
    val activeSession: ActiveQuickActivitySession? = null,
    val completedSessions: List<CompletedQuickActivitySession> = emptyList()
)

val CompletedQuickActivitySession.durationMillis: Long
    get() = (completedAtEpochMillis - startedAtEpochMillis).coerceAtLeast(0L)

fun ActiveQuickActivitySession.elapsedDurationMillis(nowEpochMillis: Long): Long =
    (nowEpochMillis - startedAtEpochMillis).coerceAtLeast(0L)

fun CompletedQuickActivitySession.toQuickActivityEntry(): QuickActivityEntry = QuickActivityEntry(
    id = id,
    type = type,
    title = "${type.label} sessie",
    createdAtEpochMillis = completedAtEpochMillis,
    durationMillis = durationMillis
)

fun totalTrackedActivityMinutes(
    completedSessions: List<CompletedQuickActivitySession>,
    activeSession: ActiveQuickActivitySession?,
    nowEpochMillis: Long,
    dayWindow: LocalDayWindow
): Int {
    val completedDuration = completedSessions.sumOf { session ->
        overlappingDurationMillis(
            startEpochMillis = session.startedAtEpochMillis,
            endEpochMillis = session.completedAtEpochMillis,
            dayWindow = dayWindow
        )
    }
    val activeDuration = activeSession?.let { session ->
        overlappingDurationMillis(
            startEpochMillis = session.startedAtEpochMillis,
            endEpochMillis = nowEpochMillis,
            dayWindow = dayWindow
        )
    } ?: 0L
    return ((completedDuration + activeDuration) / 60_000L).toInt()
}

fun formatActiveDuration(durationMillis: Long): String {
    val totalSeconds = (durationMillis / 1_000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return buildString {
        append(hours.toString().padStart(2, '0'))
        append(':')
        append(minutes.toString().padStart(2, '0'))
        append(':')
        append(seconds.toString().padStart(2, '0'))
    }
}

fun formatActivitySummaryDuration(durationMillis: Long): String {
    val totalMinutes = (durationMillis / 60_000L).coerceAtLeast(0L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return when {
        hours > 0L -> "${hours}u ${minutes}m"
        else -> "${minutes}m"
    }
}

private fun overlappingDurationMillis(
    startEpochMillis: Long,
    endEpochMillis: Long,
    dayWindow: LocalDayWindow
): Long {
    val overlapStart = maxOf(startEpochMillis, dayWindow.startEpochMillis)
    val overlapEnd = minOf(endEpochMillis, dayWindow.endEpochMillisExclusive)
    return (overlapEnd - overlapStart).coerceAtLeast(0L)
}
