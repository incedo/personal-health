# Navigation Principles

Dit document is de centrale bron voor navigatieprincipes en informatiearchitectuur in `personal-health` voor mobiel, tablet, laptop/desktop en tv.

Andere documenten mogen hiernaar verwijzen, maar horen de navigatierichting niet inhoudelijk te dupliceren.

## 1. Probleem

De huidige navigatie is te scherm-specifiek en te plat:
- `Home`, `Sync` en `Profile` voelen als technische tabs, niet als productstromen.
- Op grotere schermen verdwijnt de primaire navigatie grotendeels in losse layout-keuzes.
- Er is nog geen duidelijke scheiding tussen:
  - top-level productnavigatie
  - lokale subnavigatie binnen een feature
  - tijdelijke acties zoals import, filters en profieltaken

Het gevolg is dat de informatiearchitectuur onduidelijk wordt zodra dezelfde app moet werken op telefoon, tablet, desktop en tv.

## 2. Navigatieprincipes

1. Gebruik taakgedreven top-level navigatie, geen implementatiegedreven tabs.
- Navigatie moet aansluiten op gebruikersvragen:
  - wat is vandaag belangrijk
  - wat wil ik registreren
  - wat moet ik trainen
  - hoe ontwikkel ik me

2. Gebruik overal dezelfde productsemantiek.
- Een bestemming blijft functioneel hetzelfde op elk device.
- Alleen presentatie verandert per size class.

3. Beperk top-level bestemmingen tot maximaal 5.
- Meer dan 5 hoofditems maakt mobiel en tv onrustig.
- Extra routes horen in overflow, profiel of contextuele acties.

4. Maak detailnavigatie contextueel.
- Binnen een feature gebruik je tabs, segmented controls of een secundaire lijst.
- Gebruik geen tweede globale navigatiebalk in hetzelfde scherm.

5. Grote schermen krijgen persistente navigatie.
- Tablet, desktop en tv moeten niet terugvallen op verborgen menu's voor primaire routes.

6. Navigatie is input-onafhankelijk.
- Alles moet bruikbaar zijn met touch, muis, keyboard en tv remote.
- Focusvolgorde en zichtbare selected/focus states zijn verplicht.

## 3. Voorgestelde top-level informatiearchitectuur

Aanbevolen hoofdstructuur:

- `Today`
  - dagoverzicht, belangrijke signalen, quick actions, sync status
- `Track`
  - activiteit starten, workout loggen, handmatige invoer, body capture indien beschikbaar
- `Plan`
  - trainingsadvies, hersteladvies, aanbevolen sessie van vandaag
- `Progress`
  - trends, historie, doelen, balans, voortgang per metric
- `Profile`
  - account, apparaten, integraties, instellingen, permissies

Niet als top-level bestemming:
- `Sync`
  - dit is een cross-cutting status of utility, geen productgebied
- losse technische integratiepagina's
- tijdelijke import- of debugschermen

## 4. Menu- en navigatiemodel per deviceklasse

### Compact: telefoon
- Gebruik een `bottom navigation bar` met 4 of 5 items.
- Aanbevolen items:
  - `Today`
  - `Track`
  - `Plan`
  - `Progress`
  - `Profile`
- Gebruik geen hamburger als primaire navigatie als bottom nav past.
- Gebruik een prominente contextuele actie op `Track` voor snelle logging.

### Medium: tablet portrait / kleine landscape
- Gebruik een `NavigationRail`.
- Combineer dit waar relevant met een `list-detail` layout:
  - links bestemming of lijst
  - rechts detail of inhoud
- Behoud dezelfde hoofditems als op mobiel.

### Expanded: laptop, desktop, grote tablet
- Gebruik een persistente linkerzijbalk.
- Structuur:
  - bovenaan app-identiteit en eventueel globale status
  - midden top-level bestemmingen
  - onderaan profiel, instellingen en secundaire acties
- Gebruik 2- of 3-pane layouts waar relevant:
  - `Today`: hoofdinhoud + contextpaneel
  - `Progress`: filter/lijst + detail + vergelijking
  - `Track`: activiteitstypes + editor/detail

### TV
- Gebruik een persistente linker rail of brede sidebar met grote focusbare items.
- Vermijd bottom navigation op tv.
- Eerste focus moet altijd op de primaire navigatie of het hoofddoel van het scherm landen.
- Gebruik grotere targets, minder dichte content en horizontale carousels alleen als de focuslogica strak is.
- Verstop instellingen en zelden gebruikte acties achter een secundair profiel/meer-menu, niet tussen hoofditems.

## 5. Subnavigatie binnen features

Top-level navigatie bepaalt waar je bent. Subnavigatie bepaalt wat je daar ziet.

Aanbevolen patronen:

- `Today`
  - geen extra globale tabs
  - werk met secties zoals `samenvatting`, `alerts`, `quick actions`, `recent`
- `Track`
  - segmented control of tabs: `Activities`, `Workouts`, `Body Capture`
  - op grote schermen kan links een secundaire lijst of category rail staan
- `Plan`
  - tabs of filterchips: `Today`, `Week`, `Recovery`
- `Progress`
  - tabs of secondary nav: `Overview`, `Activity`, `Strength`, `Recovery`, `Body`
- `Profile`
  - grouped menu sections: `Account`, `Devices`, `Permissions`, `Settings`

