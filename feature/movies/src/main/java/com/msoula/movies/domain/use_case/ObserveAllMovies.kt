package com.msoula.movies.domain.use_case

import android.util.Log
import com.msoula.movies.data.model.Movie
import com.msoula.movies.domain.MovieRepository
import kotlinx.coroutines.flow.Flow

fun interface ObserveMoviesUseCase : () -> Flow<List<Movie>?>

fun observeMovies(
    movieRepository: MovieRepository
): Flow<List<Movie>?> {
    Log.d("HMM", "Into UseCase")
    return movieRepository.observeMovies()
}
