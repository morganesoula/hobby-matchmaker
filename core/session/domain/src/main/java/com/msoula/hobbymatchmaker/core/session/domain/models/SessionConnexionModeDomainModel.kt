package com.msoula.hobbymatchmaker.core.session.domain.models

enum class SessionConnexionModeDomainModel {
    FACEBOOK, EMAIL, GOOGLE
}

fun String.toSessionConnexionModeDomainModel(): SessionConnexionModeDomainModel {
    return when (this) {
        "FACEBOOK" -> SessionConnexionModeDomainModel.FACEBOOK
        "GOOGLE" -> SessionConnexionModeDomainModel.GOOGLE
        else -> SessionConnexionModeDomainModel.EMAIL
    }
}
