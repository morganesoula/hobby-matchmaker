package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SyncLocalFavoritesToCloudUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val movieRepository: MovieRepository,
    private val authenticationRepository: AuthenticationRepository
) : FlowUseCase<Parameters.None, Unit, SyncLocalErrors>(dispatcher) {

    override fun execute(parameters: Parameters.None): Flow<Result<Unit, SyncLocalErrors>> =
        channelFlow {
            send(Result.Loading)

            val uid = authenticationRepository.fetchFirebaseUserInfo()?.uid ?: return@channelFlow
            send(Result.Failure(SyncLocalErrors.NoUIDFoundError))

            val localIds = movieRepository.getFavoriteLocalMovieIds()

            try {
                movieRepository.syncUserFavoritesRemote(uid, localIds)
                send(Result.Success(Unit))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.e("Sync local movie favorites failed: ${e.message}")
                send(Result.Failure(SyncLocalErrors.SyncLocalFavoriteError))
            }
        }.flowOn(dispatcher)
}

sealed class SyncLocalErrors(override val message: String) : AppError {
    data object NoUIDFoundError : SyncLocalErrors("")
    data object SyncLocalFavoriteError : SyncLocalErrors("")
}
