package com.incedo.personalhealth.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthChangeSignal
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthLiveSyncProcessor
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthSignalSubscription
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.integration.healthconnect.HealthConnectGateway
import com.incedo.personalhealth.integration.healthconnect.HealthConnectPollingSignalSource
import com.incedo.personalhealth.feature.home.NutritionImagePicker
import com.incedo.personalhealth.shared.AppBus
import com.incedo.personalhealth.shared.PersonalHealthApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requiredPermissions: Set<String> by lazy { HealthConnectGateway.requiredPermissions() }
    private var healthConnectLiveSyncSubscription: HealthSignalSubscription? = null
    private var pendingHistoryMetrics: Set<HealthMetricType> = SYNC_METRICS
    private var startImportAfterPermissionGrant: Boolean = true
    private var samsungHealthPermissionAttempted: Boolean = false

    private val requestHealthConnectPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(requiredPermissions)) {
            if (startImportAfterPermissionGrant) {
                publishUiFeedback("Health Connect permissies verleend, import start.")
                startAndroidHealthSync(metrics = pendingHistoryMetrics)
            } else {
                publishUiFeedback("Health Connect permissies verleend.")
                refreshTodayDashboardMetrics()
            }
        } else {
            publishUiFeedback("Health Connect permissies niet verleend.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NutritionImagePicker.attachHost(this)
        setContent {
            PersonalHealthApp()
        }
        observeUiHealthSyncRequests()
        refreshTodayDashboardMetrics()
        samsungHealthAvailabilityMessage(this)?.let(::publishUiFeedback)
        publishUiFeedback("Klaar. Gebruik 'Geef permissies' of 'Importeer historie' om te starten.")
    }

    override fun onResume() {
        super.onResume()
        refreshTodayDashboardMetrics()
    }

    private fun requestAndroidHealthPermissionsOnly() {
        startImportAfterPermissionGrant = false
        lifecycleScope.launch {
            val samsungGateway = prepareSamsungHealthGateway(
                activity = this@MainActivity,
                shouldRequestPermissions = !samsungHealthPermissionAttempted
            )
            samsungHealthPermissionAttempted = true
            if (samsungGateway != null) {
                publishUiFeedback("Samsung Health permissies zijn actief.")
            }
            requestHealthConnectPermissionsIfNeeded(
                requestedMetrics = SYNC_METRICS,
                reason = "stappen en hartslag"
            )
        }
    }

    private fun startAndroidHealthSync(
        metrics: Set<HealthMetricType>
    ) {
        ensureAndroidLiveSyncStarted()
        startAndroidHealthHistoryImport(metrics = metrics)
    }

    private fun ensureAndroidLiveSyncStarted() {
        if (healthConnectLiveSyncSubscription != null) return

        val gateway = HealthConnectGateway(
            context = this,
            eventBus = AppBus.events
        )
        val processor = HealthLiveSyncProcessor(
            gateway = gateway,
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = AppBus.events
        )
        val signalSource = HealthConnectPollingSignalSource()
        healthConnectLiveSyncSubscription = signalSource.start(lifecycleScope) { signal ->
            handleLiveSyncSignal(processor, signal)
        }
    }

    private fun startAndroidHealthHistoryImport(
        metrics: Set<HealthMetricType> = SYNC_METRICS
    ) {
        lifecycleScope.launch {
            val nowEpochMillis = System.currentTimeMillis()
            pendingHistoryMetrics = metrics
            startImportAfterPermissionGrant = true

            val samsungGateway = prepareSamsungHealthGateway(
                activity = this@MainActivity,
                shouldRequestPermissions = !samsungHealthPermissionAttempted
            )
            samsungHealthPermissionAttempted = true

            runCatching {
                val samsungMetrics = samsungMetricsFor(metrics)
                val samsungCount = if (samsungGateway != null && samsungMetrics.isNotEmpty()) {
                    importHealthHistory(
                        gateway = samsungGateway,
                        source = HealthDataSource.SAMSUNG_HEALTH,
                        metrics = samsungMetrics,
                        nowEpochMillis = nowEpochMillis,
                        eventBus = AppBus.events,
                        oneYearMillis = ONE_YEAR_MILLIS
                    )
                } else {
                    0
                }

                val healthConnectMetrics = healthConnectFallbackMetricsFor(
                    metrics = metrics,
                    samsungReady = samsungGateway != null
                )
                val healthConnectGateway = healthConnectGatewayOrRequest(
                    requestedMetrics = metrics,
                    requestPermissions = healthConnectMetrics.isNotEmpty(),
                    reason = "fallback import"
                )
                val healthConnectCount = if (healthConnectGateway != null && healthConnectMetrics.isNotEmpty()) {
                    ensureAndroidLiveSyncStarted()
                    importHealthHistory(
                        gateway = healthConnectGateway,
                        source = HealthDataSource.HEALTH_CONNECT,
                        metrics = healthConnectMetrics,
                        nowEpochMillis = nowEpochMillis,
                        eventBus = AppBus.events,
                        oneYearMillis = ONE_YEAR_MILLIS
                    )
                } else {
                    0
                }

                publishTodayDashboardMetrics(
                    primaryGateway = samsungGateway,
                    fallbackGateway = healthConnectGateway,
                    eventBus = AppBus.events
                )
                publishUiFeedback(
                    buildImportCompletionMessage(
                        samsungCount = samsungCount,
                        healthConnectCount = healthConnectCount,
                        pendingHealthConnectMetrics = healthConnectMetrics,
                        healthConnectGateway = healthConnectGateway
                    )
                )
            }
                .onFailure { error ->
                    Log.e(TAG, "History import failed", error)
                    publishUiFeedback("Historie import gefaald: ${error.message ?: "onbekende fout"}")
                }
        }
    }

    private fun observeUiHealthSyncRequests() {
        lifecycleScope.launch {
            AppBus.events.events.collect { event ->
                when (event) {
                    is HealthEvent.SyncRequested -> {
                        publishUiFeedback("Importverzoek ontvangen. Samsung Health eerst, Health Connect als fallback.")
                        startAndroidHealthSync(metrics = event.metrics)
                    }
                    is HealthEvent.PermissionsRequested -> {
                        publishUiFeedback("Permissieverzoek ontvangen...")
                        requestAndroidHealthPermissionsOnly()
                    }
                    is HealthEvent.HealthConnectSettingsRequested -> {
                        publishUiFeedback("Open Health Connect instellingen...")
                        openHealthConnectSettings(
                            providerPackage = HEALTH_CONNECT_PROVIDER_PACKAGE,
                            publishUiFeedback = ::publishUiFeedback
                        )
                    }
                }
            }
        }
    }

    private fun handleLiveSyncSignal(
        processor: HealthLiveSyncProcessor,
        signal: HealthChangeSignal
    ) {
        lifecycleScope.launch {
            runCatching {
                processor.processSignal(
                    signal = signal,
                    metrics = SYNC_METRICS,
                    lookbackMillis = LIVE_SYNC_LOOKBACK_MILLIS
                )
                publishTodayDashboardMetrics(
                    primaryGateway = prepareSamsungHealthGateway(
                        activity = this@MainActivity,
                        shouldRequestPermissions = false
                    ),
                    fallbackGateway = processorGateway(),
                    eventBus = AppBus.events
                )
            }
                .onFailure { error ->
                    Log.e(TAG, "Live sync processing failed", error)
                    publishUiFeedback("Live sync fout: ${error.message ?: "onbekende fout"}")
                }
        }
    }

    private fun refreshTodayDashboardMetrics() {
        lifecycleScope.launch {
            val samsungGateway = prepareSamsungHealthGateway(
                activity = this@MainActivity,
                shouldRequestPermissions = !samsungHealthPermissionAttempted
            )
            samsungHealthPermissionAttempted = true
            val healthConnectGateway = healthConnectGatewayOrRequest(
                requestedMetrics = SYNC_METRICS,
                requestPermissions = false,
                reason = "dashboard refresh"
            )
            if (healthConnectGateway != null) {
                ensureAndroidLiveSyncStarted()
            }
            if (samsungGateway == null && healthConnectGateway == null) return@launch
            publishTodayDashboardMetrics(
                primaryGateway = samsungGateway,
                fallbackGateway = healthConnectGateway,
                eventBus = AppBus.events
            )
        }
    }

    private suspend fun healthConnectGatewayOrRequest(
        requestedMetrics: Set<HealthMetricType>,
        requestPermissions: Boolean,
        reason: String
    ): HealthConnectGateway? {
        when (HealthConnectClient.getSdkStatus(this, HEALTH_CONNECT_PROVIDER_PACKAGE)) {
            HealthConnectClient.SDK_UNAVAILABLE -> return null
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                if (requestPermissions) {
                    publishUiFeedback("Health Connect update/installatie vereist.")
                    openHealthConnectOnPlayStore(
                        providerPackage = HEALTH_CONNECT_PROVIDER_PACKAGE,
                        publishUiFeedback = ::publishUiFeedback
                    )
                }
                return null
            }
            HealthConnectClient.SDK_AVAILABLE -> Unit
            else -> return null
        }
        if (!HealthConnectGateway.isAvailable(this)) return null

        val client = HealthConnectClient.getOrCreate(this)
        val granted = runCatching {
            client.permissionController.getGrantedPermissions()
        }.getOrNull() ?: return null

        if (granted.containsAll(requiredPermissions)) {
            return processorGateway()
        }

        if (requestPermissions) {
            pendingHistoryMetrics = requestedMetrics
            publishUiFeedback("Vraag Health Connect permissies aan voor $reason...")
            requestHealthConnectPermissions.launch(requiredPermissions)
        }
        return null
    }

    private suspend fun requestHealthConnectPermissionsIfNeeded(
        requestedMetrics: Set<HealthMetricType>,
        reason: String
    ) {
        healthConnectGatewayOrRequest(
            requestedMetrics = requestedMetrics,
            requestPermissions = true,
            reason = reason
        )?.let {
            publishUiFeedback("Health Connect permissies zijn al actief.")
        }
    }

    private fun processorGateway(): HealthConnectGateway = HealthConnectGateway(
        context = this,
        eventBus = AppBus.events
    )

    private fun publishUiFeedback(message: String) {
        lifecycleScope.launch {
            AppBus.events.publish(
                FrontendEvent.UiFeedbackRequested(
                    message = message,
                    emittedAtEpochMillis = System.currentTimeMillis()
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        healthConnectLiveSyncSubscription?.stop()
        healthConnectLiveSyncSubscription = null
    }

    companion object {
        private const val TAG = "PersonalHealthSync"
        private const val HEALTH_CONNECT_PROVIDER_PACKAGE = "com.google.android.apps.healthdata"
        private const val ONE_YEAR_MILLIS = 365L * 24L * 60L * 60L * 1000L
        private const val LIVE_SYNC_LOOKBACK_MILLIS = 24L * 60L * 60L * 1000L
        private val SYNC_METRICS = setOf(
            HealthMetricType.STEPS,
            HealthMetricType.HEART_RATE_BPM,
            HealthMetricType.SLEEP_DURATION_MINUTES,
            HealthMetricType.ACTIVE_ENERGY_KCAL,
            HealthMetricType.BODY_WEIGHT_KG,
            HealthMetricType.HEIGHT_CM,
            HealthMetricType.BODY_FAT_PERCENTAGE,
            HealthMetricType.MUSCLE_MASS_KG,
            HealthMetricType.WATER_MASS_KG,
            HealthMetricType.WATER_PERCENTAGE,
            HealthMetricType.BODY_MASS_INDEX,
            HealthMetricType.BASAL_METABOLIC_RATE_KCAL,
            HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG,
            HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG,
            HealthMetricType.BLOOD_GLUCOSE_MGDL,
            HealthMetricType.OXYGEN_SATURATION_PERCENTAGE,
            HealthMetricType.BODY_TEMPERATURE_CELSIUS,
            HealthMetricType.HYDRATION_ML,
            HealthMetricType.DIETARY_ENERGY_KCAL
        )
    }
}
