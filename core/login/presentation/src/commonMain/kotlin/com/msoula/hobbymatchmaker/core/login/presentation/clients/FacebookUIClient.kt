package com.msoula.hobbymatchmaker.core.login.presentation.clients

import dev.gitlive.firebase.auth.AuthCredential

interface FacebookUIClient {
    suspend fun getFacebookCredentials(): Pair<AuthCredential, String?>
}
