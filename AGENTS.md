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
- Shared logic should use deterministic cross-platform tests from dedicated test modules.

9. Feature test code location.
- Keep feature production code and test code in separate sibling folders/modules.
- Use `feature/<name>` for production and `feature/<name>-test` for tests.
- Prefer test modules over placing tests directly inside production feature module source folders.

10. CI gate.
- Pull requests must pass GitHub Actions checks before merge.
- Do not merge with failing checks.

11. Keep platform bridges thin.
- Android `Activity`, iOS `UIViewController`, Desktop/Web `main` files should only bootstrap shared UI.

12. Canonical health model is required.
- Health data from platform providers (Health Connect, HealthKit) must map to one shared canonical model in `core`.
- Feature/UI layers must only consume the canonical model, never raw platform record types.

13. Event-based module communication is required.
- Cross-module communication must be event-driven via shared event contracts in `core` modules.
- Producers publish events, consumers subscribe; avoid direct feature-to-feature calls.
- Realtime in-app sync should use events first, not database polling.
- Use explicit event categories:
  - UI feedback events
  - navigation events
  - domain events (for example health records/sync)
  - sync lifecycle events (idle/syncing/up_to_date/error)

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

## Git Workflow Rules
- For every new user request, create and use a separate branch with prefix `codex/`.
- Open one pull request per request/branch.
- Keep each PR scoped to that single request; avoid mixing unrelated changes.
- Ensure required checks pass before merge.

## Documentation Rules
- Update `/docs/kmp-compose-best-practices.md` and `/README.md` when architecture or module boundaries change.
- Keep `/docs/kmp-compose-best-practices.md` aligned with adaptive-layout and multimodal-input generation rules.
- New modules must be documented in README module layout.
