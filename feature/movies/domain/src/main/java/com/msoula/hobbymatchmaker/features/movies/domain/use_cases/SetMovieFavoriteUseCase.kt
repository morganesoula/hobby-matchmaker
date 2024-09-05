package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import android.util.Log
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) {
        Log.d("HMM", "SetMovieFavoriteUseCase: $id, $isFavorite")
        movieRepository.updateMovieWithFavoriteValue(id, isFavorite)
    }
}
