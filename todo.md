# Personal Health UI Refactor Todo

Checklist voor de UI-refactor op basis van de aangeleverde designs uit `Personal Health.zip`.
Gebruik dit bestand als terugvalpunt voor latere schermen in dezelfde stijl.

## Werkafspraken

- [x] Maak per nieuwe UI-refactor request een aparte `codex/` branch.
- [x] Houd de bestaande architectuurlagen aan: `apps -> shared -> feature -> core`.
- [x] Plaats gedeelde styling, componenten en primitives in `core/designsystem`.
- [x] Plaats feature-specifieke UI in de juiste `feature/*` module.
- [x] Houd shared/domain/state logic in `commonMain`, tenzij platform APIs nodig zijn.
- [x] Houd Kotlin en Gradle bestanden die we aanraken op maximaal 300 regels.
- [x] Update `README.md` en `docs/kmp-compose-best-practices.md` als modulegrenzen of architectuur wijzigen.

## Design System Regel

- [x] Als tijdens implementatie blijkt dat een scherm een stijl, layout primitive, component variant, spacing, shape, typography style, chart/gauge, input state of interactie nodig heeft die nog niet goed in `core/designsystem` zit, meld dit expliciet voordat dezelfde stijl lokaal wordt gedupliceerd.
- [x] Refactor zo'n herbruikbaar stuk eerst naar het design system en rebuild/test daarna de betrokken schermen.
- [ ] Vermijd lokale palettes, hardcoded hex-kleuren, losse dp/sp-schalen en per-scherm componentvarianten wanneer ze onderdeel zijn van de gedeelde visuele taal.
  - [ ] Ruim de resterende `homePalette()` overlap met `PhTheme` verder op.

## Feature Toggles

- [ ] Voeg een gedeelde feature-toggle aanpak toe voor experimentele UI, A/B tests en gefaseerde rollout van nieuwe flows.
- [ ] Gebruik feature toggles om Dev/Test tools alleen beschikbaar te maken in development/test builds of expliciet ingeschakelde support-modi.
- [ ] Houd toggle-definities centraal en type-safe, zodat features niet met losse string flags of per-scherm conditionals worden verspreid.
- [ ] Leg per toggle vast: eigenaar, doel, default per environment, verwachte verwijderdatum of promotiepad.
- [ ] Voeg testdekking toe voor belangrijke toggle-combinaties, vooral waar navigatie, onboarding, profile of dev/support tools anders renderen.

## Fase 1: Design Input Canoniseren

- [x] Pak de design input uit naar een vaste referentielocatie, bijvoorbeeld `docs/design/personal-health-v1/`.
- [x] Documenteer welke bestanden leidend zijn:
  - [x] `tokens.css`
  - [x] `kotlin/PersonalHealthTheme.kt.reference`
  - [x] `components.jsx`
  - [x] `dashboard.jsx`
  - [x] `onboarding.jsx`
  - [x] `profile-plan.jsx`
  - [x] `screens.jsx`
- [x] Leg in `docs/kmp-compose-best-practices.md` vast hoe designs naar Compose worden vertaald.
- [x] Noteer dat JSX-designs referentie zijn, geen code die blind wordt overgenomen.

## Fase 2: Design System Tokens

- [x] Vervang de minimale `PersonalHealthTheme` in `core/designsystem` door een volledige tokenlaag.
- [x] Voeg `PhTheme.colors` toe.
- [x] Voeg `PhTheme.typography` toe.
- [x] Voeg `PhTheme.spacing` toe.
- [x] Voeg `PhTheme.shapes` toe.
- [x] Voeg `PhTheme.elevation` toe.
- [x] Voeg `PhTheme.motion` toe.
- [x] Map de tokens naar Material 3 `ColorScheme` en `Typography`.
- [x] Ondersteun light en dark theme.
- [ ] Verwijder of migreer feature-lokale palettes waar ze overlappen met het design system.

## Fase 3: Design System Componenten

- [x] Voeg `PhButton` toe.
- [x] Voeg `PhChoiceCard` toe.
- [x] Voeg `PhIconButton` toe.
- [x] Voeg `PhTextField` toe.
- [x] Voeg `PhToggle` toe.
- [x] Voeg `PhTag` toe.
- [x] Voeg `PhCard` toe.
- [x] Voeg `PhSegmentedControl` toe.
- [x] Voeg `PhSectionHeader` toe.
- [x] Voeg `PhListRow` toe.
- [x] Voeg `PhMetricCard` toe.
- [x] Voeg `PhAvatar` toe met masculine, feminine en neutral placeholder variants.
- [x] Voeg chart primitives toe:
  - [x] Ring gauge
  - [x] Triple ring
  - [x] Sparkline
  - [x] Bars
  - [x] Heatmap
  - [x] Zone bar
- [x] Zorg dat primary actions touch, pointer en keyboard ondersteunen.
- [x] Zorg voor focus, hover en disabled states.
- [x] Voeg compacte en expanded previews/tests toe voor nieuwe interactieve componenten.

