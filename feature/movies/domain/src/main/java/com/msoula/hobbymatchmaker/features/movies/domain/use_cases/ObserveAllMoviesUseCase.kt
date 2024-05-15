package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllMoviesUseCase(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<List<MovieDomainModel>> {
        return movieRepository.observeMovies()
    }
}
