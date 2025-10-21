package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes.FakeMovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes.FakeMovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class MovieDetailRemoteDataSourceImplTest : FunSpec({
    lateinit var fakeDetailService: FakeMovieDetailKtorService
    lateinit var fakeVideosService: FakeMovieVideosKtorService
    lateinit var dataSource: MovieDetailRemoteDataSourceImpl

    beforeTest {
        MockKAnnotations.init(this)
        fakeDetailService = FakeMovieDetailKtorService()
        fakeVideosService = FakeMovieVideosKtorService()
        dataSource = MovieDetailRemoteDataSourceImpl(
            movieDetailKtorService = fakeDetailService,
            movieVideosKtorService = fakeVideosService
        )
    }


    test("fetchMovieDetail forwards params and returns Success") {
        runTest {
            val movieId = 42L
            val lang = "fr-FR"
            val expected: MovieDetailResponseRemoteModel = mockk(relaxed = true)

            fakeDetailService.resultDetail = AppResult.Success(expected)

            val res = dataSource.fetchMovieDetail(movieId, lang)

            fakeDetailService.lastDetailMovieId shouldBe movieId
            fakeDetailService.lastDetailLanguage shouldBe lang

            res.shouldBeInstanceOf<AppResult.Success<MovieDetailResponseRemoteModel>>()
            res.data shouldBe expected
        }
    }

    test("fetchMovieDetail propagates Failure") {
        runTest {
            val err = AppError.Network.Unknown()
            fakeDetailService.resultDetail = AppResult.Failure(err)

            val res = dataSource.fetchMovieDetail(1L, "en-US")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe err
        }
    }

    test("fetchMovieCredit forwards params and returns Success") {
        runTest {
            val movieId = 7L
            val lang = "en-US"
            val expected: CastResponseRemoteModel = mockk(relaxed = true)

            fakeDetailService.resultCredits = AppResult.Success(expected)

            val res = dataSource.fetchMovieCredit(movieId, lang)

            fakeDetailService.lastCreditsMovieId shouldBe movieId
            fakeDetailService.lastCreditsLanguage shouldBe lang

            res.shouldBeInstanceOf<AppResult.Success<CastResponseRemoteModel?>>()
            res.data shouldBe expected
        }
    }

    test("fetchMovieCredit propagates Failure") {
        runTest {
            val err = AppError.Network.Http(code = 404, "Not Found")
            fakeDetailService.resultCredits = AppResult.Failure(err)

            val res = dataSource.fetchMovieCredit(99L, "fr-FR")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe err
        }
    }

    test("fetchMovieTrailer forwards params and returns Success") {
        runTest {
            val movieId = 13L
            val lang = "fr"
            val expected: MovieVideosResponseRemoteModel = mockk(relaxed = true)

            fakeVideosService.resultVideos = AppResult.Success(expected)

            val res = dataSource.fetchMovieTrailer(movieId, lang)

            fakeVideosService.lastMovieId shouldBe movieId
            fakeVideosService.lastLanguage shouldBe lang

            res.shouldBeInstanceOf<AppResult.Success<MovieVideosResponseRemoteModel?>>()
            res.data shouldBe expected
        }
    }


    test("fetchMovieTrailer propagates Failure") {
        runTest {
            val err = AppError.Network.Unknown()
            fakeVideosService.resultVideos = AppResult.Failure(err)

            val res = dataSource.fetchMovieTrailer(13L, "fr")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe err
        }
    }
})
