package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

data class MatchingMemberUiModel(
    val name: String = "",
    val avatarUrl: String? = null
) {
    companion object {
        val Initial = MatchingMemberUiModel()
    }
}
