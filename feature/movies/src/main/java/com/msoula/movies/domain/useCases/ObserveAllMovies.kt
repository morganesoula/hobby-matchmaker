package com.msoula.movies.domain.useCases

import com.msoula.movies.data.model.Movie
import com.msoula.movies.domain.MovieRepository
import kotlinx.coroutines.flow.Flow

fun interface ObserveMoviesUseCase : () -> Flow<List<Movie>?>

fun observeMovies(movieRepository: MovieRepository): Flow<List<Movie>?> = movieRepository.observeMovies()
