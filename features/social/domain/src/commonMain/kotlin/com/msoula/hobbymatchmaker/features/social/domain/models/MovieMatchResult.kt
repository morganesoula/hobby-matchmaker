package com.msoula.hobbymatchmaker.features.social.domain.models

sealed interface MovieMatchResult {
    data object NoMatch : MovieMatchResult
    data class Match(val matchingMembers: List<MatchingMember>) : MovieMatchResult
}
