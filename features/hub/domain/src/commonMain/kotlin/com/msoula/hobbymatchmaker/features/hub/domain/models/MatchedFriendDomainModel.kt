package com.msoula.hobbymatchmaker.features.hub.domain.models

data class MatchedFriendDomainModel(
    val uid: String = "",
    val displayName: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val sharedMovieIds: List<Long> = emptyList(),
) {
    companion object {
        val Initial = MatchedFriendDomainModel()
    }
}
