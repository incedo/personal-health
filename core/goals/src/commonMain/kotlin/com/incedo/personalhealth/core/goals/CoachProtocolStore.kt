package com.incedo.personalhealth.core.goals

expect object CoachProtocolStore {
    fun selectedProtocolId(): CoachProtocolId?
    fun setSelectedProtocolId(protocolId: CoachProtocolId?)
}
