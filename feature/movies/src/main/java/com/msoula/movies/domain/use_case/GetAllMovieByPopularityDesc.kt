package com.msoula.movies.domain.use_case

import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.data.mapper.MapMovieEntityToMovie
import com.msoula.movies.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun interface GetMovieUseCase : () -> Flow<MovieUiStateResult>

fun getMovies(
    movieRepository: MovieRepository,
    mapper: MapMovieEntityToMovie
): Flow<MovieUiStateResult> {
    return movieRepository
        .getMoviesByPopularityDesc()
        .map { movies ->
            if (movies.isEmpty()) {
                MovieUiStateResult.Error(Exception("Empty movies"))
            } else {
                MovieUiStateResult.Fetched(mapper.mapList(movies))
            }
        }
        .catch {
            emit(MovieUiStateResult.Error(it.cause))
        }
}
