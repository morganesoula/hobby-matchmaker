package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) {
        movieRepository.updateMovieWithFavoriteValue(id, isFavorite)
    }
}
