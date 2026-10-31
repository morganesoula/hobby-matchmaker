package com.msoula.hobbymatchmaker.features.social.domain.models

data class BlockedUserDomainModel(
    val uid: String = "",
    val pseudo: String? = null,
    val blockedAt: Long? = null
)
