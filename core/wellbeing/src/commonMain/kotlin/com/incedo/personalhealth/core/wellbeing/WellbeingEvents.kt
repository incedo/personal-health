package com.incedo.personalhealth.core.wellbeing

import com.incedo.personalhealth.core.events.AppEvent

sealed interface WellbeingEvent : AppEvent {
    data class ScreenTimeSummaryUpdated(
        val summary: ScreenTimeSummary,
        override val emittedAtEpochMillis: Long
    ) : WellbeingEvent

    data class ScreenTimeRefreshRequested(
        override val emittedAtEpochMillis: Long
    ) : WellbeingEvent

    data class ScreenTimePermissionRequested(
        override val emittedAtEpochMillis: Long
    ) : WellbeingEvent
}
