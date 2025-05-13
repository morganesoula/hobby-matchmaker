import UIKit
import AuthenticationServices
import Foundation
import CryptoKit

@objcMembers public class AppleSignInManager: NSObject {
    @objc public static let shared = AppleSignInManager()
    private var callback: ((String?, String?, String?) -> Void)?

    private var currentNonce: String?

    @available(iOS 13, *)
    @objc public func signInWithApple(_ completion: @escaping (String?, String?, String?) -> Void) {
        self.currentNonce = randomNonceString()
        self.callback = completion

        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = sha256(currentNonce!)

        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }

    @available(iOS 13, *)
    private func sha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashedData = SHA256.hash(data: inputData)
        let hashString = hashedData.compactMap {
            String(format: "%02x", $0)
        }.joined()

        return hashString
    }

    private func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        var randomBytes = [UInt8](repeating: 0, count: length)
        let errorCode = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
        if errorCode != errSecSuccess {
            fatalError(
                "Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)"
            )
        }

        let charset: [Character] =
            Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")

        let nonce = randomBytes.map { byte in
            charset[Int(byte) % charset.count]
        }

        return String(nonce)
    }

}

extension AppleSignInManager: ASAuthorizationControllerDelegate {
    public func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization authorization: ASAuthorization
    ) {
        guard
            let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
            let identityTokenData = appleIDCredential.identityToken,
            let tokenString = String(data: identityTokenData, encoding: .utf8),
            let nonce = currentNonce else {
                callback?(nil, nil, "Unable to parse Apple ID credentials.")
                return
            }

            callback?(tokenString, nonce, nil)
    }

    public func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: any Error) {
        callback?(nil, nil, error.localizedDescription)
    }
}

extension AppleSignInManager: ASAuthorizationControllerPresentationContextProviding {
    public func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return UIApplication.shared.windows.first { $0.isKeyWindow }!
    }
}
