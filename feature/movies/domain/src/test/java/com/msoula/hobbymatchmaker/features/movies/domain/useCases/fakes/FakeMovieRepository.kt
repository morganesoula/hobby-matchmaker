package com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class FakeMovieRepository(
    private val remoteDataSource: FakeMovieRemoteDataSource,
    private val localDataSource: FakeMovieLocalDataSource
) :
    MovieRepository {

    override suspend fun fetchMovies(language: String): Result<Unit, MovieErrors> {
        return remoteDataSource.fetchMovies(language)
            .mapSuccess { movies ->
                localDataSource.upsertAll(movies)
                Result.Success(Unit)
            }
    }

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return localDataSource.observeMovies()
    }

    override suspend fun updateMovieWithFavoriteValue(uuidUser: String, id: Long, isFavorite: Boolean) {
        return localDataSource.updateMovieWithFavoriteValue(id, isFavorite)
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        TODO("Not yet implemented")
    }

    fun getFirstElement(): MovieDomainModel {
        return localDataSource.getFirstElement()
    }

    fun clearData() {
        remoteDataSource.clearData()
        localDataSource.clearData()
    }
}
