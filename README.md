# personal-health

Kotlin multi-module Compose project with a modular architecture for:

- Android mobile (phone + tablet via responsive layout)
- iOS mobile
- Desktop (JVM)
- Web (Wasm)

## Module layout

- `apps/android` Android app entry point
- `apps/ios` iOS integration setup (Xcode + CocoaPods)
- `apps/desktop` Desktop app entry point
- `apps/web` Web app entry point
- `shared/app` Shared app shell, iOS framework export, and composition root
- `feature/home` Home feature UI
- `core/designsystem` Theme and design primitives

## Run

```bash
./gradlew :apps:android:assembleDebug
./gradlew :apps:desktop:run
./gradlew :apps:web:wasmJsBrowserDevelopmentRun
```

## iOS

Generate iOS framework/pod artifacts from shared module:

```bash
./gradlew :shared:app:podspec
```

Then follow setup notes in `apps/ios/README.md`.

Android phone/tablet behavior is handled in shared Compose UI with width-based adaptive layouts.
