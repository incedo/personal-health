package com.incedo.personalhealth.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthChangeSignal
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthHistoryImportRequest
import com.incedo.personalhealth.core.health.HealthHistoryImporter
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthLiveSyncProcessor
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthSignalSubscription
import com.incedo.personalhealth.core.health.buildTodayStepsSnapshot
import com.incedo.personalhealth.integration.healthconnect.HealthConnectGateway
import com.incedo.personalhealth.integration.healthconnect.HealthConnectPollingSignalSource
import com.incedo.personalhealth.shared.AppBus
import com.incedo.personalhealth.shared.PersonalHealthApp
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    private val requiredPermissions: Set<String> by lazy { HealthConnectGateway.requiredPermissions() }
    private var healthConnectLiveSyncSubscription: HealthSignalSubscription? = null
    private var pendingHistoryMetrics: Set<HealthMetricType> = SYNC_METRICS
    private var startImportAfterPermissionGrant: Boolean = true

    private val requestHealthConnectPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(requiredPermissions)) {
            if (startImportAfterPermissionGrant) {
                publishUiFeedback("Health Connect permissies verleend, import start.")
                startAndroidHealthSync(metrics = pendingHistoryMetrics)
            } else {
                publishUiFeedback("Health Connect permissies verleend.")
                refreshTodayStepsForDashboard()
            }
        } else {
            publishUiFeedback("Health Connect permissies niet verleend.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalHealthApp()
        }
        observeUiHealthSyncRequests()
        refreshTodayStepsForDashboard()
        publishUiFeedback("Klaar. Gebruik 'Geef permissies' of 'Importeer historie' om te starten.")
    }

    private fun ensureHealthConnectPermissionsAndSync(
        requestedMetrics: Set<HealthMetricType>
    ) {
        when (HealthConnectClient.getSdkStatus(this, HEALTH_CONNECT_PROVIDER_PACKAGE)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                publishUiFeedback("Health Connect niet beschikbaar op dit toestel.")
                return
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                publishUiFeedback("Health Connect update/installatie vereist.")
                openHealthConnectOnPlayStore()
                return
            }
            HealthConnectClient.SDK_AVAILABLE -> Unit
            else -> {
                publishUiFeedback("Onbekende Health Connect status.")
                return
            }
        }
        if (!HealthConnectGateway.isAvailable(this)) {
            publishUiFeedback("Health Connect niet beschikbaar.")
            return
        }
        pendingHistoryMetrics = requestedMetrics
        startImportAfterPermissionGrant = true

        val client = HealthConnectClient.getOrCreate(this)
        lifecycleScope.launch {
            val granted = client.permissionController.getGrantedPermissions()
            publishUiFeedback("Reeds verleende HC permissies: ${granted.size}/${requiredPermissions.size}")
            if (granted.containsAll(requiredPermissions)) {
                publishUiFeedback("Health Connect permissies OK, import start.")
                startAndroidHealthSync(metrics = requestedMetrics)
            } else {
                publishUiFeedback("Vraag Health Connect permissies aan...")
                requestHealthConnectPermissions.launch(requiredPermissions)
            }
        }
    }

    private fun requestHealthConnectPermissionsOnly() {
        when (HealthConnectClient.getSdkStatus(this, HEALTH_CONNECT_PROVIDER_PACKAGE)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                publishUiFeedback("Health Connect niet beschikbaar op dit toestel.")
                return
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                publishUiFeedback("Health Connect update/installatie vereist.")
                openHealthConnectOnPlayStore()
                return
            }
            HealthConnectClient.SDK_AVAILABLE -> Unit
            else -> {
                publishUiFeedback("Onbekende Health Connect status.")
                return
            }
        }
        if (!HealthConnectGateway.isAvailable(this)) {
            publishUiFeedback("Health Connect is niet beschikbaar op dit toestel.")
            return
        }
        startImportAfterPermissionGrant = false
        val client = HealthConnectClient.getOrCreate(this)
        lifecycleScope.launch {
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(requiredPermissions)) {
                publishUiFeedback("Health Connect permissies zijn al verleend.")
            } else {
                publishUiFeedback("Vraag Health Connect permissies aan...")
                requestHealthConnectPermissions.launch(requiredPermissions)
            }
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
            val gateway = HealthConnectGateway(context = this@MainActivity, eventBus = AppBus.events)
            val nowEpochMillis = System.currentTimeMillis()
            val importer = HealthHistoryImporter(
                gateway = gateway,
                source = HealthDataSource.HEALTH_CONNECT,
                eventBus = AppBus.events
            )

            runCatching {
                val records = importer.import(
                    HealthHistoryImportRequest(
                        metrics = metrics,
                        startEpochMillis = nowEpochMillis - ONE_YEAR_MILLIS,
                        endEpochMillis = nowEpochMillis
                    )
                )
                publishTodayStepsFromHealthConnect(gateway)
                publishUiFeedback("Historie import klaar: ${records.size} records.")
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
                        publishUiFeedback("Importverzoek ontvangen, controleer permissies...")
                        ensureHealthConnectPermissionsAndSync(requestedMetrics = event.metrics)
                    }
                    is HealthEvent.PermissionsRequested -> {
                        publishUiFeedback("Permissieverzoek ontvangen...")
                        requestHealthConnectPermissionsOnly()
                    }
                    is HealthEvent.HealthConnectSettingsRequested -> {
                        publishUiFeedback("Open Health Connect instellingen...")
                        openHealthConnectSettings()
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
                val gateway = HealthConnectGateway(context = this@MainActivity, eventBus = AppBus.events)
                publishTodayStepsFromHealthConnect(gateway)
            }
                .onFailure { error ->
                    Log.e(TAG, "Live sync processing failed", error)
                    publishUiFeedback("Live sync fout: ${error.message ?: "onbekende fout"}")
                }
        }
    }

    private fun refreshTodayStepsForDashboard() {
        lifecycleScope.launch {
            if (!HealthConnectGateway.isAvailable(this@MainActivity)) return@launch

            val client = HealthConnectClient.getOrCreate(this@MainActivity)
            val granted = runCatching {
                client.permissionController.getGrantedPermissions()
            }.getOrNull() ?: return@launch

            if (!granted.containsAll(requiredPermissions)) return@launch

            val gateway = HealthConnectGateway(context = this@MainActivity, eventBus = AppBus.events)
            publishTodayStepsFromHealthConnect(gateway)
        }
    }

    private suspend fun publishTodayStepsFromHealthConnect(
        gateway: HealthConnectGateway
    ) {
        val zoneId = ZoneId.systemDefault()
        val now = Instant.now()
        val startOfDay = now.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant()
        val snapshot = buildTodayStepsSnapshot(
            records = gateway.readRecords(
                HealthReadRequest(
                    metrics = setOf(HealthMetricType.STEPS),
                    startEpochMillis = startOfDay.toEpochMilli(),
                    endEpochMillis = now.toEpochMilli(),
                    limit = 10_000
                )
            ),
            dayStartEpochMillis = startOfDay.toEpochMilli(),
            dayEndEpochMillis = now.toEpochMilli(),
            bucketSizeHours = 1
        )
        AppBus.events.publish(
            FrontendEvent.TodayStepsUpdated(
                totalSteps = snapshot.totalSteps,
                buckets = snapshot.buckets.map { bucket ->
                    FrontendEvent.StepBucket(
                        label = bucket.label,
                        steps = bucket.steps
                    )
                },
                emittedAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

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

    private fun openHealthConnectSettings() {
        val healthConnectSettingsIntent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val healthConnectAppInfoIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$HEALTH_CONNECT_PROVIDER_PACKAGE")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        runCatching {
            startActivity(healthConnectSettingsIntent)
            publishUiFeedback("Health Connect instellingen geopend.")
        }.recoverCatching {
            startActivity(healthConnectAppInfoIntent)
            publishUiFeedback("Health Connect app-instellingen geopend.")
        }.recoverCatching {
            openHealthConnectOnPlayStore()
        }.onFailure {
            publishUiFeedback("Health Connect instellingen konden niet worden geopend.")
            Log.e(TAG, "Opening Health Connect settings failed", it)
        }
    }

    private fun openHealthConnectOnPlayStore() {
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$HEALTH_CONNECT_PROVIDER_PACKAGE")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$HEALTH_CONNECT_PROVIDER_PACKAGE")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        runCatching {
            startActivity(marketIntent)
        }.recoverCatching {
            startActivity(webIntent)
        }.onFailure {
            publishUiFeedback("Kon Health Connect Play Store pagina niet openen.")
            Log.e(TAG, "Opening Play Store failed", it)
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
            HealthMetricType.BODY_WEIGHT_KG
        )
    }
}
