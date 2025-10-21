package com.msoula.hobbymatchmaker.tests.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel

class FakeMovieDetailRemoteDataSource(
    private var detailFixture: MovieDetailResponseRemoteModel?,
    private var castFixture: CastResponseRemoteModel?,
    private val videosByLang: MutableMap<String, MovieVideosResponseRemoteModel?>
): MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): AppResult<MovieDetailResponseRemoteModel?, AppError> =
        AppResult.Success(detailFixture)

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): AppResult<CastResponseRemoteModel?, AppError> =
        AppResult.Success(castFixture)

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): AppResult<MovieVideosResponseRemoteModel?, AppError> {
        val key = language.lowercase()
        val payload =
            videosByLang[key]
                ?: videosByLang[key.replace('_', '-')]
                ?: videosByLang[key.substringBefore('-')]
                ?: videosByLang[key.substringBefore('_')]
                ?: videosByLang["en"]

        return AppResult.Success(payload)
    }

    fun setVideosFor(lang: String, payload: MovieVideosResponseRemoteModel?) {
        videosByLang[lang] = payload
    }

    fun clearVideos(lang: String) {
        videosByLang[lang] = null
    }
}
