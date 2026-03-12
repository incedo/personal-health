# Health Integrations

## Canonical model
- Shared model lives in `core/health`.
- All platform records map to:
  - `HealthRecord`
  - `HealthMetricType`
  - `HealthDataSource`
  - `HealthReadRequest`
  - `HealthDataGateway`
- Canonical domain/type mapping (Health Connect + HealthKit):
  - `docs/unified-health-model-mapping.md`

## Event-based sync
- Shared event contracts and bus live in `core/events`:
  - `AppEvent`
  - `AppEventBus`
  - `InMemoryAppEventBus`
- Frontend event categories include:
  - `FrontendEvent.UiFeedbackRequested`
  - `FrontendEvent.NavigationChanged`
  - `FrontendEvent.SyncStateChanged`
- Health-specific domain events live in `core/health`:
  - `HealthEvent`
- Integration gateways publish `HealthEvent.RecordsRead` after read operations.
- Live sync now uses a dedicated abstraction layer in `core/health`:
  - `HealthChangeSignalSource` for platform-specific change detection
  - `HealthLiveSyncProcessor` for shared sync behavior and event publishing
  - Intent-style events:
    - `HealthEvent.LiveSyncIntentReceived`
    - `HealthEvent.LiveSyncIntentSkippedDuplicate`
    - `HealthEvent.LiveSyncIntentApplied`
    - `HealthEvent.LiveSyncIntentFailed`
  - Metric-specific intent events (current implemented metrics):
    - `HealthEvent.LiveSyncStepsIntentApplied`
    - `HealthEvent.LiveSyncHeartRateIntentApplied`
    - `HealthEvent.LiveSyncSleepIntentApplied`
    - `HealthEvent.LiveSyncActiveEnergyIntentApplied`
    - `HealthEvent.LiveSyncBodyWeightIntentApplied`
- Features can subscribe to `AppEventBus.events` for realtime in-app sync without DB polling.
- Note: cross-device or cross-user realtime sync still needs network transport/backend eventing; the in-memory bus covers in-process module sync.

## Android: Health Connect
- Module: `integration/health-connect` (Android-only)
- Gateway: `HealthConnectGateway`
- Change source: `HealthConnectPollingSignalSource` (platform polling encapsulated in Android module)
- Current scope:
  - Reads historical data for:
    - `STEPS`
    - `HEART_RATE_BPM`
    - `SLEEP_DURATION_MINUTES`
    - `ACTIVE_ENERGY_KCAL`
    - `BODY_WEIGHT_KG`
  - Uses paging internally for long history ranges
  - Exposes `requiredPermissions()` for supported read types
  - Exposes `isAvailable(context)`
  - Publishes `HealthEvent.RecordsRead` after read operations

## iOS: HealthKit
- Module: `integration/healthkit` (iOS-only)
- Gateway: `HealthKitGateway`
- Change source: `HealthKitObserverSignalSource` (`HKObserverQuery` encapsulated in iOS module)
- Current scope:
  - Detects HealthKit availability
  - Reads historical data for:
    - `STEPS`
    - `HEART_RATE_BPM`
    - `SLEEP_DURATION_MINUTES`
    - `ACTIVE_ENERGY_KCAL`
    - `BODY_WEIGHT_KG`
  - Uses date-range sample queries and maps all records to canonical `HealthRecord`
  - Publishes `HealthEvent.RecordsRead` after read operations
  - App layer remains responsible for requesting HealthKit read permissions before calling gateway reads

## Wiring
- `shared/app` depends on:
  - `core/events` in `commonMain`
  - `core/health` in `commonMain`
  - `integration/health-connect` in `androidMain`
  - `integration/healthkit` in `iosMain`

## Shared history import orchestration
- `core/health` includes:
  - `HealthHistoryImportRequest`
  - `HealthHistoryImporter`
- Importer behavior:
  - Splits large ranges into deterministic batch windows
  - Reads each window through `HealthDataGateway`
  - Emits `FrontendEvent.SyncStateChanged` with `SYNCING`, then `UP_TO_DATE` or `ERROR`

## Shared live sync orchestration
- `core/health` also includes:
  - `HealthChangeSignal`
  - `HealthChangeTrigger`
  - `HealthLiveSyncProcessor`
  - `HealthSyncCheckpointStore`
- Processor behavior:
  - Accepts platform `HealthChangeSignal` events from the signal source
  - Uses intent-style eventing with explicit event names
  - Publishes metric-specific events through a metric-event factory registry (modular dispatch)
  - Uses `intentId` + `HealthSyncCheckpointStore` to skip duplicate intent processing
  - Performs bounded read from `HealthDataGateway` using configured lookback
  - Emits `FrontendEvent.SyncStateChanged` for `health-live-sync:*` channels
  - Emits intent-style health events for UI/features to consume immediately
