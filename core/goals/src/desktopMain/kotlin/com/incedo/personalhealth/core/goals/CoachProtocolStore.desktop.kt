package com.incedo.personalhealth.core.goals

import java.io.File

actual object CoachProtocolStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun selectedProtocolId(): CoachProtocolId? = runCatching {
        preferenceFile.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.isNotBlank() }?.let(CoachProtocolId::valueOf)
    }.getOrNull()

    actual fun setSelectedProtocolId(protocolId: CoachProtocolId?) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(protocolId?.name.orEmpty())
        }
    }

    private fun resolvePreferenceFile(): File {
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/coach-protocol.pref")
    }
}
