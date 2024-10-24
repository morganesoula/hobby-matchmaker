package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.common.Result

class SocialMediaSignInUseCase(
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val linkInWithCredentialUseCase: LinkInWithCredentialUseCase,
    private val isFirstSignInUseCase: IsFirstSignInUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo
) {
    suspend operator fun invoke(
        credential: AuthCredential,
        type: String
    ): Result<AuthResult?, SocialMediaError> {
        val firebaseUserInfo = fetchFirebaseUserInfo()
        val isFirstTime = isFirstSignInUseCase(firebaseUserInfo?.uid ?: "")

        val providerId = when (type) {
            "Google" -> "google.com"
            "Facebook" -> "facebook.com"
            else -> ""
        }

        return if (isFirstTime || firebaseUserInfo?.providers?.contains(providerId) == true) {
            signInWithCredentialUseCase(credential)
        } else {
            linkInWithCredentialUseCase(credential)
        }
    }
}
