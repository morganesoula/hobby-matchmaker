package com.msoula.hobbymatchmaker.features.movies.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDB
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRepository {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
            .map { list ->
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) =
        movieLocalDataSource.updateMovieWithLocalCoverFilePath(
            coverFileName,
            localCoverFilePath,
            movieId
        )

    override suspend fun fetchMovies(language: String): AppResult<Unit, AppError> =
        movieRemoteDataSource.fetchMovies(language)
            .flatMap { movies ->
                movieLocalDataSource.upsertAll(movies.map { it.toMovieDB() })
            }

    override suspend fun updateMovieFavoriteLocal(id: Long, isFavorite: Boolean) =
        movieLocalDataSource.updateMovieWithFavoriteValue(id, isFavorite)

    override suspend fun updateMovieFavoriteRemote(
        uid: String,
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> =
        movieRemoteDataSource.updateUserFavoriteMovieList(uid, id, isFavorite)

    override suspend fun isSynopsisMovieAvailable(movieId: Long): AppResult<Boolean, AppError> =
        movieLocalDataSource.isMovieSynopsisAvailable(movieId)

    override suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError> =
        movieLocalDataSource.getFavoriteLocalMovieIds()

    override suspend fun syncUserFavoritesRemote(
        uid: String,
        localIds: List<Long>
    ): AppResult<Unit, AppError> = movieRemoteDataSource.setUserFavoriteMovies(uid, localIds)
}
