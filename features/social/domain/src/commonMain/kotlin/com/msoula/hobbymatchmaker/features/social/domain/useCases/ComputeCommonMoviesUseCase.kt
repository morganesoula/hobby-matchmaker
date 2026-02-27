package com.msoula.hobbymatchmaker.features.social.domain.useCases

class ComputeCommonMoviesUseCase {
    operator fun invoke(
        memberMovies: List<Long>,
        userMovies: Set<Long>
    ): Int = memberMovies.count { it in userMovies }
}
