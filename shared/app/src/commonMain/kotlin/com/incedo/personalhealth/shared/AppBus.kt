package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.InMemoryAppEventBus

/**
 * App-level bus instance used for in-process realtime sync between modules.
 */
object AppBus {
    val events: AppEventBus = InMemoryAppEventBus()
}
