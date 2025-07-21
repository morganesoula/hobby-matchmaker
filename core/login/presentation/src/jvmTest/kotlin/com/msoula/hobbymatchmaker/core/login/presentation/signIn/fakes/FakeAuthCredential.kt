package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.google.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.AuthCredential as GitLiveAuthCredential

class FakeFirebaseAuthCredential: AuthCredential() {
    override fun getProvider(): String = "google.com"
}

class FakeAuthCredential: GitLiveAuthCredential(FakeFirebaseAuthCredential())
