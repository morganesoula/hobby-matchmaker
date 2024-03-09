package com.msoula.movies.domain.use_case

import android.util.Log
import com.msoula.movies.domain.MovieRepository

fun interface SetMovieFavoriteUseCase : suspend (Int, Boolean) -> Unit

suspend fun setMovieFavorite(
    movieRepository: MovieRepository,
    movieId: Int,
    isFavorite: Boolean
) {
    Log.d("HMM", "Into SetFavoriteUseCase with $isFavorite")
    movieRepository.updateMovie(movieId, isFavorite)
}
