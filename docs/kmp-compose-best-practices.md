# Kotlin MPP + Compose Best Practices

This document defines project conventions for Android (phone/tablet), iOS, Desktop, and Web.

## 1. Modular architecture
- Separate by responsibility:
  - `apps/*`: platform launchers and integration glue.
  - `shared/app`: composition root, app shell.
  - `feature/*`: feature-specific UI/state/use-cases.
  - `core/*`: reusable cross-feature building blocks.
- Keep dependency direction one-way: `apps -> shared -> feature -> core`.

## 2. Shared-first development
- Start in `commonMain`.
- Move code to platform source sets only when platform APIs are required.
- Keep business logic platform-agnostic.

## 3. Compose UI and state
- Use unidirectional data flow for features:
  - immutable `UiState`
  - explicit user events/actions
  - clear state reducers/update handlers
- Keep composables mostly stateless; hoist state to feature boundaries.

## 4. Adaptive design (phone/tablet/desktop)
- Implement responsive layout decisions in shared Compose code.
- Use width breakpoints for phone/tablet behavior.
- Avoid duplicating entire screens per device type unless unavoidable.
- Recommended baseline:
  - compact: width < 600dp
  - medium: width 600dp..839dp
  - expanded: width >= 840dp
- Use pane strategy by size class:
  - compact: single pane
  - medium: list-detail/two pane when relevant
  - expanded: two or three panes, persistent navigation on large screens

## 5. Multimodal input (touch + mouse + keyboard)
- Design all primary actions to work with touch, pointer, and keyboard.
- Ensure interactive elements provide:
  - touch-friendly target size
  - focus states for keyboard navigation
  - hover states/tooling for pointer devices
- Do not create touch-only or mouse-only critical flows.

## 6. Foldables and posture changes
- Treat fold/unfold and resize events as normal runtime state transitions.
- Preserve navigation and feature state across posture changes.
- Avoid placing critical controls/content across hinge or seam regions.

## 6a. Adaptive navigation
- Treat `docs/navigation-principles.md` as the canonical navigation source.
- Keep implementation aligned with that document instead of redefining navigation rules here.

## 7. Platform boundaries
- Android/iOS/Desktop/Web entrypoints should only start shared UI.
- Wrap platform services behind interfaces and inject implementations.
- Avoid direct platform calls from `commonMain`.

## 8. Dependency and build hygiene
- Use centralized versions from `gradle/libs.versions.toml`.
- Prefer adding dependencies to the narrowest module that needs them.
- Avoid cyclic dependencies and large cross-feature coupling.

## 9. Canonical cross-platform data model
- Use one shared canonical model for domain data in `core/*` modules.
- Platform integrations (for example Android Health Connect and iOS HealthKit) must map into that canonical model.
- Additional vendor integrations, such as Samsung Health Data SDK on Android, must also map into the same canonical model and stay isolated inside dedicated integration modules.
- UI/features must consume only canonical models, never raw platform framework types.

## 10. Event-based communication
- Use event-based communication between modules as the default integration mechanism.
- Keep event contracts in `core/*` modules and expose them via interfaces/flows.
- Publish domain events from integrations/features and subscribe in consumers for realtime state sync.
- Prefer event streams over direct database polling for in-app synchronization.
- Standardize event taxonomy at minimum:
  - UI feedback events
  - navigation events
  - domain events
  - sync lifecycle events

## 11. Testing strategy
- Keep production and test code in separate sibling modules for features:
  - production: `feature/<name>`
  - tests: `feature/<name>-test`
- Use test modules as the default for feature-level tests; use in-module `commonTest` only when technically required.
- Keep platform tests focused on platform behavior.
- Add at least one smoke test per new feature/module.
- Validate UI behavior with a size/input matrix:
  - compact + touch
  - medium + touch
  - medium + touch+mouse
  - expanded + mouse+keyboard
  - fold/unfold transition with state preserved

## 12. CI policy
- Run automated checks on each PR and push to `main`.
- Minimum baseline: compile and test at least one target.
- Expand checks as modules/features grow.

## 13. iOS integration notes
- Shared iOS framework is exported via CocoaPods from `shared/app`.
- Keep Swift wrapper code minimal and delegate UI to shared Compose.

## 14. Documentation maintenance
- Update README module layout when adding, removing, or renaming modules.
- Keep this guide aligned with actual build configuration.
- Current feature modules in this repository include `feature/home` and `feature/onboarding`.

## 15. Sports and training feature direction
- Keep `activity tracking`, `strength logging`, and `training recommendations` as separate concerns even when they appear in one user flow.
- Recommended domain split:
  - GPS and endurance activity domain in `core/*`
  - exercise taxonomy, muscle groups, and workout logging in `core/*`
  - recommendation logic in shared `core/*` or dedicated `feature/*` state modules
- Recommended feature split when implementation starts:
  - `feature/today` for daily summary, quick actions, and recommendations
  - `feature/track` for outdoor, pool, gym, and manual logging flows
  - `feature/plan` for workout suggestions and recovery guidance
  - `feature/progress` for trends, volume, and muscle balance
- Keep social sharing optional and downstream from completed activities/workouts:
  - the canonical activity or workout record should exist first
  - shareable summary models should be derived from canonical records, not stored as the source of truth
- Guidance and recommendation systems should start deterministic and explainable before introducing ML ranking or AI-generated suggestions.
