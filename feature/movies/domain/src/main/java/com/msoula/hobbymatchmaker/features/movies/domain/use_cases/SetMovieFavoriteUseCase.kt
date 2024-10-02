package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(uuidUser: String, id: Long, isFavorite: Boolean) {
        movieRepository.updateMovieWithFavoriteValue(uuidUser, id, isFavorite)
    }
}
