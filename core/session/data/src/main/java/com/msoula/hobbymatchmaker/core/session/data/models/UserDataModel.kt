package com.msoula.hobbymatchmaker.core.session.data.models

import com.msoula.hobbymatchmaker.core.session.domain.models.ConnexionMode

data class UserDataModel(
    val email: String,
    val connexionMode: ConnexionMode
) {
    companion object {
        const val DEFAULT_EMAIL: String = ""
        const val DEFAULT_CONNEXION_MODE: String = ""
    }
}
