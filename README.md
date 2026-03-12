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
- `feature/onboarding` Shared onboarding flow (state + adaptive UI)
- `feature/onboarding-test` Onboarding feature tests (separate test module)
- `feature/home` Home feature UI
- `feature/home-test` Home feature tests (separate test module)
- `core/designsystem` Theme and design primitives
- `core/events` App-level event bus + frontend event contracts
- `core/health` Canonical health data model + gateway contract
- `integration/health-connect` Android-only Health Connect integration
- `integration/healthkit` iOS-only HealthKit integration

Health live sync architecture:
- platform modules detect changes (`polling` on Health Connect, `observer` on HealthKit)
- shared `core/health` normalizes this via `HealthChangeSignalSource` + `HealthLiveSyncProcessor`
- app modules consume uniform intent events:
  - `HealthEvent.LiveSyncIntentReceived`
  - `HealthEvent.LiveSyncIntentSkippedDuplicate`
  - `HealthEvent.LiveSyncIntentApplied`
  - `HealthEvent.LiveSyncIntentFailed`

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

## Rules and Documentation

- Project rules for contributors and coding agents: `AGENTS.md`
- Kotlin MPP + Compose best practices: `docs/kmp-compose-best-practices.md`
- Includes adaptive generation rules for desktop/tablet/mobile/foldables with touch + mouse support
- Requirements: `docs/requirements.md`
- Health integration overview: `docs/health-integrations.md`
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
