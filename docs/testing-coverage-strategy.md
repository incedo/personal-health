# Testing Coverage Strategy

Deze repository hanteert een **laag-specifieke coverage aanpak** in plaats van 100% op alle modules.

## Doelverhoudingen

- `core/*`: 95-100% (kritieke domeinlogica, mappers, event verwerking)
- `feature/*`: 80-90% (feature state, use-cases, reducer logica)
- `apps/*` + platform bootstrap: lager toegestaan, focus op smoke + snapshot + integratie
- Totaal project (line coverage): 70%+

## Waarom niet overal 100%

- Compose UI en platform bootstrap (`Activity`, `main`, view wiring) zijn beperkt zinvol op 100% unit coverage.
- Snapshot tests (Paparazzi/Shot) bewaken visuele regressies goed, maar vervangen geen logica-tests.
- Hoge dekking moet vooral op business rules en event flows zitten.

## Gefaseerde uitvoering

1. `Fase 1` (nu): stabiele gate met projectbrede ondergrens (huidig ingesteld in Gradle/Kover).
2. `Fase 2`: per-laag verhoging naar minimaal:
   - `core/*` >= 70%
   - `feature/*` >= 50%
   - totaal >= 50%
3. `Fase 3` (doel): bereiken van de doelverhoudingen bovenaan dit document.

## Meetcommando's

- XML + verify:
  - `./gradlew koverXmlReport koverVerify --no-daemon`
- HTML report:
  - `./gradlew koverHtmlReport --no-daemon`
- Samenvatting met doelratio's:
  - `./scripts/coverage-summary.sh`
