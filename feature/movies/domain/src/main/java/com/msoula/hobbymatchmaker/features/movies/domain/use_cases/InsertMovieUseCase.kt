package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class InsertMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movie: MovieDomainModel) {
        movieRepository.insertMovie(movie)
    }
}
