package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class ShouldRefreshMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): Boolean {
        val lastSync = movieRepository.getLastMovieSyncTimestamp()
        val now = Clock.System.now().toEpochMilliseconds()
        val staleDuration = 24.hours.inWholeMilliseconds
        return (now - lastSync) > staleDuration
    }
}
