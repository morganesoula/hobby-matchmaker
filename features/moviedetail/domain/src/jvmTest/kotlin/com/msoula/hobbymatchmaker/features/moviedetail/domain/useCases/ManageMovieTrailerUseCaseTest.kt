package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRepository
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

class ManageMovieTrailerUseCaseTest : FunSpec({
    lateinit var fakeRepo: FakeMovieDetailRepository
    lateinit var updateMock: UpdateMovieVideoURIUseCase
    lateinit var useCase: ManageMovieTrailerUseCase

    beforeTest {
        MockKAnnotations.init(this)
        fakeRepo = FakeMovieDetailRepository()
        updateMock = mockk(relaxed = true) // on mock le UseCase
        useCase = ManageMovieTrailerUseCase(
            movieDetailRepository = fakeRepo,
            updateMovieVideoURIUseCase = updateMock
        )
    }

    test("propagates Failure when fetchMovieTrailer fails") {
        runTest {
            val expectedErr: AppError = AppError.Network.Timeout
            fakeRepo.fetchTrailerResult = AppResult.Failure(expectedErr)

            val res = useCase(movieId = 10L, language = "fr-FR")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe expectedErr
        }
    }


    test("returns NotFound when trailer data is null") {
        runTest {
            fakeRepo.fetchTrailerResult = AppResult.Success(null)

            val res = useCase(movieId = 10L, language = "fr-FR")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.NotFound
        }
    }

    test("YouTube (case-insensitive) → uri == key; update success → Success(MovieTrailerReady)") {
        runTest {
            val video = stubVideo(site = "youTUbe", key = "abc123")
            fakeRepo.fetchTrailerResult = AppResult.Success(video)

            val movieId = 42L
            val uriSlot = slot<String>()
            val idSlot = slot<Long>()
            coEvery {
                updateMock(
                    capture(idSlot),
                    capture(uriSlot)
                )
            } returns AppResult.Success(Unit)

            val res = useCase(movieId = movieId, language = "en-US")

            coVerify(exactly = 1) { updateMock.invoke(any(), any()) }
            idSlot.captured shouldBe movieId
            uriSlot.captured shouldBe "abc123"

            res.shouldBeInstanceOf<AppResult.Success<MovieTrailerReady>>()
            res.data.videoURI shouldBe "abc123"
        }
    }

    test("non-YouTube → uri == https://vimeo.com/{key}; update success → Success") {
        runTest {
            val video = stubVideo(site = "Vimeo", key = "k9")
            fakeRepo.fetchTrailerResult = AppResult.Success(video)

            val movieId = 7L
            val uriSlot = slot<String>()
            val idSlot = slot<Long>()
            coEvery {
                updateMock.invoke(
                    capture(idSlot),
                    capture(uriSlot)
                )
            } returns AppResult.Success(Unit)

            val res = useCase(movieId = movieId, language = "fr")

            coVerify(exactly = 1) { updateMock.invoke(any(), any()) }
            idSlot.captured shouldBe movieId
            uriSlot.captured shouldBe "https://vimeo.com/k9"

            res.shouldBeInstanceOf<AppResult.Success<MovieTrailerReady>>()
            res.data.videoURI shouldBe "https://vimeo.com/k9"
        }
    }


    test("YouTube with empty key → NotFound") {
        runTest {
            val video = stubVideo(site = "YouTube", key = "")
            fakeRepo.fetchTrailerResult = AppResult.Success(video)

            val res = useCase(movieId = 99L, language = "fr-FR")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.NotFound
        }
    }

    test("updateMovieVideoURIUseCase failure is propagated") {
        runTest {
            val video = stubVideo(site = "YouTube", key = "zyx")
            fakeRepo.fetchTrailerResult = AppResult.Success(video)

            val writeErr: AppError = AppError.Storage.WriteFailed
            coEvery { updateMock.invoke(any(), any()) } returns AppResult.Failure(writeErr)

            val res = useCase(movieId = 12L, language = "en")

            coVerify(exactly = 1) { updateMock.invoke(12L, "zyx") }
            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe writeErr
        }
    }
})

private fun stubVideo(site: String, key: String): MovieVideoDomainModel {
    val video = mockk<MovieVideoDomainModel>(relaxed = true)
    every { video.site } returns site
    every { video.key } returns key
    return video
}
