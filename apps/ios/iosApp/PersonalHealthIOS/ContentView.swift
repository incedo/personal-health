//
//  ContentView.swift
//  PersonalHealthIOS
//
//  Created by kees on 11/03/2026.
//

import SwiftUI
import HealthKit
import SharedApp

struct ContentView: View {
    @State private var didRequestHealthPermissions = false
    @State private var healthKitStatusMessage: String?
    private let healthStore = HKHealthStore()

    var body: some View {
        VStack(spacing: 0) {
            ComposeView()
                .ignoresSafeArea()

            if let healthKitStatusMessage {
                Text(healthKitStatusMessage)
                    .font(.footnote)
                    .padding(8)
            }
        }
        .onAppear {
            guard !didRequestHealthPermissions else { return }
            didRequestHealthPermissions = true
            requestHealthKitReadAccess()
        }
    }

    private func requestHealthKitReadAccess() {
        #if targetEnvironment(simulator)
        healthKitStatusMessage = "HealthKit import is disabled in the iOS Simulator."
        return
        #endif

        guard HKHealthStore.isHealthDataAvailable() else {
            healthKitStatusMessage = "HealthKit not available on this device."
            return
        }

        var readTypes = Set<HKObjectType>()
        if let step = HKObjectType.quantityType(forIdentifier: .stepCount) { readTypes.insert(step) }
        if let heartRate = HKObjectType.quantityType(forIdentifier: .heartRate) { readTypes.insert(heartRate) }
        if let activeEnergy = HKObjectType.quantityType(forIdentifier: .activeEnergyBurned) { readTypes.insert(activeEnergy) }
        if let bodyMass = HKObjectType.quantityType(forIdentifier: .bodyMass) { readTypes.insert(bodyMass) }
        if let height = HKObjectType.quantityType(forIdentifier: .height) { readTypes.insert(height) }
        if let bodyFat = HKObjectType.quantityType(forIdentifier: .bodyFatPercentage) { readTypes.insert(bodyFat) }
        if let bmi = HKObjectType.quantityType(forIdentifier: .bodyMassIndex) { readTypes.insert(bmi) }
        if let systolic = HKObjectType.quantityType(forIdentifier: .bloodPressureSystolic) { readTypes.insert(systolic) }
        if let diastolic = HKObjectType.quantityType(forIdentifier: .bloodPressureDiastolic) { readTypes.insert(diastolic) }
        if let glucose = HKObjectType.quantityType(forIdentifier: .bloodGlucose) { readTypes.insert(glucose) }
        if let oxygen = HKObjectType.quantityType(forIdentifier: .oxygenSaturation) { readTypes.insert(oxygen) }
        if let bodyTemperature = HKObjectType.quantityType(forIdentifier: .bodyTemperature) { readTypes.insert(bodyTemperature) }
        if let dietaryWater = HKObjectType.quantityType(forIdentifier: .dietaryWater) { readTypes.insert(dietaryWater) }
        if let dietaryEnergy = HKObjectType.quantityType(forIdentifier: .dietaryEnergyConsumed) { readTypes.insert(dietaryEnergy) }
        if let sleep = HKObjectType.categoryType(forIdentifier: .sleepAnalysis) { readTypes.insert(sleep) }

        healthStore.requestAuthorization(toShare: nil, read: readTypes) { granted, error in
            DispatchQueue.main.async {
                if granted {
                    let bridge = IOSSharedUiBridge()
                    bridge.startIosHealthHistoryImport()
                    bridge.startIosHealthLiveSync()
                    healthKitStatusMessage = "HealthKit access granted, import and live sync started."
                } else {
                    healthKitStatusMessage = error?.localizedDescription ?? "HealthKit access not granted."
                }
            }
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        if let controller = IOSSharedUiBridge().makeRootViewController() as? UIViewController {
            return controller
        }
        return UIViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

#Preview {
    ContentView()
}
