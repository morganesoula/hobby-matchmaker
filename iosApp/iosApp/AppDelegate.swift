//
//  AppDelegate.swift
//  iosApp
//
//  Created by Morgane Soula on 02/10/2025.
//
/* import UIKit
import FirebaseCore
import composeApp


class AppDelegate : UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        print("🔥 AppDelegate called - func application didFinishLaunching")
        FirebaseApp.configure()
        return true
    }
} */

import UIKit
import FirebaseCore
import composeApp

class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        return true
    }
}
