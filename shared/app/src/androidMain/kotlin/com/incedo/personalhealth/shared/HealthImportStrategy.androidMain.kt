package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.currentEpochMillis

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "Android",
    title = "Health Connect import",
    summary = "Android kan permissies aanvragen, Health Connect openen en historie importeren.",
    actions = listOf(
        HealthImportAction(
            id = HealthImportActionId.REQUEST_PERMISSIONS,
            label = "Geef permissies"
        ),
        HealthImportAction(
            id = HealthImportActionId.OPEN_SOURCE_SETTINGS,
            label = "Open Health Connect"
        ),
        HealthImportAction(
            id = HealthImportActionId.IMPORT_HISTORY,
            label = "Importeer historie"
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
            publishUiMessage("Live sync start op Android loopt via de bestaande Health Connect flow.")
        }
    }
}
