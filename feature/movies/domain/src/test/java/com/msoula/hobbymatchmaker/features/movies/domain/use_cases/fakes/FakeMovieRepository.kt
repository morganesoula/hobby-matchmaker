package com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class FakeMovieRepository(private val remoteDataSource: FakeMovieRemoteDataSource) :
    MovieRepository {

    override suspend fun fetchMovies(language: String): Result<Unit> {
        return remoteDataSource.fetchMovies(language)
            .mapSuccess { movies ->
                remoteDataSource.upsertMovies(movies)
                Result.Success(Unit)
            }
    }

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return remoteDataSource.observeMovies()
    }

    override suspend fun updateMovieWithFavoriteValue(id: Long, isFavorite: Boolean) {
        return remoteDataSource.setMovieFavoriteValue(movieId = id, isFavorite = isFavorite)
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        TODO("Not yet implemented")
    }

    fun getFirstElement(): MovieDomainModel {
        return remoteDataSource.getFirstElement()
    }

    fun clearData() {
        remoteDataSource.clearData()
    }
}
