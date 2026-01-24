package com.msoula.hobbymatchmaker.features.social.domain.models

sealed interface MovieMatchResult {
    data object NoMatch : MovieMatchResult
    data class Match(val matchingMemberNames: List<String>) : MovieMatchResult
}
