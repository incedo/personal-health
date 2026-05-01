# Personal Health UI Refactor Todo

Checklist voor de UI-refactor op basis van de aangeleverde designs uit `Personal Health.zip`.
Gebruik dit bestand als terugvalpunt voor latere schermen in dezelfde stijl.

## Werkafspraken

- [ ] Maak per nieuwe UI-refactor request een aparte `codex/` branch.
- [ ] Houd de bestaande architectuurlagen aan: `apps -> shared -> feature -> core`.
- [ ] Plaats gedeelde styling, componenten en primitives in `core/designsystem`.
- [ ] Plaats feature-specifieke UI in de juiste `feature/*` module.
- [ ] Houd shared/domain/state logic in `commonMain`, tenzij platform APIs nodig zijn.
- [ ] Houd Kotlin en Gradle bestanden die we aanraken op maximaal 300 regels.
- [ ] Update `README.md` en `docs/kmp-compose-best-practices.md` als modulegrenzen of architectuur wijzigen.

## Design System Regel

- [ ] Als tijdens implementatie blijkt dat een scherm een stijl, layout primitive, component variant, spacing, shape, typography style, chart/gauge, input state of interactie nodig heeft die nog niet goed in `core/designsystem` zit, meld dit expliciet voordat dezelfde stijl lokaal wordt gedupliceerd.
- [ ] Refactor zo'n herbruikbaar stuk eerst naar het design system en rebuild/test daarna de betrokken schermen.
- [ ] Vermijd lokale palettes, hardcoded hex-kleuren, losse dp/sp-schalen en per-scherm componentvarianten wanneer ze onderdeel zijn van de gedeelde visuele taal.

## Fase 1: Design Input Canoniseren

- [x] Pak de design input uit naar een vaste referentielocatie, bijvoorbeeld `docs/design/personal-health-v1/`.
- [x] Documenteer welke bestanden leidend zijn:
  - [x] `tokens.css`
  - [x] `kotlin/PersonalHealthTheme.kt`
  - [x] `components.jsx`
  - [x] `dashboard.jsx`
  - [x] `onboarding.jsx`
  - [x] `profile-plan.jsx`
  - [x] `screens.jsx`
- [ ] Leg in `docs/kmp-compose-best-practices.md` vast hoe designs naar Compose worden vertaald.
- [x] Noteer dat JSX-designs referentie zijn, geen code die blind wordt overgenomen.

## Fase 2: Design System Tokens

- [ ] Vervang de minimale `PersonalHealthTheme` in `core/designsystem` door een volledige tokenlaag.
- [ ] Voeg `PhTheme.colors` toe.
- [ ] Voeg `PhTheme.typography` toe.
- [ ] Voeg `PhTheme.spacing` toe.
- [ ] Voeg `PhTheme.shapes` toe.
- [ ] Voeg `PhTheme.elevation` toe.
- [ ] Voeg `PhTheme.motion` toe.
- [ ] Map de tokens naar Material 3 `ColorScheme` en `Typography`.
- [ ] Ondersteun light en dark theme.
- [ ] Verwijder of migreer feature-lokale palettes waar ze overlappen met het design system.

## Fase 3: Design System Componenten

- [ ] Voeg `PhButton` toe.
- [ ] Voeg `PhIconButton` toe.
- [ ] Voeg `PhTextField` toe.
- [ ] Voeg `PhToggle` toe.
- [ ] Voeg `PhTag` toe.
- [ ] Voeg `PhCard` toe.
- [ ] Voeg `PhSegmentedControl` toe.
- [ ] Voeg `PhSectionHeader` toe.
- [ ] Voeg `PhListRow` toe.
- [ ] Voeg `PhMetricCard` toe.
- [ ] Voeg chart primitives toe:
  - [ ] Ring gauge
  - [ ] Triple ring
  - [ ] Sparkline
  - [ ] Bars
  - [ ] Heatmap
  - [ ] Zone bar
- [ ] Zorg dat primary actions touch, pointer en keyboard ondersteunen.
- [ ] Zorg voor focus, hover en disabled states.
- [ ] Voeg compacte en expanded previews/tests toe voor nieuwe interactieve componenten.

## Fase 4: Home/Dashboard Refactor

- [ ] Migreer `HomeChrome.kt` naar `PhTheme`.
- [ ] Verwijder lokale kleuren waar design-system tokens bestaan.
- [ ] Bouw dashboarddelen uit `dashboard.jsx` na in Compose:
  - [ ] Today hero
  - [ ] Vitals
  - [ ] Weekly volume
  - [ ] Sleep panel
  - [ ] Plan card
  - [ ] Muscle balance
  - [ ] Body trend
  - [ ] Recent sessions
  - [ ] Consistency card
- [ ] Houd bestaande home state, callbacks en domeinmodellen intact.
- [ ] Splits grote composables per verantwoordelijkheid.
- [ ] Behoud compact, medium en expanded layouts als een adaptief codepad.
- [ ] Voeg of update home layout smoke tests.

## Fase 5: Onboarding Refactor

- [ ] Gebruik `onboarding.jsx` als flow- en layoutreferentie.
- [ ] Houd bestaande `OnboardingUiState` en reducer zoveel mogelijk intact.
- [ ] Refactor onboarding shell naar design-system componenten.
- [ ] Refactor progress, choice cards, footer actions en step layouts.
- [ ] Ondersteun compact, medium en expanded zonder aparte device-specifieke schermen.
- [ ] Behoud keyboard bediening voor primary action.
- [ ] Voeg of update compact en expanded layout checks.

## Fase 6: Profile en Plan Refactor

- [ ] Gebruik `profile-plan.jsx` als referentie.
- [ ] Refactor `HomeProfileSection` naar de nieuwe stijl.
- [ ] Voeg profile preferences, privacy/export en account sections toe via design-system primitives.
- [ ] Refactor plan/coach-gerelateerde schermen waar ze visueel bij de nieuwe stijl horen.
- [ ] Houd communicatie event-driven en voorkom feature-to-feature calls.
- [ ] Voeg of update tests voor profile/plan state en layout.

## Fase 7: Nieuwe Schermen Later

- [ ] Maak per nieuw scherm een kleine screen recipe.
- [ ] Noteer welke tokens gebruikt worden.
- [ ] Noteer welke bestaande design-system componenten gebruikt worden.
- [ ] Noteer welke nieuwe design-system componenten of varianten nodig zijn.
- [ ] Noteer welke feature-state al bestaat of ontbreekt.
- [ ] Noteer welke compact, medium en expanded layouts nodig zijn.
- [ ] Implementeer nieuwe schermen bovenop het design system, niet met lokale stijlduplicatie.

## Verificatie

- [ ] Draai `./gradlew lineCountCheck`.
- [ ] Draai relevante feature tests.
- [ ] Draai relevante compile targets voor Android, Desktop en Web.
- [ ] Controleer dat touched files binnen de 300-regel afspraak blijven.
- [ ] Controleer dat `commonMain` platformneutraal blijft.
- [ ] Controleer dat nieuwe modules of architectuurwijzigingen in README en docs staan.

## Voorgestelde PR Volgorde

- [ ] `codex/design-system-tokens`: theme, tokens, primitives en docs.
- [ ] `codex/home-ui-refactor`: dashboard/home naar nieuwe stijl.
- [ ] `codex/onboarding-ui-refactor`: onboarding flow naar nieuwe stijl.
- [ ] `codex/profile-plan-ui-refactor`: profile en plan schermen.
- [ ] Per later scherm: aparte branch en PR bovenop het design system.
