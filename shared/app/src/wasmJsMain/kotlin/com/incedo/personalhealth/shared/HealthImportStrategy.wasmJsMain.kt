package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "Web",
    title = "Canonical web import",
    summary = "Web leest geen Health Connect of HealthKit direct, maar ondersteunt nu canonical JSON-import voor browserdata en stapvisualisatie.",
    actions = emptyList()
)

actual suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
) {
    publishUiMessage("Web ondersteunt geen lokale health import-flow.")
}
