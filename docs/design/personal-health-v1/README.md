# Personal Health Design Reference v1

This folder stores the design input from `Personal Health.zip` so UI refactor work can continue across sessions without depending on the original download.

## Source Files

- `tokens.css`: canonical color, typography, spacing, radius, motion, and visualization token reference.
- `kotlin/PersonalHealthTheme.kt.reference`: generated Kotlin theme reference to translate into `core/designsystem`. It keeps a non-source extension so repository line-count checks do not treat the generated reference as production Kotlin.
- `components.jsx`: component behavior and visual examples for shared design-system primitives.
- `dashboard.jsx`: dashboard/home screen reference.
- `onboarding.jsx`: onboarding flow reference.
- `profile-plan.jsx`: profile, preferences, export, and plan screen reference.
- `screens.jsx`: broader screen composition reference.
- `design-canvas.jsx`, `ios-frame.jsx`, `tweaks-panel.jsx`, and `Personal Health Design System.html`: supporting design preview files.

## Usage

- Treat these files as design references, not production source.
- Prefer translating repeated patterns into `core/designsystem` before applying them in feature modules.
- If a needed primitive, token, state, or variant is missing from the design system, call that out and refactor it there first before duplicating local styles.
- Keep implementation aligned with the checklist in `/todo.md`.
