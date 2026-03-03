package com.msoula.hobbymatchmaker.features.social.domain.models

data class MatchingMemberDomainModel(
    val uid: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null
) {
    companion object Companion {
        val Initial = MatchingMemberDomainModel()
    }
}
