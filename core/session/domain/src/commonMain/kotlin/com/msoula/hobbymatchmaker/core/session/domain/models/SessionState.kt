package com.msoula.hobbymatchmaker.core.session.domain.models

sealed interface SessionState {
    data class Guest(val uid: String) : SessionState
    data class Authenticated(val uid: String) : SessionState
}
