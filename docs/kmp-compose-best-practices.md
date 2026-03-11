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

## 7. Platform boundaries
- Android/iOS/Desktop/Web entrypoints should only start shared UI.
- Wrap platform services behind interfaces and inject implementations.
- Avoid direct platform calls from `commonMain`.

## 8. Dependency and build hygiene
- Use centralized versions from `gradle/libs.versions.toml`.
- Prefer adding dependencies to the narrowest module that needs them.
- Avoid cyclic dependencies and large cross-feature coupling.

## 9. Testing strategy
- Test shared logic in `commonTest` first.
- Keep platform tests focused on platform behavior.
- Add at least one smoke test per new feature/module.
- Validate UI behavior with a size/input matrix:
  - compact + touch
  - medium + touch
  - medium + touch+mouse
  - expanded + mouse+keyboard
  - fold/unfold transition with state preserved

## 10. CI policy
- Run automated checks on each PR and push to `main`.
- Minimum baseline: compile and test at least one target.
- Expand checks as modules/features grow.

## 11. iOS integration notes
- Shared iOS framework is exported via CocoaPods from `shared/app`.
- Keep Swift wrapper code minimal and delegate UI to shared Compose.

## 12. Documentation maintenance
- Update README module layout when adding, removing, or renaming modules.
- Keep this guide aligned with actual build configuration.
- Current feature modules in this repository include `feature/home` and `feature/onboarding`.
