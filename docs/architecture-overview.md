# Architecture Overview

This document shows how the project is wired across platforms, shared modules, integrations, and cross-module patterns.

## High-level structure

```mermaid
flowchart TB
    subgraph Platforms["Platform entry points"]
        Android["apps/android"]
        IOS["apps/ios"]
        Desktop["apps/desktop"]
        Web["apps/web"]
    end

    subgraph Shared["Shared composition root"]
        App["shared/app<br/>Compose app shell + wiring"]
    end

    subgraph Features["Feature modules"]
        Home["feature/home<br/>Home, coach, news UI"]
        Onboarding["feature/onboarding<br/>Onboarding flow UI"]
        HomeTest["feature/home-test"]
        OnboardingTest["feature/onboarding-test"]
    end

    subgraph Core["Core shared modules"]
        Design["core/designsystem"]
        Events["core/events<br/>AppEventBus + frontend events"]
        Health["core/health<br/>Canonical health model + gateway"]
        Wellbeing["core/wellbeing<br/>App usage / screen time"]
        Goals["core/goals"]
        Coaches["core/coaches"]
        OnboardingCore["core/onboarding"]
        Recommendations["core/recommendations<br/>Recommendation API + stub"]
        NewsSocial["core/newssocial<br/>News/social API + rotating stub"]
        Media["core/media"]
    end

    subgraph Integrations["Platform integration modules"]
        HealthConnect["integration/health-connect<br/>Android Health Connect"]
        AppUsage["integration/app-usage<br/>Android UsageStats"]
        Samsung["integration/samsung-health<br/>Android vendor bridge"]
        HealthKit["integration/healthkit<br/>iOS HealthKit"]
    end

    Android --> App
    IOS --> App
    Desktop --> App
    Web --> App

    App --> Home
    App --> Onboarding
    App --> Events
    App --> Health
    App --> Wellbeing
    App --> Goals
    App --> OnboardingCore
    App --> Recommendations
    App --> NewsSocial
    App --> Design

    Home --> Coaches
    Home --> Goals
    Home --> Media
    Home --> NewsSocial
    Home --> Recommendations
    Home --> Design

    Onboarding --> OnboardingCore
    Onboarding --> Design
    HomeTest -. verifies .-> Home
    OnboardingTest -. verifies .-> Onboarding

    Health --> Events

    HealthConnect --> Health
    HealthConnect --> Events
    HealthKit --> Health
    HealthKit --> Events
    Samsung --> Health
    AppUsage --> Wellbeing

    App -. Android source set .-> HealthConnect
    App -. iOS source set .-> HealthKit
```

## Integration and data patterns

```mermaid
flowchart LR
    NativeSource["Native data sources<br/>Health Connect / HealthKit / Samsung / UsageStats"] --> Integration["Integration modules"]
    Integration --> Canonical["Canonical shared contracts<br/>core/health or core/wellbeing"]
    Canonical --> Events["Event streams<br/>core/events + HealthEvent"]
    Canonical --> StubApi["Backend-ready APIs with stubs<br/>core/recommendations / core/newssocial"]
    Events --> App["shared/app"]
    StubApi --> App
    App --> Feature["feature/* UI state + handlers"]
    Feature --> Adaptive["Shared adaptive Compose UI<br/>phone / tablet / desktop / web"]
```

## What each layer owns

- `apps/*` boot the platform and stay thin.
- `shared/app` is the composition root and decides which integrations are wired per platform source set.
- `feature/*` owns UI state, screen composition, and user interactions.
- `core/*` owns reusable contracts, canonical models, event contracts, and deterministic stub APIs.
- `integration/*` adapts platform SDKs and vendor APIs into shared core contracts.

## Current integration map

- Android health data enters through `integration/health-connect`.
- iOS health data enters through `integration/healthkit`.
- Android screen time / app usage enters through `integration/app-usage`.
- Samsung Health is prepared as an Android-only vendor bridge in `integration/samsung-health`.
- Recommendation-of-the-day and daily insights are provided through `core/recommendations`.
- News, social, gallery, and video feed content is provided through `core/newssocial`.

## Patterns used in this repository

- Shared-first Kotlin Multiplatform code in `commonMain`.
- One-way dependency flow: `apps -> shared -> feature -> core`.
- Platform APIs hidden behind integration modules and shared contracts.
- Canonical health model in `core/health` so feature code never depends on raw provider records.
- Event-based module communication via `core/events` and `HealthEvent`.
- Backend-ready API boundaries with stub implementations for features that are not backed by a live service yet.
- One adaptive Compose UI path instead of separate per-device screens.

## Reader guidance

- Read [README.md](/Users/kees/data/projects/personal-health/README.md) for the module inventory.
- Read [docs/kmp-compose-best-practices.md](/Users/kees/data/projects/personal-health/docs/kmp-compose-best-practices.md) for architectural rules and adaptive UI constraints.
- Read [docs/health-integrations.md](/Users/kees/data/projects/personal-health/docs/health-integrations.md) for the detailed health ingestion and event model.
