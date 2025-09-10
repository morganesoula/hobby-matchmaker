package com.msoula.hobbymatchmaker.features.movies.domain.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import io.kotest.mpp.replay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.jvm.Throws

class FakeMovieRepository() : MovieRepository {
    private val _flow =
        MutableSharedFlow<List<MovieDomainModel>>(replay = 1, extraBufferCapacity = 16)
    var favoriteIdsResult: AppResult<List<Long>, AppError> = AppResult.Success(emptyList())
    var syncRemoteResult: AppResult<Unit, AppError> = AppResult.Success(Unit)

    var getFavoriteLocalMovieIdsCall = 0
    var syncUserFavoritesRemoteCalls = 0
    var lastSyncUid: String? = null
    var lastSyncIds: List<Long>? = null

    override fun observeMovies(): Flow<List<MovieDomainModel>> = _flow.asSharedFlow()

    suspend fun emit(list: List<MovieDomainModel>) = _flow.emit(list)
    fun tryEmit(list: List<MovieDomainModel>) = _flow.tryEmit(list)

    override suspend fun updateMovieFavoriteLocal(
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = error("Not used in this test")

    override suspend fun updateMovieFavoriteRemote(
        uid: String,
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = error("Not used in this test")

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError> = error("Not used in this test")

    override suspend fun fetchMovies(language: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun isSynopsisMovieAvailable(movieId: Long): AppResult<Boolean, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError> {
        getFavoriteLocalMovieIdsCall++
        return favoriteIdsResult
    }

    override suspend fun syncUserFavoritesRemote(
        uid: String,
        localIds: List<Long>
    ): AppResult<Unit, AppError> {
        syncUserFavoritesRemoteCalls++
        lastSyncUid = uid
        lastSyncIds = localIds
        return syncRemoteResult
    }
}
