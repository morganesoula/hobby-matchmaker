package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.facebook.login.LoginManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUserSignedInWith
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.AuthCredential

class FacebookAuthProviderImpl(
    private val auth: FirebaseAuth
) : AuthProvider {

    override val type: ProviderType = ProviderType.FACEBOOK

    override suspend fun signIn(credentials: AuthCredential): AppResult<AuthFirebaseUser?, AppError> =
        safeFirebaseCall {
            auth.signInWithCredential(credentials)
                .user?.toAuthFirebaseUserSignedInWith(type.id)
        }

    override suspend fun signOut(): AppResult<Unit, AppError> = safeFirebaseCall {
        LoginManager.getInstance().logOut()
        auth.signOut()
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}
