# Health Integrations

## Canonical model
- Shared model lives in `core/health`.
- All platform records map to:
  - `HealthRecord`
  - `HealthMetricType`
  - `HealthDataSource`
  - `HealthReadRequest`
  - `HealthDataGateway`
- Canonical domain/type mapping (Health Connect + HealthKit):
  - `docs/unified-health-model-mapping.md`
- Local persistence contract and platform storage plan:
  - `docs/local-activity-storage.md`
- Canonical browser import document shape:
  - `docs/canonical-health-import-format.md`

## Optional body capture direction
- Planned as an optional capability, separate from Health Connect / HealthKit ingestion.
- Supports two modes:
  - `2D` guided body capture via standard device cameras
  - `3D` body capture via supported depth cameras or body-tracker sensors
- Shared layers should consume only a canonical body-capture model for:
  - capture mode
  - posture landmarks
  - derived body measurements
  - scan quality / confidence
  - capture source
- Platform camera, depth, and sensor APIs must stay behind abstractions in platform-facing modules.
- Feature/UI layers must not depend on raw SDK record types from camera or motion-capture vendors.
- Device capability detection should expose whether the current platform supports:
  - `2D` camera capture only
  - `3D` capture with native hardware
  - `3D` capture with external tracker sensors
  - no supported body capture
- Practical feasibility notes:
  - one phone or one webcam is sufficient for guided `2D` capture
  - one phone or one webcam can support approximate `3D` pose or body-mesh estimation
  - one standard camera is usually not enough for a high-quality full `3D` body scan
  - multi-camera capture produces better `3D` reconstruction quality than single-camera capture
  - product copy should distinguish between `3D pose/mesh estimation` and a true `3D body scan`
