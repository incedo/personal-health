# Local Activity Storage

This project should persist activities and imported health history through one shared repository contract in `core/health`, while keeping platform providers behind platform-specific gateways.

## Current repository shape

- Canonical record model:
  - `core/health/src/commonMain/kotlin/com/incedo/personalhealth/core/health/CanonicalHealthModel.kt`
- Local storage contract:
  - `core/health/src/commonMain/kotlin/com/incedo/personalhealth/core/health/LocalActivityStore.kt`
- Platform storage recommendation entry points:
  - `core/health/src/androidMain/kotlin/com/incedo/personalhealth/core/health/PlatformLocalActivityStorage.android.kt`
  - `core/health/src/iosMain/kotlin/com/incedo/personalhealth/core/health/PlatformLocalActivityStorage.ios.kt`
  - `core/health/src/desktopMain/kotlin/com/incedo/personalhealth/core/health/PlatformLocalActivityStorage.desktop.kt`
  - `core/health/src/wasmJsMain/kotlin/com/incedo/personalhealth/core/health/PlatformLocalActivityStorage.wasmJs.kt`

## Architecture

Use the following flow:

1. Platform integration modules read provider records.
2. Gateways map provider values into canonical `HealthRecord` values.
3. Shared code persists only canonical records in `LocalActivityStore`.
4. Features read from the local store instead of reading provider SDKs directly.
5. Live sync and history import update the local store incrementally.

This keeps provider-specific APIs out of feature modules and preserves the required dependency direction:

- `apps -> shared -> feature -> core`
- platform integrations stay thin and platform-bound
- feature/UI code consumes only the shared canonical model

## Storage strategy by platform

### Android

- Primary engine: native SQLite via a shared KMP schema.
- Preferred implementation: SQLDelight or another shared SQLite abstraction.
- Fallback during early development: in-memory store for previews/tests only.
- Avoid storing raw `HealthConnect` record types outside the integration module.

### iOS

- Primary engine: native SQLite via the same shared KMP schema used on Android.
- HealthKit remains read-only integration input; canonical records are stored locally after mapping.
- Core Data is possible, but it would split the persistence implementation away from shared code and should only be chosen with a clear reason.

### Desktop

- Primary engine: SQLite with the JVM/native desktop driver matching the shared schema.
- Desktop should reuse the same repository behavior as mobile to keep query and sync semantics aligned.

### Web

- Primary engine: SQLite compiled to WebAssembly.
- Best persistence mode: SQLite WASM backed by OPFS where browser support is sufficient.
- Fallback engine: IndexedDB behind the same `LocalActivityStore` contract.
- Do not use `localStorage` for activity history beyond trivial preferences.

The web recommendation exists because browser storage support is variable:

- OPFS gives the closest match to a persistent SQLite file.
- SQLite WASM has higher startup and integration cost than native SQLite.
- IndexedDB remains the practical fallback when SQLite WASM support or packaging is not good enough for the target browser set.

## Implementation plan

Recommended next steps:

1. Add a shared database module or extend `core/health` with a real SQLite-backed `LocalActivityStore`.
2. Store canonical `HealthRecord` rows plus sync metadata such as source, last import window, and processed intent keys.
3. Update history import and live sync flows to write into the local store.
4. Move feature read paths to query the local store first and use provider imports only as ingestion.
5. Keep deterministic repository tests in shared test modules, with small platform smoke tests for driver wiring.
