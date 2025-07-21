package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential

class FakeSocialClient : SocialUIClient {
    override val providerType: ProviderType = ProviderType.GOOGLE

    override suspend fun getCredential(): AuthCredential? {
        return FakeAuthCredential()
    }
}

