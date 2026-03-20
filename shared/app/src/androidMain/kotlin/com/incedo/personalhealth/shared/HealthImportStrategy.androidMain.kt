package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.currentEpochMillis

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "Android",
    title = "Android health import",
    summary = "Samsung Health Data SDK krijgt voorrang voor slaap, actieve energie en gewicht. Health Connect blijft fallback voor stappen, hartslag en overige data.",
    actions = listOf(
        HealthImportAction(
            id = HealthImportActionId.REQUEST_PERMISSIONS,
            label = "Geef bronpermissies"
        ),
        HealthImportAction(
            id = HealthImportActionId.OPEN_SOURCE_SETTINGS,
            label = "Open Health Connect"
        ),
        HealthImportAction(
            id = HealthImportActionId.IMPORT_HISTORY,
            label = "Importeer Android health"
        )
    )
)

actual suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
) {
    when (actionId) {
        HealthImportActionId.REQUEST_PERMISSIONS -> {
            publishHealthEvent(
                HealthEvent.PermissionsRequested(
                    emittedAtEpochMillis = currentEpochMillis()
                )
            )
        }

        HealthImportActionId.IMPORT_HISTORY -> {
            publishHealthEvent(
                HealthEvent.SyncRequested(
                    metrics = DEFAULT_IMPORT_METRICS,
                    emittedAtEpochMillis = currentEpochMillis()
                )
            )
        }

        HealthImportActionId.OPEN_SOURCE_SETTINGS -> {
            publishHealthEvent(
                HealthEvent.HealthConnectSettingsRequested(
                    emittedAtEpochMillis = currentEpochMillis()
                )
            )
        }

        HealthImportActionId.START_LIVE_SYNC -> {
            publishUiMessage("Android gebruikt Samsung Health als voorkeursbron waar beschikbaar en Health Connect als fallback.")
        }
    }
}
