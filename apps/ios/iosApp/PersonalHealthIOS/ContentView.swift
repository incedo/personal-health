//
//  ContentView.swift
//  PersonalHealthIOS
//
//  Created by kees on 11/03/2026.
//

import SwiftUI
import SharedApp

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
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
