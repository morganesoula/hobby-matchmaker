import UIKit
import FirebaseCore
import FirebaseAuth
import GoogleSignIn

@objcMembers public class IosNativeSocialCredential: NSObject {

    @objc public static let shared = IosNativeSocialCredential()

    @objc public func getGoogleCredentials(from viewController: UIViewController, completion: @escaping (String?, String?, NSError?) -> Void) {
            guard let clientID = FirebaseApp.app()?.options.clientID else {
                completion(nil, nil, NSError(domain: "GoogleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "Google client ID not found"]))
                return
            }

            let config = GIDConfiguration(clientID: clientID)
            GIDSignIn.sharedInstance.configuration = config

            GIDSignIn.sharedInstance.signIn(withPresenting: viewController) { signInResult, error in
                if let error = error {
                    completion(nil, nil, NSError(domain: "apple.com", code: -1, userInfo: [NSLocalizedDescriptionKey: error.localizedDescription]))
                    return
                }

                guard let user = signInResult?.user else {
                    completion(nil, nil, NSError(domain: "GoogleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "User not found"]))
                    return
                }

                let idToken = user.idToken?.tokenString
                let accessToken = user.accessToken.tokenString

                guard let validIdToken = idToken else {
                    completion(nil, nil, NSError(domain: "GoogleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "ID token not found"]))
                    return
                }

                completion(validIdToken, accessToken, nil)
            }
        }
}
