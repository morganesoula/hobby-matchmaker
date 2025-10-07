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

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {

        FirebaseApp.configure()
        KoinHelperKt.doInitKoin()

        let window = UIWindow(frame: UIScreen.main.bounds)

        let vc = MainViewControllerKt.MainViewController()

        // Forcer l’edge-to-edge
        vc.edgesForExtendedLayout = [.top, .bottom]
        vc.extendedLayoutIncludesOpaqueBars = true
        vc.additionalSafeAreaInsets = .zero
        vc.view.insetsLayoutMarginsFromSafeArea = false
        vc.viewRespectsSystemMinimumLayoutMargins = false

        window.rootViewController = vc
        
        let debugStrip = UIView(frame: CGRect(x: 0, y: 0, width: window.bounds.width, height: 2))
        debugStrip.backgroundColor = .green
        window.addSubview(debugStrip)
        
        window.makeKeyAndVisible()
        self.window = window
        return true
    }
}
