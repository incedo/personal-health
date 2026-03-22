package com.incedo.personalhealth.core.goals

private const val COACH_PROTOCOL_KEY = "personal-health.coach-protocol"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun localStorageRemoveItem(key: String)

actual object CoachProtocolStore {
    actual fun selectedProtocolId(): CoachProtocolId? = localStorageGetItem(COACH_PROTOCOL_KEY)?.let(CoachProtocolId::valueOf)

    actual fun setSelectedProtocolId(protocolId: CoachProtocolId?) {
        if (protocolId == null) {
            localStorageRemoveItem(COACH_PROTOCOL_KEY)
        } else {
            localStorageSetItem(COACH_PROTOCOL_KEY, protocolId.name)
        }
    }
}
