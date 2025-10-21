package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.providers

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUserSignedInWith
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class GoogleAuthProvider(private val auth: FirebaseAuth) : AuthProvider {
    override val type: ProviderType = ProviderType.GOOGLE

    override suspend fun signIn(credentials: AuthCredential): AppResult<AuthFirebaseUser?, AppError> =
        safeFirebaseCall {
            auth.signInWithCredential(credentials)
                .user?.toAuthFirebaseUserSignedInWith(type.id)
        }

    override suspend fun signOut(): AppResult<Unit, AppError> = safeFirebaseCall { auth.signOut() }
    override fun isSignedIn(): Boolean = auth.currentUser != null
}
