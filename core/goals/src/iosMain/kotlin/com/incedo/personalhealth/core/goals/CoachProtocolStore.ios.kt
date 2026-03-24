package com.incedo.personalhealth.core.goals

import platform.Foundation.NSUserDefaults

private const val COACH_PROTOCOL_KEY = "coach_protocol"

actual object CoachProtocolStore {
    actual fun selectedProtocolId(): CoachProtocolId? = NSUserDefaults.standardUserDefaults
        .stringForKey(COACH_PROTOCOL_KEY)
        ?.let(CoachProtocolId::valueOf)

    actual fun setSelectedProtocolId(protocolId: CoachProtocolId?) {
        if (protocolId == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(COACH_PROTOCOL_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(protocolId.name, forKey = COACH_PROTOCOL_KEY)
        }
    }
}
