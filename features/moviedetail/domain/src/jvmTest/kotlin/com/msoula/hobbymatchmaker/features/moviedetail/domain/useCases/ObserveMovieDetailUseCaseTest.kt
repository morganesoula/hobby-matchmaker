package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRepository
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class ObserveMovieDetailUseCaseTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val dummyMovieDetail = MovieDetailDomainModel(
        1,
        "Dummy movie detail",
        emptyList(),
        null,
        "",
        "Dummy movie detail synopsis",
        null,
        null,
        null,
        listOf(
            MovieActorDomainModel(
                id = 1,
                name = "Actor 1",
                role = "Role 1"
            ), MovieActorDomainModel(
                id = 2,
                name = "Actor 2",
                role = "Role 2"
            )
        )
    )

    context("ObserveMovieDetail - success") {
        test("Should return Success when movie detail is fully available") {
            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(dummyMovieDetail)
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase.execute(Parameters.LongStringParam(1L, "en")).take(2).toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Success(ObserveMovieSuccess.Success(dummyMovieDetail))
                )
            }
        }

        test("Should return Failure when movie detail is null") {
            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(null)
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase.execute(Parameters.LongStringParam(1L, "en")).take(2).toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(ObserveMovieErrors.Empty)
                )
            }
        }
    }

    context("ObserveMovieDetail - Failure") {
        test("Fetches and saves movie detail + cast when synopsis is blank") {
            val observedMovie = dummyMovieDetail.copy(synopsis = null, cast = emptyList())

            val fetchedDetail =
                dummyMovieDetail.copy(synopsis = "Fetched synopsis", cast = emptyList())
            val fetchedCast = listOf(
                MovieActorDomainModel(
                    id = 3,
                    name = "Actor 3",
                    role = "Role 3"
                )
            )
            val expectedSavedMovie = fetchedDetail.copy(cast = fetchedCast)

            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(observedMovie),
                fetchDetailResult = Result.Success(fetchedDetail),
                fetchCreditResult = Result.Success(fetchedCast)
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Success(ObserveMovieSuccess.DataLoadedInDB)
                )

                fakeMovieDetailRepository.savedMovie shouldBe expectedSavedMovie
            }
        }

        test("return MovieDetailError when fetchMovieDetail fails") {
            val dummyEmptyMovie = MovieDetailDomainModel(
                id = 1L,
                title = "Some title",
                genre = emptyList(),
            )

            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(dummyEmptyMovie),
                fetchDetailResult = Result.Failure(
                    MovieDetailDomainError.MovieDetailError("Something went wrong")
                )
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(ObserveMovieErrors.MovieDetailError)
                )
            }
        }

        test("return CreditError when fetchMovieCredit fails") {
            val dummyMovieWithoutCast = MovieDetailDomainModel(
                id = 1L,
                title = "Some title",
                genre = emptyList(),
                synopsis = "This movie has no cast",
                cast = emptyList()
            )

            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(dummyMovieWithoutCast),
                fetchCreditResult = Result.Failure(
                    MovieDetailDomainError.CreditError("No cast available")
                )
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(ObserveMovieErrors.CreditError)
                )
            }
        }

        test("returns Empty when observeMovieDetail emits null") {
            val fakeMovieDetailRepository = FakeMovieDetailRepository(
                movieDetailFlow = flowOf(null)
            )

            val useCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                dispatcher = dispatcher
            )

            runTest(dispatcher) {
                val result = useCase
                    .execute(Parameters.LongStringParam(1L, "en"))
                    .take(2)
                    .toList()

                result shouldBe listOf(
                    Result.Loading,
                    Result.Failure(ObserveMovieErrors.Empty)
                )
            }
        }
    }
})
