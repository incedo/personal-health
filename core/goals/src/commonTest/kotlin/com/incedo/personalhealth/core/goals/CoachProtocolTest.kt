package com.incedo.personalhealth.core.goals

import kotlin.test.Test
import kotlin.test.assertEquals

class CoachProtocolTest {

    @Test
    fun coachProtocolById_fallsBackToFirstProtocol() {
        assertEquals(CoachProtocolId.BALANCE, coachProtocolById(null).id)
        assertEquals(CoachProtocolId.RECOVERY, coachProtocolById(CoachProtocolId.RECOVERY).id)
    }
}