- Additional open-source reference:
  - [MobilePoser](https://github.com/SPICExLAB/MobilePoser) is relevant for real-time full-body pose estimation from mobile IMU sensors; it is better categorized as posture/motion tracking infrastructure than as a body-scanning stack
- Reference implementations / product inspiration:
  - [OpenCap](https://www.opencap.ai/get-started) for markerless guided capture workflows using commodity cameras
  - [Moverse](https://moverse.ai/) for more advanced real-time markerless motion capture and 3D tracking
  - [LightBuzz](https://lightbuzz.com/) as a commercial integration option for body tracking across webcam, depth-camera, and LiDAR-based capture setups

## Evaluation shortlist
### 3D body scanning
- [3DLOOK](https://3dlook.ai/fitxpress/)
  - Pros: mobile-first two-photo flow, strong API/SDK messaging, good fit for consumer body-measurement UX.
  - Cons: more sizing/progress oriented than biomechanics-grade scanning.
- [Size Stream](https://www.sizestream.com/mobile-scanning/)
  - Pros: enterprise-ready smartphone scanning, many derived measurements, wellness/research relevance.
  - Cons: likely heavier commercial integration and less focused on live coaching.
- [Astrivis](https://astrivis.com/3d-body-scanner)
  - Pros: technically strong 3D scanning positioning, on-device preview, iOS/Android support.
  - Cons: public materials make SDK maturity less obvious than some larger vendors.
- [Avatar Body](https://www.avatar3d.tech/3d-body-scanner-app-avatar-body/)
  - Pros: simple two-photo capture story, API integration, measurement-focused.
  - Cons: appears more like a service/API than a broad developer platform.
- [Bodygee](https://www.bodygee.com/)
  - Pros: relevant for progress tracking and body-shape change workflows.
  - Cons: less clearly positioned as a generic embeddable SDK.

### Pose tracking and form advice
- [LightBuzz](https://lightbuzz.com/)
  - Pros: strong commercial fit for real-time posture/form guidance across webcam, depth, and LiDAR capture.
  - Cons: commercial SDK complexity may be unnecessary for a lightweight MVP.
- [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
  - Pros: strong phone-only baseline, on-device, official Android/iOS/Web support, good for fast MVP iteration.
  - Cons: requires app-side development for rep logic, angle heuristics, feedback rules, and smoothing.
- [OpenCap](https://www.opencap.ai/get-started)
  - Pros: academically credible markerless biomechanics reference using commodity cameras.
  - Cons: not a lightweight in-app SDK; better suited to heavier multi-camera analysis workflows.
- [Moverse](https://moverse.ai/)
  - Pros: advanced markerless motion-capture direction with strong 3D tracking positioning.
  - Cons: closer to professional mocap than to simple real-time mobile coaching.
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - Pros: interesting IMU-based open-source approach for full-body pose estimation on consumer devices.
  - Cons: license and productization constraints make it less straightforward for commercial app embedding.
- [IMUPoser](https://spice-lab.org/projects/IMUPoser/)
  - Pros: strong research reference for estimating full-body pose from mobile and wearable IMUs.
  - Cons: research project rather than production-ready cross-platform SDK.

### AI model and toolbox layer
- Recent AI interest is producing more reusable pose/body-estimation models, but many are model toolboxes rather than ready-made app integrations.
- Relevant current building blocks include:
  - [MMPose](https://github.com/open-mmlab/mmpose) for 2D, 3D, whole-body, and newer realtime model families
  - [MMHuman3D](https://github.com/open-mmlab/mmhuman3d) for human mesh recovery and parametric body modeling
  - [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose) as a classic body/face/hand keypoint baseline
  - [FreeMoCap](https://github.com/freemocap/freemocap) for open-source markerless motion capture workflows
- These are best treated as engineering building blocks when the product wants to own the inference stack instead of buying a vendor SDK or API.

## AI model overview
### Sensor-based models
- Focus: tracking movement without cameras, useful for sports in open space.
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - Pros: direct `IMUPoser` successor, consumer-device oriented, relevant for realtime full-body pose.
  - Cons: better for pose tracking than for visual `3D` body scanning; commercial licensing needs review.
- [DIP (Deep Inertial Poser)](https://github.com/eth-ait/dip18)
  - Pros: foundational sparse-IMU full-body pose research baseline.
  - Cons: research-oriented and not a product-ready mobile SDK.
- [TransPose](https://github.com/Xinyu-Yi/TransPose)
  - Pros: strong global position estimation from sparse IMUs.
  - Cons: still a research stack that requires significant engineering around it.
- [PIP (Physical Inertial Poser)](https://github.com/Xinyu-Yi/PIP)
  - Pros: adds physics constraints to reduce floating and unstable motion.
  - Cons: not positioned as a simple embeddable app SDK.
- [Xsens MVN](https://www.movella.com/products/motion-capture/xsens-mvn)
  - Pros: established professional mocap ecosystem with strong motion fidelity.
  - Cons: commercial hardware and software stack, far heavier than a consumer wellness app MVP.
- [BaroPoser](https://arxiv.org/abs/2401.04283)
  - Pros: interesting research direction that improves height-change estimation with barometer data.
  - Cons: research paper direction, not an off-the-shelf integration.

### Camera-based models
- Focus: joint-angle estimation and posture analysis via video.
- [MediaPipe BlazePose / Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
  - Pros: excellent phone-only baseline, realtime on device, official cross-platform support.
  - Cons: product logic for feedback, form scoring, and smoothing still has to be built in-house.
- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose)
  - Pros: robust classic multi-person body/face/hand tracking baseline.
  - Cons: licensing and commercial-use constraints need explicit validation.
- [HybrIK](https://github.com/jeffffffli/HybrIK)
  - Pros: anatomically informed `3D` pose from `2D` imagery.
  - Cons: more model/research infrastructure than a mobile-ready product integration.
- [AlphaPose](https://github.com/MVIG-SJTU/AlphaPose)
  - Pros: strong accuracy in complex sports and multi-person scenes.
  - Cons: requires engineering effort to operationalize for realtime mobile use.

### 3D body scanning and reconstruction
- Focus: generating a digital body copy, shape estimate, or mesh.
- [PIFuHD](https://github.com/facebookresearch/pifuhd)
  - Pros: detailed clothed human mesh reconstruction from a single image.
  - Cons: not a turnkey mobile scanning SDK; operational quality depends heavily on input conditions.
- [ECON](https://github.com/YuliangXiu/ECON)
  - Pros: strong modern clothed-human reconstruction direction with explicit mesh output.
  - Cons: research-grade stack rather than direct end-user app integration.
- [ROMP](https://github.com/Arthur151/ROMP)
  - Pros: recovers body shape and pose under partial occlusion using `SMPL`.
  - Cons: `SMPL` family licensing needs review before commercial use.
- [Luma AI](https://lumalabs.ai/)
  - Pros: strong video-to-`3D` capture direction using newer reconstruction paradigms.
  - Cons: more scene/object capture oriented than a dedicated health body-scan SDK.

### Project guidance
- For `3D` scan exploration:
  - evaluate `ECON` and `PIFuHD` as research references for geometry and mesh reconstruction
- For sport tracking:
  - evaluate `MobilePoser`, `IMUPoser`, `DIP`, `TransPose`, and `PIP` when wearable sensors are acceptable
  - evaluate `MediaPipe` first when the target is camera-on-tripod or phone-only posture analysis

## Recommendation layers
### Best for MVP
- `Phone-only posture/form tracking`:
  - [MediaPipe Pose Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker)
  - [QuickPose](https://quickpose.ai/)
- Why:
  - low hardware friction
  - good fit for realtime exercise feedback, posture cues, and rep counting
  - more realistic delivery path than a custom research stack
- `Phone-first body scanning / measurements`:
  - [3DLOOK](https://3dlook.ai/fitxpress/)
  - [Size Stream](https://www.sizestream.com/mobile-scanning/)
- Why:
  - productized smartphone capture flows
  - better path to usable measurements than building body reconstruction from research models

### Best for premium / commercial
- `Realtime coaching / movement assessment`:
  - [LightBuzz](https://lightbuzz.com/)
  - [Kinotek](https://kinotek.com/)
- Why:
  - commercial support, productized workflows, and clearer go-to-market fit for coaching experiences
- `3D scanning / measurement workflows`:
  - [Size Stream](https://www.sizestream.com/mobile-scanning/)
  - [Bodidata](https://www.bodidata.com/)
  - [Bodygee](https://www.bodygee.com/)
- Why:
  - stronger scanning business maturity than research-only stacks
  - better suited when partner support and service reliability matter

### Best for research / experimentation
- `Human reconstruction and mesh`:
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
- Why:
  - strongest when the team wants to own the inference stack or prototype novel body-capture flows
  - weakest when the goal is fast, low-risk productization

### Use caution
- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose)
  - licensing needs separate validation for commercial deployment
- [MobilePoser](https://github.com/SPICExLAB/MobilePoser)
  - repository license and commercial rights need explicit review
- `SMPL`-dependent stacks such as `ROMP`, `MMHuman3D`, and `4DHumans`
  - dependent model licensing may constrain commercial product use
- Research frameworks such as `ECON`, `WHAM`, and `FreeMoCap`
  - strong references, but generally not production-ready mobile SDKs

## Event-based sync
- Shared event contracts and bus live in `core/events`:
  - `AppEvent`
  - `AppEventBus`
  - `InMemoryAppEventBus`
- Frontend event categories include:
  - `FrontendEvent.UiFeedbackRequested`
  - `FrontendEvent.NavigationChanged`
  - `FrontendEvent.SyncStateChanged`
- Health-specific domain events live in `core/health`:
  - `HealthEvent`
- Integration gateways publish `HealthEvent.RecordsRead` after read operations.
- Live sync now uses a dedicated abstraction layer in `core/health`:
  - `HealthChangeSignalSource` for platform-specific change detection
  - `HealthLiveSyncProcessor` for shared sync behavior and event publishing
  - Intent-style events:
    - `HealthEvent.LiveSyncIntentReceived`
    - `HealthEvent.LiveSyncIntentSkippedDuplicate`
    - `HealthEvent.LiveSyncIntentApplied`
    - `HealthEvent.LiveSyncIntentFailed`
  - Metric-specific intent events (current implemented metrics):
    - `HealthEvent.LiveSyncStepsIntentApplied`
    - `HealthEvent.LiveSyncHeartRateIntentApplied`
    - `HealthEvent.LiveSyncSleepIntentApplied`
    - `HealthEvent.LiveSyncActiveEnergyIntentApplied`
    - `HealthEvent.LiveSyncBodyWeightIntentApplied`
- Features can subscribe to `AppEventBus.events` for realtime in-app sync without DB polling.
- Note: cross-device or cross-user realtime sync still needs network transport/backend eventing; the in-memory bus covers in-process module sync.

## Local persistence direction
- Imported and manually logged activities should be stored as canonical `HealthRecord` values through `LocalActivityStore` in `core/health`.
- Platform-specific integrations should act as ingestion adapters only.
- Recommended storage engine by platform:
  - Android: shared SQLite schema with native Android driver
  - iOS: shared SQLite schema with native iOS driver
  - Desktop: shared SQLite schema with JVM driver
  - Web: SQLite WASM first, IndexedDB fallback behind the same repository contract

## Web import direction
- Web has no direct access to Health Connect or HealthKit.
- Web import should use a canonical JSON document that represents shared model parts instead of raw platform records.
- The current browser import flow is designed around:
  - one import window
  - canonical `HealthRecord` entries
  - step imports that can be rebuilt into hourly charts

## Android: Health Connect
- Module: `integration/health-connect` (Android-only)
- Gateway: `HealthConnectGateway`
- Change source: `HealthConnectPollingSignalSource` (platform polling encapsulated in Android module)
- Current scope:
  - Reads historical data for:
    - `STEPS`
    - `HEART_RATE_BPM`
    - `SLEEP_DURATION_MINUTES`
    - `ACTIVE_ENERGY_KCAL`
    - `BODY_WEIGHT_KG`
  - Uses paging internally for long history ranges
  - Exposes `requiredPermissions()` for supported read types
  - Exposes `isAvailable(context)`
  - Publishes `HealthEvent.RecordsRead` after read operations

## iOS: HealthKit
- Module: `integration/healthkit` (iOS-only)
- Gateway: `HealthKitGateway`
- Change source: `HealthKitObserverSignalSource` (`HKObserverQuery` encapsulated in iOS module)
- Current scope:
  - Detects HealthKit availability
  - Reads historical data for:
    - `STEPS`
    - `HEART_RATE_BPM`
    - `SLEEP_DURATION_MINUTES`
    - `ACTIVE_ENERGY_KCAL`
    - `BODY_WEIGHT_KG`
  - Uses date-range sample queries and maps all records to canonical `HealthRecord`
  - Publishes `HealthEvent.RecordsRead` after read operations
  - App layer remains responsible for requesting HealthKit read permissions before calling gateway reads

## Wiring
- `shared/app` depends on:
  - `core/events` in `commonMain`
  - `core/health` in `commonMain`
  - `integration/health-connect` in `androidMain`
  - `integration/healthkit` in `iosMain`

## Shared history import orchestration
- `core/health` includes:
  - `HealthHistoryImportRequest`
  - `HealthHistoryImporter`
- Importer behavior:
  - Splits large ranges into deterministic batch windows
  - Reads each window through `HealthDataGateway`
  - Emits `FrontendEvent.SyncStateChanged` with `SYNCING`, then `UP_TO_DATE` or `ERROR`

## Shared live sync orchestration
- `core/health` also includes:
  - `HealthChangeSignal`
  - `HealthChangeTrigger`
  - `HealthLiveSyncProcessor`
  - `HealthSyncCheckpointStore`
- Processor behavior:
  - Accepts platform `HealthChangeSignal` events from the signal source
  - Uses intent-style eventing with explicit event names
  - Publishes metric-specific events through a metric-event factory registry (modular dispatch)
  - Uses `intentId` + `HealthSyncCheckpointStore` to skip duplicate intent processing
  - Performs bounded read from `HealthDataGateway` using configured lookback
  - Emits `FrontendEvent.SyncStateChanged` for `health-live-sync:*` channels
  - Emits intent-style health events for UI/features to consume immediately
