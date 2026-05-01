# Requirements

## Web app
- De web-app moet lokaal bereikbaar zijn op: [http://localhost:8080](http://localhost:8080)
- Startcommando: `./gradlew :apps:web:wasmJsBrowserDevelopmentRun`

## Health data integraties
- Android gebruikt Health Connect via module `integration/health-connect` (Android-only).
- iOS gebruikt Apple HealthKit via module `integration/healthkit` (iOS-only).
- Beide platformen mappen data naar het canonieke model in `core/health`.
- Module-communicatie is event-based via gedeelde event-contracten in `core/health` voor realtime sync zonder DB polling.

## Optionele body capture feature
- De app mag een optionele `Body Capture` feature ondersteunen voor `2D` en `3D` lichaamsscans.
- `2D body capture` gebruikt standaard camera's op telefoon of tablet voor begeleide front-, zij- en achteraanzichten.
- `3D body capture` gebruikt ondersteunde dieptecamera's of body-tracker sensoren wanneer geschikte hardware beschikbaar is.
- De feature is optioneel en mag geen verplichte stap in onboarding of dagelijkse flow zijn.
- Shared feature- en UI-lagen mogen alleen een canoniek gedeeld body-capture model gebruiken, nooit ruwe platform- of vendor-SDK types.
- Beschikbaarheid moet per platform of apparaat zichtbaar zijn, zodat gebruikers weten of alleen `2D` of ook `3D` capture mogelijk is.
- Lifecycle-statussen moeten expliciet modelleerbaar zijn, bijvoorbeeld `started`, `completed`, `failed` en `supported_hardware_required`.
- Referentierichtingen voor product- en integratie-onderzoek:
  - [OpenCap](https://www.opencap.ai/get-started) voor guided markerless capture met commodity camera's
  - [Moverse](https://moverse.ai/) voor geavanceerdere markerless motion capture en 3D tracking
  - [LightBuzz](https://lightbuzz.com/) voor commerciële cross-platform body-tracking SDK's met ondersteuning voor webcam-, LiDAR- en depth-camera workflows
  - [MobilePoser](https://github.com/SPICExLAB/MobilePoser) als open-source referentie voor real-time full-body pose estimation en 3D translatie uit IMU-sensoren op mobiele consumentendevices

### Praktische haalbaarheid met alleen telefoon of webcam
- Met alleen een telefoon of webcam is `2D body capture` realistisch en goed uitvoerbaar.
- Met alleen een telefoon of webcam is ruwe `3D pose` of `3D body mesh estimation` mogelijk, maar dit is niet gelijk aan een nauwkeurige volledige `3D body scan`.
- Een echte hoogwaardige `3D body scan` is met slechts een enkele standaardcamera beperkt door occlusie, beweging, kleding en lichtomstandigheden.
- Twee of meer camera's geven merkbaar betere resultaten voor markerless `3D` reconstructie en beweginganalyse.
- Een `phone-only` of `webcam-only` MVP moet daarom primair worden gepositioneerd als:
  - guided `2D` capture
  - optionele geschatte `3D` pose of body mesh
  - niet als medische of high-precision lichaamsscan
- IMU-gebaseerde alternatieven zoals [MobilePoser](https://github.com/SPICExLAB/MobilePoser) zijn interessant voor realtime houding en bewegingsanalyse, maar vallen functioneel eerder onder `pose tracking` dan onder een visuele `3D body scan`.

### Evaluatie-shortlist: 3D body scanning
- [3DLOOK](https://3dlook.ai/fitxpress/)
  - Pluspunten: smartphone-first, `2-photo` flow, API/SDK-positionering, sterke fit voor consumentgerichte UX.
  - Minpunten: komt historisch uit sizing/retail; waarschijnlijk minder geschikt voor biomechanische precisie.
- [Size Stream](https://www.sizestream.com/mobile-scanning/)
  - Pluspunten: sterke enterprise-positionering, veel afgeleide metingen, smartphone/tablet scanning, relevant voor wellness/research.
  - Minpunten: vermoedelijk zwaardere sales- en integratiecyclus; minder gericht op realtime coaching.
- [Astrivis](https://astrivis.com/3d-body-scanner)
  - Pluspunten: technisch interessant, mobiele 3D scanner-positionering, realtime preview, iOS/Android ondersteuning.
  - Minpunten: publiek materiaal laat minder duidelijk zien hoe volwassen de developer-integratie is voor een brede health app.
- [Avatar Body](https://www.avatar3d.tech/3d-body-scanner-app-avatar-body/)
  - Pluspunten: eenvoudige capture-story met `2` smartphonefoto's, `100+` metingen, API-integratie.
  - Minpunten: voelt meer als service/API dan als breed developer-platform.
- [Bodygee](https://www.bodygee.com/)
  - Pluspunten: sterk op body progress, wellness en before/after-visualisatie.
  - Minpunten: lijkt meer gekoppeld aan eigen scanning-workflows dan aan een generieke drop-in SDK.

### Evaluatie-shortlist: pose tracking en form advice
- [LightBuzz](https://lightbuzz.com/)
  - Pluspunten: sterke fit voor realtime houding/form guidance, webcam/depth/LiDAR support, commerciële SDK-focus.
  - Minpunten: commercieel; mogelijk zwaarder dan nodig voor een simpele phone-only MVP.
- [OpenCap](https://www.opencap.ai/get-started)
  - Pluspunten: sterk referentieproject voor markerless biomechanica met commodity camera's.
  - Minpunten: geen lichte mobiele drop-in SDK; zwaardere multi-camera en processing workflow.
- [Moverse](https://moverse.ai/)
  - Pluspunten: geavanceerde markerless motion capture en 3D tracking.
  - Minpunten: meer high-end mocap dan directe mobiele coaching-flow.
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - Pluspunten: open-source, interessant voor IMU-gebaseerde realtime full-body pose.
  - Minpunten: niet bedoeld voor visuele body scanning; de GitHub-repo gebruikt `CC BY-NC-SA 4.0`, dus commerciële inzet vraagt extra licensing review.
- [IMUPoser](https://spice-lab.org/projects/IMUPoser/)
  - Pluspunten: sterk onderzoeksreferentiepunt voor full-body pose estimation met telefoon, watch en earbuds-IMU's.
  - Minpunten: onderzoeksproject, geen productklare mobiele SDK.

### Extra AI-modelrichtingen voor estimation
- Door de huidige AI-focus verschijnen er meer open-source pose- en body-estimation toolboxes die bruikbaar zijn als bouwstenen, ook als ze geen complete app-SDK zijn.
- Relevante opties:
  - [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker): sterk voor phone-only realtime pose tracking op device.
  - [MMPose](https://github.com/open-mmlab/mmpose): brede open-source toolbox voor 2D, 3D en whole-body pose estimation, inclusief nieuwere realtime modellen zoals `RTMW` / `RTMW3D`.
  - [MMHuman3D](https://github.com/open-mmlab/mmhuman3d): toolbox voor 3D human parametric models en human mesh recovery.
  - [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose): bekende klassieke open-source baseline voor body/face/hand keypoints.
  - [FreeMoCap](https://github.com/freemocap/freemocap): open-source markerless motion capture platform, vooral interessant voor multi-camera en research workflows.
- Deze model/toolbox-laag is vooral relevant als de app een eigen body-capture of posture-analysis stack wil bouwen in plaats van een commerciële integratie af te nemen.

### Overzicht: AI-modellen voor body tracking en 3D scanning
- Dit overzicht bundelt varianten van `IMUPoser` en aanvullende modellen voor `3D` lichaamsreconstructie en sportondersteuning.

#### 1. Sensor-based modellen (`IMU` / wearable)
- Focus: beweging volgen zonder camera's, bruikbaar voor sport in vrije ruimte.
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - Kenmerken: directe opvolger van `IMUPoser`; gebruikt smartphone + watch.
  - Status: open source
- [DIP (Deep Inertial Poser)](https://github.com/eth-ait/dip18)
  - Kenmerken: basislijn voor sparse full-body tracking met een beperkt aantal sensoren.
  - Status: open source
- [TransPose](https://github.com/Xinyu-Yi/TransPose)
  - Kenmerken: hoge accuraatheid en globale positiebepaling uit sparse IMU-data.
  - Status: open source
- [PIP (Physical Inertial Poser)](https://github.com/Xinyu-Yi/PIP)
  - Kenmerken: voegt physics toe om zweven en instabiele bewegingen te beperken.
  - Status: open source
- [Xsens MVN](https://www.movella.com/products/motion-capture/xsens-mvn)
  - Kenmerken: professionele mocap-standaard met eigen hardware-ecosysteem.
  - Status: commercieel
- [BaroPoser](https://arxiv.org/abs/2401.04283)
  - Kenmerken: combineert `IMU` met luchtdrukdata voor betere hoogtemeting zoals trappen en sprongen.
  - Status: research

#### 2. Camera-based modellen (`vision`)
- Focus: nauwkeurige gewrichtshoeken en posture-analyse via video.
- [MediaPipe BlazePose / Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
  - Kenmerken: realtime tracking op mobiel, sterke basis voor phone-only posture tracking.
  - Status: free / open ecosystem
- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose)
  - Kenmerken: multi-person tracking en robuuste klassieke keypoint-detectie.
  - Status: free met gebruiksbeperkingen, license review nodig voor commercieel gebruik
- [HybrIK](https://github.com/jeffffffli/HybrIK)
  - Kenmerken: `3D` pose uit `2D` beelden met anatomisch consistente reconstructie.
  - Status: open source
- [AlphaPose](https://github.com/MVIG-SJTU/AlphaPose)
  - Kenmerken: hoge nauwkeurigheid in complexe sport- en multi-person omgevingen.
  - Status: open source

#### 3. 3D body scanning en reconstruction (`mesh`)
- Focus: het maken van een digitale kopie of `3D` reconstructie van het lichaam.
- [PIFuHD](https://github.com/facebookresearch/pifuhd)
  - Kenmerken: gedetailleerde `3D` mesh uit een enkele foto, inclusief kledingcontouren.
  - Status: open source
- [ECON](https://github.com/YuliangXiu/ECON)
  - Kenmerken: moderne volledige lichaamsreconstructie met kleding en expliciete mesh-output.
  - Status: open source
- [ROMP](https://github.com/Arthur151/ROMP)
  - Kenmerken: schat lichaamsvorm en pose (`SMPL`) ook bij gedeeltelijke occlusie.
  - Status: open source
- [Luma AI](https://lumalabs.ai/)
  - Kenmerken: `NeRF` / Gaussian Splatting-achtige workflows voor `3D` scans uit video.
  - Status: free / commercieel

#### Samenvatting per categorie
- Op basis van techniek:
  - sensor-based: `MobilePoser`, `DIP`, `TransPose`, `PIP`, `Xsens`
  - camera-based: `MediaPipe`, `OpenPose`, `HybrIK`, `AlphaPose`
  - mesh / reconstruction: `PIFuHD`, `ECON`, `ROMP`, `Luma AI`
- Op basis van licentie:
  - open source / free: `MobilePoser`, `MediaPipe`, `DIP`, `PIFuHD`, `ECON`, `AlphaPose`
  - commercieel: `Xsens`, `Luma AI` premium workflows
  - free-to-use met beperkingen: `OpenPose`; commerciële inzet altijd apart valideren

#### Toepassing voor dit project
- `3D` scan maken:
  - gebruik `ECON` of `PIFuHD` als onderzoekslijn voor geometrie en mesh-reconstructie
- sport tracking:
  - gebruik `MobilePoser` of `IMUPoser`-achtige richtingen als sensoren gewenst zijn
  - gebruik `MediaPipe` als camera op statief of phone-only capture het uitgangspunt is

### Aanraders en selectiehulp
#### Beste opties voor een MVP
- `Phone-only posture/form tracking`:
  - [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
  - [QuickPose](https://quickpose.ai/)
- Waarom:
  - snelle integratie
  - geen extra hardware nodig
  - sterk voor realtime feedback, range-of-motion en rep counting
- Let op:
  - `QuickPose` positioneert zich als een commerciële exercise- en posture-SDK, terwijl `MediaPipe` meer een technische basislaag is
- `Phone-first body scanning / measurements`:
  - [3DLOOK](https://3dlook.ai/fitxpress/)
  - [Size Stream](https://www.sizestream.com/mobile-scanning/)
- Waarom:
  - duidelijke productfit voor smartphone capture
  - sneller naar een bruikbaar eindproduct dan een eigen research stack

#### Beste opties voor premium / commercial
- `Realtime movement assessment / coaching`:
  - [LightBuzz](https://lightbuzz.com/)
  - [Kinotek](https://kinotek.com/)
- Waarom:
  - sterk op coaching, movement assessment en commerciële workflows
  - geschikt wanneer productkwaliteit en support belangrijker zijn dan maximale technische vrijheid
- `3D scanning / measurements`:
  - [Size Stream](https://www.sizestream.com/mobile-scanning/)
  - [Bodidata](https://www.bodidata.com/)
  - [Bodygee](https://www.bodygee.com/)
- Waarom:
  - duidelijk meet- en scanningproduct
  - volwassen commerciële positionering
- Let op:
  - `Bodygee` lijkt deels gekoppeld aan eigen scan-hardware en partnerlocaties
  - `Bodidata` lijkt sterk retail/fit georiënteerd en minder health- of posture-first

#### Beste opties voor research / experimentation
- `Mesh and human reconstruction`:
  - [ECON](https://github.com/YuliangXiu/ECON)
  - [PIFuHD](https://github.com/facebookresearch/pifuhd)
  - [ROMP](https://github.com/Arthur151/ROMP)
  - [4DHumans / HMR2](https://github.com/shubham-goel/4D-Humans)
  - [WHAM](https://github.com/yohanshin/WHAM)
- `Sensor-based pose estimation`:
  - [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - [IMUPoser](https://spice-lab.org/projects/IMUPoser/)
  - [DIP](https://github.com/eth-ait/dip18)
  - [TransPose](https://github.com/Xinyu-Yi/TransPose)
  - [PIP](https://github.com/Xinyu-Yi/PIP)
- Waarom:
  - interessant als inspiratie of om een eigen stack te bouwen
  - sterk voor proof-of-concepts en evaluaties
- Let op:
  - meestal geen mobiele drop-in SDK
  - vaak aanzienlijke ML- en platform-engineering nodig

#### Niet geschikt of oppassen met licenties
- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose)
  - krachtig, maar commerciële licentievoorwaarden moeten apart worden gevalideerd
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - de repo gebruikt `CC BY-NC-SA 4.0`; niet zomaar geschikt voor commercieel productgebruik
- `SMPL`-afhankelijke stacks zoals `ROMP`, `MMHuman3D`, `4DHumans`
  - technisch waardevol, maar body-model licensing moet expliciet worden beoordeeld
- Research-projecten zoals `ECON`, `WHAM`, `FreeMoCap`
  - vaak prima voor evaluatie, maar niet direct production-ready qua mobiele UX, support of onderhoud

## Sport activities en training guidance
- De app moet sportactiviteiten en workouts ondersteunen als een samenhangende trainingservaring.
- De primaire productvragen zijn:
  - wat heb ik vandaag gedaan
  - wat moet ik vandaag trainen
  - ben ik in balans en herstel ik goed
- De ervaring moet daarom drie duidelijke productstromen combineren:
  - `Track`: activiteiten en workouts registreren
  - `Plan`: suggesties en trainingsadvies tonen
  - `Progress`: trends, balans en herstel inzichtelijk maken

### Scope van activity tracking
- Eerste focus:
  - `running`
  - `walking`
  - `cycling`
  - `swimming`
  - `manual workout entry`
- Outdoor activiteiten moeten waar mogelijk `GPS`-gebaseerd zijn.
- Activiteiten moeten minimaal kunnen vastleggen:
  - activity type
  - start- en eindtijd
  - duur
  - afstand
  - pace of snelheid
  - calorieën
  - gemiddelde en maximale hartslag
  - route voor outdoor activiteiten
  - laps voor zwemmen
  - elevation gain voor running en cycling
  - bron: manual, Health Connect, HealthKit, device of imported file

### Scope van strength en fitness tracking
- Fitness en krachttraining zijn een aparte productstroom naast GPS-activiteiten.
- Workouts moeten minimaal kunnen vastleggen:
  - exercise name
  - category: push, pull, legs, core, mobility, cardio
  - primary muscles
  - secondary muscles
  - sets
  - reps
  - weight
  - duration
  - rest time
  - notes
  - perceived effort

### Trainingsadvies
- Trainingsadvies moet in eerste instantie regelgebaseerd en transparant zijn.
- Eerste adviesvormen:
  - `do this today`
  - `recover today`
  - `you are undertraining this muscle group`
  - `avoid overloading this area`
  - `add mobility or core`
- Advies moet rekening houden met:
  - recente activiteitenbelasting
  - recent getrainde spiergroepen
  - trainingsdoel
  - hersteltijd tussen vergelijkbare sessies

### Social sharing en sociale motivatie
- De app mag een optionele social-sharing laag bevatten zoals moderne sportapps dat doen.
- Social sharing moet ondersteunend zijn aan motivatie en progressie, niet de hoofdstructuur van het product vervangen.
- Eerste social use-cases:
  - deel een run, ride, swim of workout summary
  - deel afstand, tijd, pace, route-preview of training badge
  - deel progress milestones zoals streaks, PR's, weekly goal completion of consistency
  - deel gym-sessies inclusief oefeningen of spiergroepfocus
  - deel recovery- of recommendation cards wanneer relevant
- Privacy moet expliciet instelbaar zijn per item:
  - private
  - shareable by user action
  - visible to followers or friends in a latere fase
- Social UX moet beginnen met `share out`, niet met een volledige social feed.
- Mogelijke latere uitbreidingen:
  - likes of reactions
  - comments
  - friend activity feed
  - challenges
  - clubs of group goals
- Deelbare visuals moeten herkenbaar en clean zijn:
  - activity summary card
  - map snapshot voor outdoor sessions
  - workout completion card
  - weekly recap card
  - muscle balance or progress card
- Social sharing moet gekoppeld kunnen worden aan:
  - achievements
  - weekly consistency
  - personal records
  - recommended next-step prompts

### Voorstel voor customer journey
#### Onboarding
- Houd onboarding licht en doelgericht.
- Vraag alleen:
  - trainingsdoel
  - favoriete activiteiten
  - beschikbare apparatuur
  - optionele koppeling met Health Connect of HealthKit
- Resultaat van onboarding:
  - een gepersonaliseerde `Today`-ervaring met eerste aanbevolen actie

#### Dagelijkse terugkerende flow
- De gebruiker moet binnen enkele seconden antwoord krijgen op:
  - wat is mijn status vandaag
  - wat raad de app aan
  - hoe start ik snel een sessie
- De `Today`-ervaring moet daarom tonen:
  - daily status of readiness
  - suggested session
  - quick actions
  - recent sessions
  - muscle balance of recovery snapshot
  - deelbare achievement of progress moment wanneer van toepassing

#### Tracking-flow
- Outdoor activiteiten:
  - startscherm met typekeuze en sensorstatus
  - live tracking met duur, afstand, pace, hartslag en route-status
  - finish-scherm met samenvatting, map, splits, impact op trainingsadvies en `share` actie
- Gym en fitness:
  - keuze tussen quick workout, guided template en free logging
  - workoutflow met oefeningen, sets, reps, gewicht en rusttimer
  - eindscherm met getrainde spieren, samenvatting, aanbeveling voor volgende sessie en `share` actie
- Zwemmen:
  - pool of open water selectie
  - pool length of handmatige invoer
  - lap- en afstandssamenvatting waar beschikbaar, plus optionele `share` actie

### Voorstel voor schone UI-structuur
- Gebruik vier primaire navigatiegebieden:
  - `Today`
  - `Track`
  - `Plan`
  - `Progress`
- `Today`:
  - dagelijkse status
  - aanbevolen sessie
  - quick actions zoals `Start run`, `Start ride`, `Log workout`
  - recente sessies
- `Track`:
  - tab of segmented control voor `Outdoor`, `Pool`, `Gym`, `Manual`
- `Plan`:
  - aanbevolen focus
  - ondergetrainde spiergroepen
  - suggesties op basis van beschikbare tijd en herstel
- `Progress`:
  - weekly volume
  - distance, pace en consistency trends
  - muscle heatmap of balance view
  - deelbare recap cards

### Adaptieve UX-richtlijnen
- Compact:
  - enkelkoloms flow met sterke primaire CTA's
- Medium:
  - `Today` naast `Plan` of list-detail weergave
- Expanded:
  - persistente navigatie met aparte pane voor details en progress
- Live tracking-schermen moeten minimale cognitieve belasting hebben:
  - grote cijfers
  - weinig tekst
  - één primaire actie per toestand

### Voorstel voor canoniek domeinmodel
- De kern moet twee parallelle logboeken ondersteunen:
  - `EnduranceActivity`
  - `StrengthSession`
- Gedeelde taxonomie:
  - `ActivityType`
  - `ExerciseType`
  - `MuscleGroup`
  - `TrainingGoal`
  - `RecoveryStatus`
  - `TrainingRecommendation`
- Social sharing modelconcepten:
  - `ShareableWorkoutSummary`
  - `ShareableActivitySummary`
  - `Achievement`
  - `PrivacySetting`
  - `ShareChannel`

### MVP-volgorde
1. Canoniek workout- en activitymodel definiëren.
2. Manual logging voor activiteiten en workouts toevoegen.
3. Health Connect en HealthKit mapping voor bestaande workouts en activity data toevoegen.
4. GPS-recording voor outdoor sessies toevoegen.
5. Muscle group tagging en basisadvies toevoegen.
6. Progress- en balance-views toevoegen.
7. Guided workouts en geavanceerdere recommendations toevoegen.

## Other finds
  - https://github.com/MasonSakai/Iris-Full-Body-Tracking
  - https://slimevr.dev/

## Apps that track 
  - https://www.hevyapp.com/features/
## Coding tips
  - https://www.youtube.com/watch?v=Ae3SPjsXETc
  - https://learnopencv.com/ai-fitness-trainer-using-mediapipe/
## News & Social 
  - https://www.hardlopen.nl/evenementen/ evenement kalender
    - deze integreren met nieuws & social 
  - social part uitbreiden me wie sport er mee
    - profiel kan je jouw sport vestiging opgeven, jouw sport voorkeur 
    - wie mag je volgen
