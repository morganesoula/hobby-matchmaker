package com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors

import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase

class MovieDetailInteractor(
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    private val connectivityChecker: NetworkConnectivityChecker
) {
    fun observeMovieDetail(movieId: Long, language: String) =
        observeMovieDetailUseCase(movieId, language)

    suspend fun canPlayTrailerDirectly(
        isVideoUriKnown: Boolean
    ): Boolean = isVideoUriKnown && connectivityChecker.hasActiveConnection()

    suspend fun fetchTrailer(
        movieId: Long,
        language: String
    ) = manageMovieTrailerUseCase(movieId, language).mapSuccess { it.videoURI }

}
