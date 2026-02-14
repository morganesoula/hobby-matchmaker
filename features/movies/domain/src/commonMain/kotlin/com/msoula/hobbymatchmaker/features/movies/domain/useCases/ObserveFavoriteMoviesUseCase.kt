package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<List<FavoriteMovieDomainModel>> =
        movieRepository.observeFavoriteMovies()
}
