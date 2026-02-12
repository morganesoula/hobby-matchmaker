package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.facebook.login.LoginManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUserSignedInWith
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class FacebookAuthProviderImpl(
    private val auth: FirebaseAuth
) : AuthProvider {

    override val type: ProviderTypeDataModel = ProviderTypeDataModel.FACEBOOK

    override suspend fun signIn(credentials: AuthCredential): AppResult<AuthUserRemoteDataModel?, AppError> =
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
