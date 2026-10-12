//
//  iOSApp.swift
//  iosApp
//
//  Created by Morgane Soula on 05/10/2025.
//

import SwiftUI
import composeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.all)
        }
    }
}
