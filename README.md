# personal-health

Kotlin multi-module Compose project with a modular architecture for:

- Android mobile (phone + tablet via responsive layout)
- iOS mobile
- Desktop (JVM)
- Web (Wasm)

## Module layout

- `apps/android` Android app entry point
- `apps/ios` iOS integration setup (Xcode + CocoaPods)
- `apps/desktop` Desktop app entry point
- `apps/web` Web app entry point
- `shared/app` Shared app shell, iOS framework export, and composition root
- `feature/onboarding` Shared onboarding flow UI
- `feature/onboarding-test` Onboarding feature tests (separate test module)
- `feature/home` Home feature UI
- `feature/home-test` Home feature tests (separate test module)
- `core/coaches` Shared coach directory model, selection rules, and persistence
- `core/designsystem` Theme and design primitives
- `core/events` App-level event bus + frontend event contracts
- `core/goals` Shared coach goals model and add/update rules
- `core/health` Canonical health data model + gateway contract
- `core/media` Shared video/news media catalog and backend-ready repository contracts
- `core/onboarding` Shared onboarding state, reducer, and goal contract
- `core/wellbeing` Shared wellbeing model for app usage and screen-time summaries
- `integration/health-connect` Android-only Health Connect integration
- `integration/app-usage` Android-only UsageStats integration for screen time and selected social apps
- `integration/samsung-health` Android-only Samsung Health Data SDK gateway preparation
- `integration/healthkit` iOS-only HealthKit integration

Local activity persistence direction:
- shared storage contract lives in `core/health`
- native targets should use one shared SQLite schema
- web should prefer SQLite WASM with IndexedDB fallback behind the same repository contract

Health live sync architecture:
- platform modules detect changes (`polling` on Health Connect, `observer` on HealthKit)
- shared `core/health` normalizes this via `HealthChangeSignalSource` + `HealthLiveSyncProcessor`
- app modules consume uniform intent events:
  - `HealthEvent.LiveSyncIntentReceived`
  - `HealthEvent.LiveSyncIntentSkippedDuplicate`
  - `HealthEvent.LiveSyncIntentApplied`
  - `HealthEvent.LiveSyncIntentFailed`

Optional body capture direction:
- future optional feature for `2D` and `3D` body capture
- `2D` flow uses standard device cameras for guided front/side/back body capture
- `3D` flow can use supported depth cameras or body-tracker sensors where hardware is available
- intended outputs are normalized shared body posture / measurement data instead of raw vendor SDK types
- reference products:
  - [OpenCap](https://www.opencap.ai/get-started)
  - [Moverse](https://moverse.ai/)

## Run

```bash
./gradlew :apps:android:assembleDebug
./gradlew :apps:desktop:run
./gradlew :apps:web:wasmJsBrowserDevelopmentRun
```

## Local Quality Gate

Gebruik de lokale quality gate runner:

```bash
./scripts/quality-gate-local.sh
```

- Draait altijd eerst `qualityGateBase` (tests, compile checks, lint).
- Vraagt daarna of visual simulator tests moeten meedraaien (`ja/nee`).
- Geen antwoord binnen 10 seconden = visual tests overslaan.
- Feature tests draaien vanuit losse sibling testmodules (`feature/*-test`), niet vanuit productie feature folders.
- Gebruik `./gradlew lineCountCheck` om af te dwingen dat nieuwe of aangepaste Kotlin/Gradle bronbestanden niet boven 300 regels uitkomen.

Visual tools in deze setup:
- Paparazzi (`:apps:android:verifyPaparazziDebug`)
- Shot (`:apps:android:executeScreenshotTests`)
- Maestro (`.maestro/flow_smoke.yaml`)

## iOS

Generate iOS framework/pod artifacts from shared module:

```bash
./gradlew :shared:app:podspec
```

Then follow setup notes in `apps/ios/README.md`.

Android phone/tablet behavior is handled in shared Compose UI with width-based adaptive layouts.

## Samsung Health Data SDK

- Local Android voorbereiding staat in `integration/samsung-health`.
- Voor echte Samsung Health Data SDK reads moet je de officiele Samsung SDK-binary lokaal toevoegen en developer mode in Samsung Health aanzetten.
- De architectuur is al voorbereid als aanvullende Android datasource naast Health Connect.

## Rules and Documentation

- Project rules for contributors and coding agents: `AGENTS.md`
- Kotlin MPP + Compose best practices: `docs/kmp-compose-best-practices.md`
- Includes adaptive generation rules for desktop/tablet/mobile/foldables with touch + mouse support
- Canonical navigation guidance: `docs/navigation-principles.md`
- Requirements: `docs/requirements.md`
- Health integration overview: `docs/health-integrations.md`
- Local activity persistence plan: `docs/local-activity-storage.md`
- Canonical browser import format: `docs/canonical-health-import-format.md`
- Unified Health Connect + HealthKit mapping: `docs/unified-health-model-mapping.md`
- Testing + coverage strategy: `docs/testing-coverage-strategy.md`

## Git Workflow

- Use one branch per request, with branch name prefix `codex/`.
- Use one PR per request/branch, scoped to that request only.

## Coverage

- Generate coverage reports:
  - `./gradlew koverXmlReport koverHtmlReport --no-daemon`
- Verify coverage gate:
  - `./gradlew koverVerify --no-daemon`
- Show module-vs-target summary:
  - `./scripts/coverage-summary.sh`
