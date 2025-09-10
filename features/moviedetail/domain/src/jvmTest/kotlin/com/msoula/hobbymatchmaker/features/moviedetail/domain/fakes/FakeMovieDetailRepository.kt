package com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeMovieDetailRepository : MovieDetailRepository {

    var fetchDetailResult: AppResult<MovieDetailDomainModel?, AppError> =
        AppResult.Failure(AppError.Network.Unknown())
    var fetchCreditResult: AppResult<List<MovieActorDomainModel>?, AppError> =
        AppResult.Failure(AppError.Network.Unknown())
    var saveDetailResult: AppResult<Unit, AppError> =
        AppResult.Failure(AppError.Storage.WriteFailed)
    var fetchTrailerResult: AppResult<MovieVideoDomainModel?, AppError> =
        AppResult.Failure(AppError.Network.Unknown())

    var fetchDetailThrows: Throwable? = null
    var fetchCreditThrows: Throwable? = null
    var saveThrows: Throwable? = null
    var lastFetchTrailerMovieId: Long? = null
    var lastFetchTrailerLanguage: String? = null

    private val _detailFlow =
        MutableSharedFlow<MovieDetailDomainModel?>(replay = 1, extraBufferCapacity = 16)

    var lastFetchDetailMovieId: Long? = null
    var lastFetchDetailLanguage: String? = null
    var lastFetchCreditMovieId: Long? = null
    var lastFetchCreditLanguage: String? = null
    var lastSaved: MovieDetailDomainModel? = null

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): AppResult<MovieDetailDomainModel?, AppError> {
        if (fetchDetailThrows != null) throw fetchDetailThrows as Throwable
        lastFetchCreditMovieId = movieId
        lastFetchDetailLanguage = language
        return fetchDetailResult
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): AppResult<List<MovieActorDomainModel>?, AppError> {
        if (fetchCreditThrows != null) throw fetchCreditThrows as Throwable
        lastFetchCreditMovieId = movieId
        lastFetchCreditLanguage = language
        return fetchCreditResult
    }

    override fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> = _detailFlow

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel)
        : AppResult<Unit, AppError> {
        if (saveThrows != null) throw saveThrows as Throwable
        lastSaved = movieDetailDomainModel
        return saveDetailResult
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): AppResult<Unit, AppError> = AppResult.Success(Unit)

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): AppResult<MovieVideoDomainModel?, AppError> {
        lastFetchTrailerMovieId = movieId
        lastFetchTrailerLanguage = language
        return fetchTrailerResult
    }

    suspend fun emitDetail(detail: MovieDetailDomainModel?) = _detailFlow.emit(detail)
}