Regel:
- maximaal één primaire navigatielaag zichtbaar
- maximaal één secundaire navigatielaag per scherm

## 6. Plaats van globale acties

Niet alles hoort in het menu.

Gebruik vaste plaatsing:

- `Search`
  - alleen als globale feature echt nodig wordt; dan in top app bar of desktop header
- `Notifications` of `Inbox`
  - status- of actiecentrum, niet als top-level tab in MVP
- `Sync status`
  - compacte statuschip of card in `Today` en eventueel header
- `Add` of `Start`
  - contextuele actie binnen `Track`, niet als los hoofdscherm

## 7. State- en routeprincipes

Voor shared Compose navigatie:

1. Houd een klein, stabiel setje app-routes in `shared/app`.
2. Laat features hun eigen interne subroutes beheren.
3. Maak routes semantisch:
- goed: `today`, `track`, `plan`, `progress`, `profile`
- slecht: `tab1`, `sync`, `dashboard2`
4. Bewaar geselecteerde top-level route over resize en posture changes.
5. Bewaar feature-state onafhankelijk van presentatie:
- als `Track` op mobiel single-pane is en op desktop two-pane, mag de onderliggende state niet resetten

## 8. Toegankelijkheid en inputregels

Verplicht voor alle navigatiecomponenten:

- duidelijke selected state
- duidelijke focus state
- hover feedback voor pointer devices
- keyboard support voor tab, pijltjes en enter/space waar passend
- tv remote / d-pad focusvolgorde zonder dead ends
- targets die ook op tablet en tv comfortabel selecteerbaar zijn

## 8a. Visual UI direction

Gebruik een consistente visuele taal over onboarding, settings, premium en configuratieschermen:

- werk met een donkere, rustige basis en een beperkt aantal heldere accentkleuren
- geef elk scherm een duidelijke hero header met icoon, titel en korte uitleg
- groepeer inhoud in grote cards of secties in plaats van losse componenten
- gebruik sterke selected states:
  - actieve keuze krijgt outline of ingevulde accentkleur
  - inactieve keuzes blijven rustig en leesbaar
- gebruik grote klik- en focusvlakken zodat dezelfde UI goed werkt op mobiel, tablet, desktop en tv
- gebruik segmented choices en chips alleen als feature-local subnavigatie of keuzecomponent, niet als globale app-navigatie
- gebruik settings als gegroepeerde lijstschermen met duidelijke sectiekoppen
- gebruik vaste actiezones voor flows:
  - onboarding: `back`, `skip`, `next`
  - settings/detail: primary action onderaan of rechts

Deze richting is vooral geschikt voor:
- onboarding
- profile/settings
- plan- en configuratieschermen
- premium/paywall schermen

Deze richting is minder geschikt als directe blauwdruk voor de primaire app-shell. De globale navigatiestructuur blijft taakgedreven en volgt de secties hierboven.

## 9. Concreet voorstel voor deze codebase

Aanbevolen evolutie van de app-shell:

1. Verplaats app-level navigatie naar `shared/app`.
2. Introduceer een gedeeld model, bijvoorbeeld `AppDestination`.
3. Laat de shell een adaptief navigatiechrome kiezen:
- compact: bottom bar
- medium: navigation rail
- expanded: sidebar
- tv: focus-first sidebar
4. Houd feature-inhoud los van het navigatiechrome.
5. Verplaats huidige `Sync` inhoud naar:
- `Today` als statuskaart
- `Profile > Integrations` voor beheer en instellingen

Voor productrichting in deze repository is deze indeling het meest coherent:

- `Today` sluit aan op de huidige home/dashboard behoefte
- `Track` sluit aan op activity tracking, workouts en body capture
- `Plan` sluit aan op trainingsadvies
- `Progress` sluit aan op trends en historie
- `Profile` vangt instellingen, integraties en permissies af

## 10. Wat we expliciet niet moeten doen

- geen aparte navigatiestructuur per platform
- geen technische tabs zoals `sync` als hoofdroute
- geen hamburger menu als standaard op alle devices
- geen bottom bar op tv
- geen feature-specifieke top-level items die later niet schaalbaar zijn
- geen route-reset bij resize, fold of orientation change

## 11. Gefaseerde invoering

### Fase 1
- Definieer app-bestemmingen en naming.
- Vervang huidige `HomeTab` door gedeelde top-level app-routes.

### Fase 2
- Bouw adaptieve app-shell in `shared/app`.
- Bottom bar, rail en sidebar krijgen hetzelfde route-model.

### Fase 3
- Verplaats `Sync` en andere technische schermstukken naar contextuele plekken.
- Splits `Home` inhoud op in `Today` en onderdelen die naar `Track` of `Progress` horen.

### Fase 4
- Voeg previews/tests toe voor:
  - compact
  - medium
  - expanded
  - keyboard/pointer focus
  - tv focus-flow waar van toepassing

## 12. Beslisregels voor toekomstige features

Gebruik deze beslisboom:

1. Is dit een dagelijkse primaire gebruikersvraag?
- Dan kandidaat voor een top-level bestemming.

2. Is dit alleen relevant binnen een bestaand productgebied?
- Dan subnavigatie of lokale section.

3. Is dit zeldzaam, technisch of account-gerelateerd?
- Dan in `Profile` of overflow.

4. Is dit een status of actie die overal kan voorkomen?
- Dan geen menu-item, maar contextuele UI of event/feed.
