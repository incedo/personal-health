# AGENTS.md

Project rules for contributors and coding agents working in this repository.

## Scope
- These rules apply to the whole repository.
- If a subdirectory contains its own `AGENTS.md`, the closest file to the changed code takes precedence.

## Kotlin MPP + Compose Rules
1. Architecture layering is mandatory.
- `apps/*` are platform entry points only.
- `shared/app` is the composition root.
- `feature/*` contains feature UI + feature state.
- `core/*` contains reusable primitives (design system, data, utils).
- `apps/*` must not be imported by `shared/*`, `feature/*`, or `core/*`.

2. Shared-first code placement.
- Put domain logic, state models, and use cases in `commonMain` by default.
- Use platform source sets (`androidMain`, `iosMain`, etc.) only for platform APIs.

3. Platform APIs behind abstractions.
- Platform integrations (permissions, file system, sensors, notifications) must be wrapped behind interfaces.
- Use DI or factory wiring in platform/app modules.

4. UI state pattern.
- Features should expose explicit UI state and event handlers.
- Avoid mutable global state and platform-specific UI logic in shared features.

5. Adaptive UI requirement.
- Mobile UIs must support phone and tablet using shared adaptive layouts and size breakpoints.

6. Dependency management.
- Versions must come from `gradle/libs.versions.toml`.
- Do not hardcode dependency versions inside module `build.gradle.kts` files.

7. Module dependency direction.
- Allowed flow: `apps -> shared -> feature -> core`.
- `feature` modules may depend on `core` modules only.
- `core` modules must not depend on `feature` or `shared` modules.

8. Testing minimum.
- Every new feature/module must include at least one deterministic automated test.
- Common logic should be tested in `commonTest` where possible.

9. CI gate.
- Pull requests must pass GitHub Actions checks before merge.
- Do not merge with failing checks.

10. Keep platform bridges thin.
- Android `Activity`, iOS `UIViewController`, Desktop/Web `main` files should only bootstrap shared UI.

## App Generation Rules (Required)
1. Generate one adaptive UI code path, not separate per-device screens.
- Build layouts from window size classes and constraints, never from hardcoded device names.

2. Support multimodal input by default.
- Every primary user action must work with touch, mouse/pointer, and keyboard.
- Include focus, hover, and accessible tap/click targets in shared UI components.

3. Use pane-based responsive layouts.
- Compact: single-pane layouts.
- Medium: two-pane list-detail layouts where relevant.
- Expanded: two or three panes with persistent navigation/rail/sidebar.

4. Include foldable-aware behavior.
- Handle window size and posture changes without losing UI state.
- Do not place critical controls across hinge/seam regions.

5. Keep behavior consistent across form factors.
- Preserve the same navigation model and domain state when resizing or changing posture.
- Change presentation density/layout only, not app semantics.

6. Enforce input and size-class testing.
- New app features must include at least one test/preview for compact and expanded layouts.
- New interactive components must validate touch and pointer behavior.

## Tooling Rules
- Use `./gradlew` wrapper, not system Gradle, for project tasks.
- Keep JDK target at 17 unless explicitly changed across the repository.

## Documentation Rules
- Update `/docs/kmp-compose-best-practices.md` and `/README.md` when architecture or module boundaries change.
- Keep `/docs/kmp-compose-best-practices.md` aligned with adaptive-layout and multimodal-input generation rules.
- New modules must be documented in README module layout.
