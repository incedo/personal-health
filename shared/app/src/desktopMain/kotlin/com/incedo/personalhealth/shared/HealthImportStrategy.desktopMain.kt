package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "Desktop",
    title = "Geen lokale health import",
    summary = "Desktop leest geen Health Connect of HealthKit direct. Gebruik mobiel voor bronimport en sync later via backend.",
    actions = emptyList()
)

actual suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
) {
    publishUiMessage("Desktop ondersteunt geen lokale health import-flow.")
}
