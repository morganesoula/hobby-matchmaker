package com.msoula.hobbymatchmaker.core.login.presentation.clients

import dev.gitlive.firebase.auth.AuthCredential

class IosFacebookUIClient : FacebookUIClient {

    override suspend fun getFacebookCredentials(): Pair<AuthCredential?, String?> =
        Pair(null, null)

    override fun registerCallback(
        onSuccess: (AuthCredential, String?) -> Unit,
        onError: (Exception) -> Unit
    ) = Unit

    override fun logIn() = Unit
}
