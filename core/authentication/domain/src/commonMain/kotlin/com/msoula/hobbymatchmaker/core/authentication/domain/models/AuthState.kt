package com.msoula.hobbymatchmaker.core.authentication.domain.models

sealed interface AuthState {
    data object SignedOut : AuthState
    data class Authenticated(val user: FirebaseUserInfoDomainModel) : AuthState
}
