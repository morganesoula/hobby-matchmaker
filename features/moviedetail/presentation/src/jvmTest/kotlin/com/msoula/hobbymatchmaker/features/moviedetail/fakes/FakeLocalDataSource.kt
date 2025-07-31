package com.msoula.hobbymatchmaker.features.moviedetail.fakes

import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeLocalDataSource : MovieDetailLocalDataSource {
    private val movie = MutableStateFlow(MovieDetailDomainModel())

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> = movie

    override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String) {
        movie.update { it.copy(videoKey = videoKey) }
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {
        movie.update {
            it.copy(
                id = movieDetail.id,
                title = movieDetail.title,
                genre = movieDetail.genre,
                popularity = movieDetail.popularity,
                releaseDate = movieDetail.releaseDate,
                synopsis = movieDetail.synopsis,
                status = movieDetail.status,
                localCoverFilePath = movieDetail.localCoverFilePath,
                videoKey = movieDetail.videoKey,
                cast = movieDetail.cast
            )
        }
    }
}
