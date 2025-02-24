package com.msoula.hobbymatchmaker.core.login.presentation.clients

import dev.gitlive.firebase.auth.AuthCredential

interface GoogleUIClient {
    suspend fun getGoogleCredentials(): AuthCredential?
}
