package com.msoula.hobbymatchmaker.features.movies.domain.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeMovieRepository(
    initialMovies: List<MovieDomainModel> = emptyList(),
    private var fetchResult: Result<Unit, MovieErrors> = Result.Success(Unit)
) : MovieRepository {

    private val moviesFlow = MutableSharedFlow<List<MovieDomainModel>>(replay = 1)

    init {
        moviesFlow.tryEmit(initialMovies)
    }

    override fun observeMovies(): Flow<List<MovieDomainModel>> = moviesFlow
    override suspend fun fetchMovies(language: String): Result<Unit, MovieErrors> = fetchResult
    override suspend fun isSynopsisMovieAvailable(movieId: Long): Boolean = true

    fun emitMovies(movies: List<MovieDomainModel>) = moviesFlow.tryEmit(movies)
    fun setFetchResult(result: Result<Unit, MovieErrors>) {
        fetchResult = result
    }

    override suspend fun updateMovieWithFavoriteValue(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) {
        TODO("Not yet implemented")
    }
}
