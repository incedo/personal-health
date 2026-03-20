package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.currentEpochMillis
import com.incedo.personalhealth.feature.home.HomeDetailDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun HomeDetailDestination.routeName(): String = when (this) {
    HomeDetailDestination.STEPS -> "steps-detail"
    HomeDetailDestination.HEART_RATE -> "heart-rate-detail"
    HomeDetailDestination.WEIGHT -> "weight-detail"
    HomeDetailDestination.HEALTH_DATA -> "health-data-detail"
    HomeDetailDestination.FITNESS -> "fitness-detail"
    HomeDetailDestination.FITNESS_EDITOR_DEBUG -> "fitness-editor-debug"
}

internal fun publishNavigationChange(
    scope: CoroutineScope,
    fromRoute: String,
    toRoute: String
) {
    scope.launch {
        AppBus.events.publish(
            FrontendEvent.NavigationChanged(
                fromRoute = fromRoute,
                toRoute = toRoute,
                emittedAtEpochMillis = currentEpochMillis()
            )
        )
    }
}
