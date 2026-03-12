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
        guard HKHealthStore.isHealthDataAvailable() else {
            healthKitStatusMessage = "HealthKit not available on this device."
            return
        }

        let healthStore = HKHealthStore()
        var readTypes = Set<HKObjectType>()
        if let step = HKObjectType.quantityType(forIdentifier: .stepCount) { readTypes.insert(step) }
        if let heartRate = HKObjectType.quantityType(forIdentifier: .heartRate) { readTypes.insert(heartRate) }
        if let activeEnergy = HKObjectType.quantityType(forIdentifier: .activeEnergyBurned) { readTypes.insert(activeEnergy) }
        if let bodyMass = HKObjectType.quantityType(forIdentifier: .bodyMass) { readTypes.insert(bodyMass) }
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
