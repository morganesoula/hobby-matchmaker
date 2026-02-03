package com.msoula.hobbymatchmaker.core.user.domain.models

data class UserSummaryDomainModel(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = null,
    val avatarUrl: String? = null,
    val moviesLiked: List<Long> = emptyList()
) {
    companion object {
        val Initial = UserSummaryDomainModel()
    }
}
