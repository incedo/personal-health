# 3D Scanning, Body Capture & Posture Tracking

## Overzicht

Dit document bundelt alle onderzochte opties voor 3D body scanning, body capture, pose tracking en form/posture advies.
Het dient als evaluatiehulp bij het kiezen van de juiste integratie(s) voor de health app.

---

## Praktische haalbaarheid met alleen telefoon of webcam

- Met alleen een telefoon of webcam is `2D body capture` realistisch en goed uitvoerbaar.
- Met alleen een telefoon of webcam is ruwe `3D pose` of `3D body mesh estimation` mogelijk, maar dit is niet gelijk aan een nauwkeurige volledige `3D body scan`.
- Een echte hoogwaardige `3D body scan` is met slechts een enkele standaardcamera beperkt door occlusie, beweging, kleding en lichtomstandigheden.
- Twee of meer camera's geven merkbaar betere resultaten voor markerless `3D` reconstructie en beweginganalyse.
- Een `phone-only` of `webcam-only` MVP moet daarom primair worden gepositioneerd als:
  - guided `2D` capture
  - optionele geschatte `3D` pose of body mesh
  - niet als medische of high-precision lichaamsscan
- IMU-gebaseerde alternatieven zoals [MobilePoser](https://github.com/SPICExLAB/MobilePoser) zijn interessant voor realtime houding en bewegingsanalyse, maar vallen functioneel eerder onder `pose tracking` dan onder een visuele `3D body scan`.

---

## 1. Commerciële 3D body scanning oplossingen

### 1.1 [3DLOOK — FitXpress](https://3dlook.ai/fitxpress/)
- **Type**: smartphone-first, `2-photo` flow
- **Integratie**: API/SDK
- **Kenmerken**: 96–97% accuracy voor body measurements, 80+ datapunten, BMI op basis van predicted weight (89% accuracy), real-time pose validation met voice guidance en automatische capture-optimalisatie
- **Pluspunten**: sterke fit voor consumentgerichte UX, snelle integratie, geen extra hardware nodig
- **Minpunten**: komt historisch uit sizing/retail; waarschijnlijk minder geschikt voor biomechanische precisie
- **Status**: commercieel

### 1.2 [Prism Labs](https://www.prismlabs.tech/)
- **Type**: smartphone video-based (10 sec), 3D body composition
- **Integratie**: iOS SDK, Android SDK, **mobile web SDK** (nieuw)
- **Kenmerken**: body fat %, lean mass, waist-to-hip ratio, 3D body avatar, 360° scanning, body composition metrics
- **Pluspunten**: sterke health/fitness positionering (o.a. GLP-1 tracking bij Noom), web SDK zonder app download, Fit3D SNAP tablet-scanner gebouwd op Prism tech ($200 instappunt), onafhankelijk gevalideerde nauwkeurigheid
- **Minpunten**: relatief nieuw in de markt; web SDK volwassenheid nog te beoordelen
- **Status**: commercieel

### 1.3 [Size Stream — Mobile Scanning](https://www.sizestream.com/mobile-scanning/)
- **Type**: smartphone/tablet scanning
- **Integratie**: API, SDK
- **Kenmerken**: sterke enterprise-positionering, veel afgeleide metingen, relevant voor wellness/research
- **Pluspunten**: volwassen product, brede meetcapaciteiten
- **Minpunten**: vermoedelijk zwaardere sales- en integratiecyclus; minder gericht op realtime coaching
- **Status**: commercieel

### 1.4 [Nettelo](https://nettelo.com/)
- **Type**: AI-gebaseerd, full-body selfie naar 3D body model
- **Integratie**: iOS SDK, Android SDK, API (cloud services), SaaS
- **Kenmerken**: gepatenteerde technologie, consumer en professional use, self-scan thuis met eigen device
- **Pluspunten**: lage drempel voor eindgebruiker, vergelijkbaar met 3DLOOK qua foto-flow maar met eigen tech stack
- **Minpunten**: minder publiek beschikbare validatiedata dan 3DLOOK/Prism Labs
- **Status**: commercieel

### 1.5 [Astrivis](https://astrivis.com/3d-body-scanner)
- **Type**: mobiele 3D scanner
- **Integratie**: iOS/Android ondersteuning
- **Kenmerken**: technisch interessant, realtime preview
- **Pluspunten**: mobiele 3D scanner-positionering
- **Minpunten**: publiek materiaal laat minder duidelijk zien hoe volwassen de developer-integratie is voor een brede health app
- **Status**: commercieel

### 1.6 [Avatar Body](https://www.avatar3d.tech/3d-body-scanner-app-avatar-body/)
- **Type**: `2` smartphonefoto's
- **Integratie**: API
- **Kenmerken**: `100+` metingen
- **Pluspunten**: eenvoudige capture-story
- **Minpunten**: voelt meer als service/API dan als breed developer-platform
- **Status**: commercieel

### 1.7 [Bodygee](https://www.bodygee.com/)
- **Type**: body progress tracking, before/after-visualisatie
- **Integratie**: eigen scanning-workflows
- **Kenmerken**: sterk op wellness en body progress
- **Pluspunten**: goede visuele output voor motivatie en tracking
- **Minpunten**: lijkt meer gekoppeld aan eigen scanning-workflows dan aan een generieke drop-in SDK; deels gekoppeld aan eigen scan-hardware en partnerlocaties
- **Status**: commercieel

### 1.8 [MyFit Solutions](https://myfit-solutions.com/)
- **Type**: phone camera of TrueDepth sensor
- **Integratie**: SDK (My3D Scanner app), webhook/API
- **Kenmerken**: millimeter-nauwkeurige 3D scans, fotorealistische `.stl`/`.obj` output
- **Pluspunten**: hoge precisie, medische toepassingen
- **Minpunten**: meer medisch/prosthetics georiënteerd dan consumer health
- **Status**: commercieel

### 1.9 [3DSizeMe / Qwadra](https://qwadra.com/solution/3d-sizeme/)
- **Type**: Structure Sensor of iPhone TrueDepth camera
- **Integratie**: Structure SDK
- **Kenmerken**: head-to-toe scanning, GDPR-compliant health data handling, 3D modellen voor orthotics/prosthetics
- **Pluspunten**: sterke compliance en medische focus
- **Minpunten**: hardware-afhankelijk (Structure Sensor), niche orthotics/prosthetics focus
- **Status**: commercieel

### 1.10 [Bodidata — Kora](https://www.bodidata.com/)
- **Type**: iPad Pro + eigen Kora hardware attachment
- **Integratie**: eigen platform
- **Kenmerken**: LiDAR + millimeter wave radar fusion, zeer hoge nauwkeurigheid
- **Pluspunten**: indrukwekkende meetprecisie
- **Minpunten**: hardware-afhankelijk (niet phone-only), sterk retail/fit georiënteerd, minder health- of posture-first
- **Status**: commercieel

---

## 2. Commerciële pose tracking & form advice oplossingen

### 2.1 [QuickPose](https://quickpose.ai/)
- **Type**: exercise- en posture-SDK
- **Integratie**: iOS SDK, **Android SDK** (sinds mei 2025)
- **Kenmerken**: tot 120fps op iOS, 1M+ workouts verwerkt, rep counting, range-of-motion, form feedback, on-device processing
- **Pluspunten**: zeer productierijp, snelle integratie, geen server-side processing nodig, sterk voor realtime feedback
- **Minpunten**: commercieel; richt zich specifiek op exercise/fitness, mogelijk minder flexibel voor custom pose-analyse
- **Status**: commercieel

### 2.2 [LightBuzz](https://lightbuzz.com/)
- **Type**: realtime body-tracking SDK
- **Integratie**: cross-platform SDK (webcam, depth camera, LiDAR)
- **Kenmerken**: houding/form guidance, movement assessment
- **Pluspunten**: sterke fit voor realtime coaching, brede hardware-ondersteuning
- **Minpunten**: commercieel; mogelijk zwaarder dan nodig voor een simpele phone-only MVP
- **Status**: commercieel

### 2.3 [Kinotek](https://kinotek.com/)
- **Type**: movement assessment platform
- **Integratie**: eigen platform, LiDAR camera
- **Kenmerken**: 65 movement assessments, AI gait analysis
- **Pluspunten**: sterk op coaching, movement assessment en commerciële workflows
- **Minpunten**: meer een turnkey systeem voor gyms/clinics dan een drop-in SDK; directe contactname nodig voor integratieopties
- **Status**: commercieel

### 2.4 [PoseTracker](https://www.posetracker.com/)
- **Type**: pose tracking API, geen SDK nodig
- **Integratie**: WebView/iframe, werkt op iOS/Android/web
- **Kenmerken**: gebouwd op MoveNet, 30+ fps, ingebouwde exercise-analyse (squats, push-ups, etc.)
- **Pluspunten**: extreem lage integratiedrempel, gratis tier beschikbaar, $20/maand developer plan, cross-platform zonder native SDK
- **Minpunten**: afhankelijkheid van WebView-performance; minder controle dan een native SDK
- **Status**: commercieel (freemium)

---

## 3. Open-source & research: camera-based modellen (vision)

### 3.1 [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
- **Type**: realtime on-device pose tracking
- **Kenmerken**: 33 landmarks, 2D en 3D pose estimation, sterke basis voor phone-only posture tracking
- **Pluspunten**: gratis, breed ondersteund, sterk op mobiel
- **Status**: free / open ecosystem

### 3.2 [MMPose](https://github.com/open-mmlab/mmpose)
- **Type**: brede open-source toolbox
- **Kenmerken**: 2D, 3D en whole-body pose estimation, inclusief realtime modellen zoals `RTMW` / `RTMW3D`
- **Status**: open source

### 3.3 [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose)
- **Type**: klassieke open-source baseline
- **Kenmerken**: body/face/hand keypoints, multi-person tracking
- **Pluspunten**: robuust en bewezen
- **Minpunten**: commerciële licentievoorwaarden moeten apart worden gevalideerd
- **Status**: free met gebruiksbeperkingen

### 3.4 [HybrIK](https://github.com/jeffffffli/HybrIK)
- **Type**: 3D pose uit 2D beelden
- **Kenmerken**: anatomisch consistente reconstructie
- **Status**: open source

### 3.5 [AlphaPose](https://github.com/MVIG-SJTU/AlphaPose)
- **Type**: hoge-nauwkeurigheid pose estimation
- **Kenmerken**: sterk in complexe sport- en multi-person omgevingen
- **Status**: open source

### 3.6 [YOLO11 Pose](https://docs.ultralytics.com/)
- **Type**: pose estimation module in YOLO11
- **Kenmerken**: single en multi-person detectie, 22% minder parameters dan YOLOv8m bij hogere accuracy, snelle inferentie
- **Pluspunten**: moderne architectuur, goede balans tussen snelheid en nauwkeurigheid
- **Minpunten**: minder landmarks dan MediaPipe (17 vs 33)
- **Status**: open source

### 3.7 [OpenCap](https://www.opencap.ai/get-started)
- **Type**: markerless biomechanica met commodity camera's
- **Kenmerken**: sterk referentieproject
- **Minpunten**: geen lichte mobiele drop-in SDK; zwaardere multi-camera en processing workflow
- **Status**: open source

### 3.8 [Moverse](https://moverse.ai/)
- **Type**: geavanceerde markerless motion capture en 3D tracking
- **Minpunten**: meer high-end mocap dan directe mobiele coaching-flow
- **Status**: commercieel

### 3.9 [FreeMoCap](https://github.com/freemocap/freemocap)
- **Type**: open-source markerless motion capture platform
- **Kenmerken**: vooral interessant voor multi-camera en research workflows
- **Status**: open source

---

## 4. Open-source & research: sensor-based modellen (IMU / wearable)

Focus: beweging volgen zonder camera's, bruikbaar voor sport in vrije ruimte.

### 4.1 [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
- **Type**: smartphone + watch IMU-based pose estimation
- **Kenmerken**: directe opvolger van IMUPoser; realtime full-body pose en 3D translatie
- **Pluspunten**: open source, interessant voor IMU-gebaseerde realtime full-body pose
- **Minpunten**: niet bedoeld voor visuele body scanning; repo gebruikt `CC BY-NC-SA 4.0` — commerciële inzet vraagt extra licensing review
- **Status**: open source (NC-licentie)

### 4.2 [IMUPoser](https://spice-lab.org/projects/IMUPoser/)
- **Type**: full-body pose estimation met telefoon, watch en earbuds-IMU's
- **Kenmerken**: sterk onderzoeksreferentiepunt
- **Minpunten**: onderzoeksproject, geen productklare mobiele SDK
- **Status**: research

### 4.3 [DIP (Deep Inertial Poser)](https://github.com/eth-ait/dip18)
- **Type**: sparse full-body tracking met beperkt aantal sensoren
- **Status**: open source

### 4.4 [TransPose](https://github.com/Xinyu-Yi/TransPose)
- **Type**: hoge accuraatheid en globale positiebepaling uit sparse IMU-data
- **Status**: open source

### 4.5 [PIP (Physical Inertial Poser)](https://github.com/Xinyu-Yi/PIP)
- **Type**: voegt physics toe om zweven en instabiele bewegingen te beperken
- **Status**: open source

### 4.6 [Xsens MVN](https://www.movella.com/products/motion-capture/xsens-mvn)
- **Type**: professionele mocap-standaard met eigen hardware-ecosysteem
- **Status**: commercieel

### 4.7 [BaroPoser](https://arxiv.org/abs/2401.04283)
- **Type**: combineert IMU met luchtdrukdata voor betere hoogtemeting (trappen, sprongen)
- **Status**: research

---

## 5. Open-source & research: 3D body reconstruction (mesh)

Focus: het maken van een digitale kopie of 3D reconstructie van het lichaam.

### 5.1 [4DHumans / HMR2](https://github.com/shubham-goel/4D-Humans)
- **Type**: 3D human mesh recovery uit enkele video/foto
- **Kenmerken**: state-of-the-art human mesh recovery, SMPL-gebaseerd
- **Pluspunten**: sterk onderzoeksmodel, goede resultaten bij single-camera input
- **Minpunten**: geen mobiele SDK; SMPL body-model licensing moet expliciet worden beoordeeld; significante engineering nodig voor mobile deployment
- **Status**: research / open source

### 5.2 [WHAM](https://github.com/yohanshin/WHAM)
- **Type**: 3D human motion uit video met world-aware tracking
- **Kenmerken**: combineert video-based pose met camera motion estimation, globale trajectbepaling
- **Pluspunten**: indrukwekkende resultaten voor motion-in-context reconstructie
- **Minpunten**: geen mobiele SDK; research project, niet direct production-ready; SMPL licensing review nodig
- **Status**: research / open source

### 5.3 [PIFuHD](https://github.com/facebookresearch/pifuhd)
- **Type**: gedetailleerde 3D mesh uit een enkele foto
- **Kenmerken**: inclusief kledingcontouren
- **Status**: open source

### 5.4 [ECON](https://github.com/YuliangXiu/ECON)
- **Type**: moderne volledige lichaamsreconstructie
- **Kenmerken**: kleding en expliciete mesh-output
- **Status**: open source

### 5.5 [ROMP](https://github.com/Arthur151/ROMP)
- **Type**: lichaamsvorm en pose (SMPL) schatting
- **Kenmerken**: werkt ook bij gedeeltelijke occlusie
- **Status**: open source

### 5.6 [MMHuman3D](https://github.com/open-mmlab/mmhuman3d)
- **Type**: toolbox voor 3D human parametric models en human mesh recovery
- **Status**: open source

### 5.7 [Luma AI](https://lumalabs.ai/)
- **Type**: NeRF / Gaussian Splatting workflows voor 3D scans uit video
- **Status**: free / commercieel

---

## 6. Aanraders en selectiehulp

### Beste opties voor een MVP

**Phone-only posture/form tracking:**
- [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker) — gratis, technische basislaag
- [QuickPose](https://quickpose.ai/) — commerciële exercise/posture SDK, productierijp
- [PoseTracker](https://www.posetracker.com/) — WebView-gebaseerd, laagste integratiedrempel, gratis tier

Waarom: snelle integratie, geen extra hardware nodig, sterk voor realtime feedback, range-of-motion en rep counting.

**Phone-first body scanning / measurements:**
- [3DLOOK](https://3dlook.ai/fitxpress/) — bewezen 2-photo flow, retail-origine
- [Prism Labs](https://www.prismlabs.tech/) — health/fitness-first, web SDK, body composition focus
- [Size Stream](https://www.sizestream.com/mobile-scanning/) — enterprise, brede meetcapaciteiten

Waarom: duidelijke productfit voor smartphone capture, sneller naar een bruikbaar eindproduct dan een eigen research stack.

### Beste opties voor premium / commercial

**Realtime movement assessment / coaching:**
- [LightBuzz](https://lightbuzz.com/) — brede hardware-ondersteuning
- [Kinotek](https://kinotek.com/) — turnkey movement assessment
- [QuickPose](https://quickpose.ai/) — snelste weg naar productie

**3D scanning / measurements:**
- [Prism Labs](https://www.prismlabs.tech/) — health/fitness-first, Noom-validated
- [Size Stream](https://www.sizestream.com/mobile-scanning/) — enterprise/wellness
- [Bodidata Kora](https://www.bodidata.com/) — hoogste precisie (hardware vereist)
- [Bodygee](https://www.bodygee.com/) — visuele progress tracking

### Beste opties voor research / experimentation

**Mesh en human reconstruction:**
- [ECON](https://github.com/YuliangXiu/ECON), [PIFuHD](https://github.com/facebookresearch/pifuhd), [ROMP](https://github.com/Arthur151/ROMP)
- [4DHumans / HMR2](https://github.com/shubham-goel/4D-Humans), [WHAM](https://github.com/yohanshin/WHAM)

**Sensor-based pose estimation:**
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser), [IMUPoser](https://spice-lab.org/projects/IMUPoser/)
- [DIP](https://github.com/eth-ait/dip18), [TransPose](https://github.com/Xinyu-Yi/TransPose), [PIP](https://github.com/Xinyu-Yi/PIP)

Let op: meestal geen mobiele drop-in SDK; vaak aanzienlijke ML- en platform-engineering nodig.

### Niet geschikt of oppassen met licenties

- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose) — commerciële licentievoorwaarden moeten apart worden gevalideerd
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser) — repo gebruikt `CC BY-NC-SA 4.0`; niet zomaar geschikt voor commercieel productgebruik
- `SMPL`-afhankelijke stacks zoals `ROMP`, `MMHuman3D`, `4DHumans`, `WHAM` — body-model licensing moet expliciet worden beoordeeld
- Research-projecten zoals `ECON`, `WHAM`, `FreeMoCap` — vaak prima voor evaluatie, maar niet direct production-ready qua mobiele UX, support of onderhoud

---

## Samenvatting per categorie

### Op basis van techniek
- **Commercieel scanning**: 3DLOOK, Prism Labs, Size Stream, Nettelo, Astrivis, Avatar Body, Bodygee, MyFit, 3DSizeMe, Bodidata
- **Commercieel pose/form**: QuickPose, LightBuzz, Kinotek, PoseTracker
- **Camera-based (open)**: MediaPipe, MMPose, OpenPose, HybrIK, AlphaPose, YOLO11 Pose, OpenCap, FreeMoCap
- **Sensor-based (open)**: MobilePoser, IMUPoser, DIP, TransPose, PIP
- **Mesh/reconstruction (open)**: 4DHumans, WHAM, PIFuHD, ECON, ROMP, MMHuman3D, Luma AI

### Op basis van licentie
- **Open source / free**: MediaPipe, MMPose, DIP, PIFuHD, ECON, AlphaPose, HybrIK, FreeMoCap, YOLO11 Pose
- **Commercieel**: 3DLOOK, Prism Labs, Size Stream, Nettelo, QuickPose, LightBuzz, Kinotek, Xsens, Bodidata, Bodygee, MyFit, Astrivis, Moverse, PoseTracker
- **Free met beperkingen**: OpenPose, MobilePoser (NC)
- **Research**: 4DHumans, WHAM, IMUPoser, BaroPoser
