//
//  File.swift
//  iosApp
//
//  Created by Morgane Soula on 21/02/2025.
//

import SwiftUI
import UIKit
import FirebaseCore
import FirebaseAuth
import GoogleSignIn
import ComposeApp

@objcMembers public class IosNativeSocialCredential: NSObject {

    @objc public static let shared = IosNativeSocialCredential()

    @objc public func getGoogleCredentials(from viewController: UIViewController, completion: @escaping (String?, NSError?) -> Void) {
            guard let clientID = FirebaseApp.app()?.options.clientID else {
                completion(nil, NSError(domain: "GoogleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "Google client ID not found"]))
                return
            }


            let config = GIDConfiguration(clientID: clientID)
            GIDSignIn.sharedInstance.configuration = config

            GIDSignIn.sharedInstance.signIn(withPresenting: viewController) { signInResult, error in
                if let error = error {
                    completion(nil, NSError(domain: "apple.com", code: -1, userInfo: [NSLocalizedDescriptionKey: error.localizedDescription]))
                    return
                }

                guard let user = signInResult?.user,
                      let idToken = user.idToken?.tokenString else {
                    completion(nil, NSError(domain: "GoogleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "User or ID token not found"]))
                    return
                }

                completion(idToken, nil)
            }
        }
}
