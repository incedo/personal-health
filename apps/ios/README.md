# iOS app setup

The shared Kotlin code now exposes an iOS framework (`SharedApp`) with a Compose entry point:

- `MainViewController()` from package `com.incedo.personalhealth.shared`

## 1) Generate CocoaPods artifacts

```bash
./gradlew :shared:app:podspec
```

## 2) Create/open iOS app in Xcode

Create a SwiftUI iOS app in `apps/ios/iosApp` (or open your existing app there).

## 3) Podfile example

In your iOS app directory, create `Podfile`:

```ruby
platform :ios, '15.0'
use_frameworks!

target 'PersonalHealthIOS' do
  pod 'SharedApp', :path => '../../../shared/app'
end
```

Then run:

```bash
pod install
```

## 4) Bridge Compose into SwiftUI

Use `MainViewController` from `SharedApp` framework via `UIViewControllerRepresentable`.
A starter file is included at `apps/ios/iosApp/PersonalHealthIOS/ContentView.swift`.
