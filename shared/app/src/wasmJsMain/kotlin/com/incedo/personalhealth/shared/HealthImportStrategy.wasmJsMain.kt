package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent

actual fun currentHealthImportStrategy(): HealthImportStrategy = HealthImportStrategy(
    platformName = "Web",
    title = "Geen lokale health import",
    summary = "Web heeft geen directe koppeling met apparaat-health stores. Gebruik mobiel voor bronimport.",
    actions = emptyList()
)

actual suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
) {
    publishUiMessage("Web ondersteunt geen lokale health import-flow.")
}
