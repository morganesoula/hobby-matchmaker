package com.msoula.hobbymatchmaker.features.social.presentation.models

data class SessionUiState(
    val userUid: String? = null,
    val userPseudo: String? = null
) {
    companion object {
        val Initial = SessionUiState()
    }
}
