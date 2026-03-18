package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "iOS",
    title = "HealthKit import",
    summary = "iOS gebruikt een andere flow: import en live sync lopen via HealthKit-specifieke brugcode.",
    actions = listOf(
        HealthImportAction(
            id = HealthImportActionId.IMPORT_HISTORY,
            label = "Importeer historie"
        ),
        HealthImportAction(
            id = HealthImportActionId.START_LIVE_SYNC,
            label = "Start live sync"
        )
    )
)

actual suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
) {
    val bridge = IOSSharedUiBridge()
    when (actionId) {
        HealthImportActionId.IMPORT_HISTORY -> {
            bridge.startIosHealthHistoryImport()
            publishUiMessage("HealthKit historie-import gestart.")
        }

        HealthImportActionId.START_LIVE_SYNC -> {
            bridge.startIosHealthLiveSync()
            publishUiMessage("HealthKit live sync gestart.")
        }

        HealthImportActionId.REQUEST_PERMISSIONS -> {
            publishUiMessage("HealthKit permissies verlopen via de iOS-specifieke app-flow.")
        }

        HealthImportActionId.OPEN_SOURCE_SETTINGS -> {
            publishUiMessage("HealthKit instellingen worden vanuit de iOS shell afgehandeld.")
        }
    }
}
