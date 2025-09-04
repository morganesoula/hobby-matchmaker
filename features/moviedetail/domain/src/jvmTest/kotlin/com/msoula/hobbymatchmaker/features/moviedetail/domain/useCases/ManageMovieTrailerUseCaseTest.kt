package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRepository
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class ManageMovieTrailerUseCaseTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("ManageMovieTrailer - Success") {
        test("returns Success when trailer is fetched and update succeeds") {
            val fakeTrailer = MovieVideoDomainModel(
                key = "abc123",
                type = "",
                site = "YouTube"
            )

            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                fetchTrailerResult = Result.Success(fakeTrailer),
                updateTrailerResult = Result.Success(true)
            )

            val updateMovieVideoURIUSeCase = UpdateMovieVideoURIUseCase(fakeMovieDetailRepository)

            val useCase = ManageMovieTrailerUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                updateMovieVideoURIUseCase = updateMovieVideoURIUSeCase,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Success(MovieTrailerReady("abc123"))
                )

                fakeMovieDetailRepository.updatedVideoURI shouldBe "abc123"
            }
        }
    }

    context("ManageMovieTrailer - Failure") {
        test("Returns NoConnectionError when no connection") {
            val fakeRepository = FakeMovieDetailRepository(
                fetchTrailerResult = Result.Failure(
                    MovieDetailDomainErrorHMM.NoConnection("No internet connexion")
                )
            )

            val useCase = ManageMovieTrailerUseCase(
                movieDetailRepository = fakeRepository,
                updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeRepository),
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(FetchingTrailerErrorHMM.NoConnectionErrorHMM("No internet connexion"))
                )
            }
        }

        test("Returns no TrailerFound when no trailer is found") {
            val fakeRepository = FakeMovieDetailRepository(
                fetchTrailerResult = Result.Failure(
                    FetchingTrailerErrorHMM.NoTrailerFoundErrorHMM(
                        "No trailer found"
                    )
                )
            )

            val useCase = ManageMovieTrailerUseCase(
                movieDetailRepository = fakeRepository,
                updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeRepository),
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(FetchingTrailerErrorHMM.NoTrailerFoundErrorHMM("No trailer found"))
                )
            }
        }

        test("Returns TrailerUpdateError when update fails") {
            val fakeRepository = FakeMovieDetailRepository(
                updateTrailerResult = Result.Failure(
                    UpdateMovieTrailerLocalErrorHMM("error updating trailer")
                )
            )

            val useCase = ManageMovieTrailerUseCase(
                movieDetailRepository = fakeRepository,
                updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeRepository),
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(
                        FetchingTrailerErrorHMM.TrailerUpdateErrorHMM("Empty uri")
                    )
                )
            }
        }
    }
})
