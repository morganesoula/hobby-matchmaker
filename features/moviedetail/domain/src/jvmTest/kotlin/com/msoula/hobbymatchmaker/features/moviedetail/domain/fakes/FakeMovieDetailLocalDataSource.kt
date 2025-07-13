package com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes

import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeMovieDetailLocalDataSource : MovieDetailLocalDataSource {

    private val movieDetailFlow =
        MutableStateFlow<MovieDetailDomainModel?>(null)

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> =
        movieDetailFlow

    override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String) {
        movieDetailFlow.update {
            it?.copy(videoKey = videoKey)
        }
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {
        movieDetailFlow.value = movieDetail
    }
}