## Fase 4: Home/Dashboard Refactor

- [x] Migreer `HomeChrome.kt` naar `PhTheme`.
- [x] Verwijder lokale kleuren waar design-system tokens bestaan.
- [x] Bouw dashboarddelen uit `dashboard.jsx` na in Compose:
  - [x] Today hero
  - [x] Vitals
  - [x] Weekly volume
  - [x] Sleep panel
  - [x] Plan card
  - [x] Muscle balance
  - [x] Body trend
  - [x] Recent sessions
  - [x] Consistency card
- [x] Houd bestaande home state, callbacks en domeinmodellen intact.
- [x] Splits grote composables per verantwoordelijkheid.
- [x] Behoud compact, medium en expanded layouts als een adaptief codepad.
- [x] Voeg of update home layout smoke tests.

## Fase 5: Onboarding Refactor

- [x] Gebruik `onboarding.jsx` als flow- en layoutreferentie.
- [x] Houd bestaande `OnboardingUiState` en reducer zoveel mogelijk intact.
- [x] Refactor onboarding shell naar design-system componenten.
- [x] Refactor progress, choice cards, footer actions en step layouts.
- [x] Ondersteun compact, medium en expanded zonder aparte device-specifieke schermen.
- [x] Behoud keyboard bediening voor primary action.
- [x] Voeg of update compact en expanded layout checks.

## Fase 6: Profile en Plan Refactor

- [x] Gebruik `profile-plan.jsx` als referentie.
- [x] Refactor `HomeProfileSection` naar de nieuwe stijl.
- [ ] Voeg profile preferences, privacy/export en account sections toe via design-system primitives.
- [x] Refactor plan/coach-gerelateerde schermen waar ze visueel bij de nieuwe stijl horen.
  - [x] Refactor trainingsprogramma/detailplan naar weekplanning, herstel-aanpassing, vandaag-sessie, mesocycle en volume-panelen.
- [x] Houd communicatie event-driven en voorkom feature-to-feature calls.
- [x] Voeg of update tests voor profile/plan state en layout.

## Fase 7: Navigatie Refactor

- [x] Gebruik de navigatievoorbeelden uit de designreferentie als leidraad voor de gedeelde app-navigatie.
- [x] Inventariseer welke navigation primitives in `core/designsystem` nodig zijn, zoals bottom navigation, navigation rail, sidebar/app shell, tab/pill navigation en overflow/menu states.
- [x] Refactor mobiele navigatie naar de designreferentie:
  - [x] Compacte bottom navigation met duidelijke actieve state.
  - [x] Touch targets, focus states en keyboard/pointer ondersteuning.
  - [ ] Geen dubbele lokale kleuren, spacing of icon states buiten het design system.
- [x] Refactor tablet navigatie naar de designreferentie:
  - [x] Medium layout met bottom navigation en zwevende profiel-avatar.
  - [x] Behoud dezelfde navigatie-state bij resize, rotatie en foldable/posture wijzigingen.
  - [ ] Controleer list-detail of twee-pane flows waar navigatie en content tegelijk zichtbaar zijn.
- [x] Refactor desktop/web/app navigatie naar de designreferentie:
  - [x] Expanded layout met topbar en duidelijke actieve sectie.
  - [x] Pointer hover, keyboard focus en shortcuts/enter-activatie voor primaire navigatie.
  - [x] Zorg dat web en desktop dezelfde gedeelde navigatie-state gebruiken.
- [x] Leg vast welke navigatie-items, labels, iconen en active/disabled/notification states canoniek zijn.
- [x] Voeg compact, medium en expanded previews/tests toe voor de navigatie-shell.
- [x] Meld vooraf wanneer de navigatie nieuwe design-system componenten of varianten nodig heeft om stijlduplicatie te voorkomen.

## Fase 8: Today Cleanup

- [x] Laat de profiel-avatar op compact/medium als overlay rechtsboven zweven.
- [x] Voorkom dat de profiel-avatar de Today-content naar beneden duwt.
- [ ] Breng resterende Today-kaarten verder terug naar gedeelde design-system primitives.
- [ ] Splits `HomeDashboardSection.kt` verder op zodat legacy dashboard-code niet in een groot bestand blijft hangen.

## Fase 9: Nieuwe Schermen Later

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

- [x] `codex/design-system-tokens`: theme, tokens, primitives en docs.
- [x] `codex/home-ui-refactor`: dashboard/home naar nieuwe stijl.
- [x] `codex/onboarding-ui-refactor`: onboarding flow naar nieuwe stijl.
- [x] `codex/profile-plan-ui-refactor`: profile en plan schermen.
- [x] `codex/navigation-ui-refactor`: mobiele, tablet en desktop/web/app navigatie naar de designreferentie.
- [ ] Per later scherm: aparte branch en PR bovenop het design system.
