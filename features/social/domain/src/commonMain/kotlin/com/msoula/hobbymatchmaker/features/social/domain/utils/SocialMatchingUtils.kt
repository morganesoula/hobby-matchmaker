package com.msoula.hobbymatchmaker.features.social.domain.utils

object SocialMatchingUtils {
    fun calculateCommonMoviesCount(ownerMovies: List<Long>?, memberMovies: List<Long>?): Int {
        if (ownerMovies.isNullOrEmpty() || memberMovies.isNullOrEmpty()) return 0
        return ownerMovies.intersect(memberMovies.toSet()).size
    }

    fun hasMoviesInCommon(movieId: Long, memberMovies: List<Long>?): Boolean {
        if (memberMovies.isNullOrEmpty()) return false
        return movieId in memberMovies
    }
}
