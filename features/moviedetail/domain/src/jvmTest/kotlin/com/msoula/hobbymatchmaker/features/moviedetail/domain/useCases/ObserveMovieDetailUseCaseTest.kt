package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRepository
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveMovieDetailUseCaseTest : FunSpec({
    val scheduler = TestCoroutineScheduler()

    lateinit var repo: FakeMovieDetailRepository
    lateinit var useCase: ObserveMovieDetailUseCase

    beforeTest {
        repo = FakeMovieDetailRepository()
        useCase = ObserveMovieDetailUseCase(
            movieDetailRepository = repo,
            dispatcher = UnconfinedTestDispatcher(scheduler)
        )
    }

    test("detail == null -> Failure(NotFound)") {
        runTest {
            val flow = useCase(movieId = 1L, language = "fr-FR")
            repo.emitDetail(null)

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Domain.NotFound
        }
    }

    test("synopsis blank + fetch detail Failure -> error") {
        runTest {
            repo.fetchDetailResult = AppResult.Failure(AppError.Network.Timeout)
            val flow = useCase(movieId = 2L, language = "fr-FR")
            repo.emitDetail(detail(synopsis = ""))

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Network.Timeout
        }
    }

    test("synopsis blank + fetch detail Success(null) -> Failure(NotFound)") {
        runTest {
            repo.fetchDetailResult = AppResult.Success(null)
            val flow = useCase(movieId = 3L, language = "fr-FR")
            repo.emitDetail(detail(synopsis = " "))

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Domain.NotFound
        }
    }

    test(
        "synopsis blank + fetch detail Success + fetch credit Success(non-empty) + " +
            "save success -> Success(DataLoadedInDB)"
    ) {
        runTest {
            val fetched = detail(synopsis = "synopsis fetched", cast = null)
            repo.fetchDetailResult = AppResult.Success(fetched)
            val cast = listOf(MovieActorDomainModel(name = "A", role = "B"))
            repo.fetchCreditResult = AppResult.Success(cast)
            repo.saveDetailResult = AppResult.Success(Unit)

            val flow = useCase(movieId = 4L, language = "fr-FR")
            repo.emitDetail(detail(synopsis = ""))

            val res = flow.first()
            repo.lastSaved?.cast?.shouldHaveSize(1)
            repo.lastSaved?.cast?.first()?.name shouldBe "A"
            repo.lastSaved?.cast?.first()?.role shouldBe "B"

            res.shouldBeInstanceOf<AppResult.Success<ObserveMovieSuccess>>()
            res.data shouldBe ObserveMovieSuccess.DataLoadedInDB
        }
    }

    test(
        "synopsis blank + fetch detail Success + fetch credit Failure -> cast=[] " +
            "+ save success -> Success(DataLoadedInDB)"
    ) {
        runTest {
            val fetched = detail(synopsis = "synopsis fetched", cast = null)
            repo.fetchDetailResult = AppResult.Success(fetched)
            repo.fetchCreditResult = AppResult.Failure(AppError.Network.Unreachable)
            repo.saveDetailResult = AppResult.Success(Unit)

            val flow = useCase(movieId = 5L, language = "fr-FR")
            repo.emitDetail(detail(synopsis = ""))

            val res = flow.first()
            repo.lastSaved?.cast?.size shouldBe 0

            res.shouldBeInstanceOf<AppResult.Success<ObserveMovieSuccess>>()
            res.data shouldBe ObserveMovieSuccess.DataLoadedInDB
        }
    }

    test("cast empty + fetch credit Failure -> Failure") {
        runTest {
            repo.fetchCreditResult = AppResult.Failure(AppError.Network.Http(500))
            val flow = useCase(movieId = 6L, language = "fr")
            repo.emitDetail(detail(synopsis = "ok", cast = emptyList()))

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Network.Http(500)
        }
    }

    test(
        "cast empty + fetch credit Success(empty) -> cast=[NO_CAST/MARKER] " +
            "+ save success -> Success(DataLoadedInDB)"
    ) {
        runTest {
            repo.fetchCreditResult = AppResult.Success(emptyList())
            repo.saveDetailResult = AppResult.Success(Unit)

            val flow = useCase(movieId = 7L, language = "fr")
            repo.emitDetail(detail(synopsis = "ok", cast = emptyList()))

            val res = flow.first()
            val savedCast = repo.lastSaved?.cast
            savedCast?.shouldHaveSize(1)
            savedCast?.first()?.name shouldBe "NO_CAST"
            savedCast?.first()?.role shouldBe "MARKER"

            res.shouldBeInstanceOf<AppResult.Success<ObserveMovieSuccess>>()
            res.data shouldBe ObserveMovieSuccess.DataLoadedInDB
        }
    }

    test(
        "full detail (synopsis not blank, cast not empty) -> " +
            "Success(ObserveMovieSuccess.Success(detail))"
    ) {
        runTest {
            val ready = detail(
                synopsis = "ok",
                cast = listOf(MovieActorDomainModel(name = "X", role = "Y"))
            )
            val flow = useCase(movieId = 8L, language = "fr")
            repo.emitDetail(ready)

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Success<ObserveMovieSuccess>>()
            res.data shouldBe ObserveMovieSuccess.Success(ready)
        }
    }

    test("exception in fetchMovieDetail -> catch -> Failure(toStorageError)") {
        runTest {
            repo.fetchDetailThrows = IOException("disk")
            val flow = useCase(movieId = 9L, language = "fr-FR")
            repo.emitDetail(detail(synopsis = ""))

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Storage.ReadFailed
        }
    }

    test("saveMovieDetail Failure") {
        runTest {
            val fetched = detail(synopsis = "fetched", cast = null)
            repo.fetchDetailResult = AppResult.Success(fetched)
            repo.fetchCreditResult =
                AppResult.Success(listOf(MovieActorDomainModel(name = "A", role = "B")))
            repo.saveDetailResult = AppResult.Failure(AppError.Storage.WriteFailed)

            val flow = useCase(movieId = 10L, language = "en")
            repo.emitDetail(detail(synopsis = ""))

            val res = flow.first()
            res.shouldBeInstanceOf<AppResult.Failure<ObserveMovieSuccess>>()
            res.error shouldBe AppError.Storage.WriteFailed
        }
    }

})


private fun detail(
    synopsis: String?,
    cast: List<MovieActorDomainModel>? = null
) = MovieDetailDomainModel(
    id = 1L,
    title = "t",
    genre = emptyList(),
    popularity = 0.0,
    releaseDate = "2020-01-01",
    synopsis = synopsis,
    status = "ok",
    localCoverFilePath = null,
    videoKey = "",
    cast = cast,
    duration = 100
)
