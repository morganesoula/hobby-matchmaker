package com.msoula.hobbymatchmaker.core.session.domain.models

data class SessionUserDomainModel(
    val email: String,
    val connexionMode: SessionConnexionModeDomainModel
)
