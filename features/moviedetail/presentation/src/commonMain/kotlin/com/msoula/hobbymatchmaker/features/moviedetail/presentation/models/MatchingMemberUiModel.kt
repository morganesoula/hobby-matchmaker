package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

data class MatchingMemberUiModel(
    val uid: String = "",
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null
) {
    companion object {
        val Initial = MatchingMemberUiModel()
    }
}
