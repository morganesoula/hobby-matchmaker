package com.msoula.hobbymatchmaker.features.movies.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieSyncPreferences
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDB
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.PaginationInfo
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlin.time.Clock

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieSyncPreferences: MovieSyncPreferences,
    private val imageRepository: ImageRepository
) : MovieRepository {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
            .map { list ->
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
    }

    override fun observeMoviesLikedCount(): Flow<Long> =
        movieLocalDataSource.observeMoviesLikedCount()

    override fun observeLikedMoviesIds(): Flow<List<Long>> =
        movieLocalDataSource.observeLikedMoviesIds()

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
        movieRemoteDataSource.fetchMovies(language).flatMap { movies ->
            movieLocalDataSource.upsertAll(movies.map { it.toMovieDB() }).flatMap {
                supervisorScope {
                    movies.mapNotNull { m ->
                        val id = m.id ?: return@mapNotNull null
                        val remotePath = m.poster ?: return@mapNotNull null

                        async {
                            try {
                                val local = imageRepository.getRemoteImage(remotePath)
                                if (!local.isNullOrBlank()) {
                                    movieLocalDataSource.updateMovieWithLocalCoverFilePath(
                                        coverFileName = remotePath,
                                        localCoverFilePath = local,
                                        movieId = id.toLong()
                                    )
                                }
                            } catch (e: Exception) {
                                Logger.e("MovieRepositoryImpl - Error downloading image: ${e.message}")
                            }
                        }
                    }.awaitAll()
                }
                AppResult.Success(Unit)
            }
        }

    override suspend fun loadMoreMovies(
        language: String,
        page: Int
    ): AppResult<PaginationInfo, AppError> =
        movieRemoteDataSource.fetchMoviesPage(language, page).flatMap { paginatedMovieResult ->
            movieLocalDataSource.upsertAll(paginatedMovieResult.movies.map { it.toMovieDB() })
                .flatMap {
                    supervisorScope {
                        paginatedMovieResult.movies.mapNotNull { movie ->
                            val id = movie.id ?: return@mapNotNull null
                            val remotePath = movie.poster ?: return@mapNotNull null

                            async {
                                try {
                                    val local = imageRepository.getRemoteImage(remotePath)
                                    if (!local.isNullOrBlank()) {
                                        movieLocalDataSource.updateMovieWithLocalCoverFilePath(
                                            coverFileName = remotePath,
                                            localCoverFilePath = local,
                                            movieId = id.toLong()
                                        )
                                    }
                                } catch (e: Exception) {
                                    Logger.e("MovieRepositoryImpl - Error downloading image: ${e.message}")
                                }
                            }
                        }.awaitAll()
                    }

                    movieSyncPreferences.setLastSyncTimestamp(
                        Clock.System.now().toEpochMilliseconds()
                    )
                    movieSyncPreferences.setLastLoadedPage(paginatedMovieResult.currentPage)

                    AppResult.Success(
                        PaginationInfo(
                            currentPage = paginatedMovieResult.currentPage,
                            totalPages = paginatedMovieResult.totalPages,
                            hasMore = paginatedMovieResult.hasMore
                        )
                    )
                }
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

    override suspend fun getLastMovieSyncTimestamp() =
        movieSyncPreferences.getLastSyncTimestamp()
}
