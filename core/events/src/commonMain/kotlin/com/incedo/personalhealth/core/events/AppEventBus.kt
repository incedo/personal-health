package com.incedo.personalhealth.core.events

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface AppEvent {
    val emittedAtEpochMillis: Long
}

interface AppEventBus {
    val events: Flow<AppEvent>
    suspend fun publish(event: AppEvent)
}

class InMemoryAppEventBus : AppEventBus {
    private val mutableEvents = MutableSharedFlow<AppEvent>(
        replay = 0,
        extraBufferCapacity = 128
    )

    override val events: Flow<AppEvent> = mutableEvents.asSharedFlow()

    override suspend fun publish(event: AppEvent) {
        mutableEvents.emit(event)
    }
}
