package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.FakeImageRepository
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.FakeTMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.movie
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.page
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class MovieRemoteDataSourceImplTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    test("fetchMovies agrège 3 pages et met à jour les posters quand tout va bien") {
        runTest(testDispatcher) {
            val p1 = AppResult.Success(page(movie(1, "A", "/a.jpg")))
            val p2 = AppResult.Success(
                page(
                    movie(2, "B", ""),
                    movie(3, "C", "/error.jpg")
                )
            )
            val p3 = AppResult.Success(page(movie(4, "D", "/d.jpg")))

            val tmdb = FakeTMDBKtorService(mapOf(1 to p1, 2 to p2, 3 to p3))
            val images = FakeImageRepository { path ->
                if (path == "/error.jpg") throw RuntimeException("boom")
                "local$path"
            }

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = images,
                firestore = mockk(relaxed = true),
                tmdbKtorService = tmdb
            )

            // When
            val result = sut.fetchMovies(language = "fr-FR")

            // Then
            require(result is AppResult.Success)
            val list = result.data
            list.shouldHaveSize(4)

            list.first { it.id == 1 }.poster shouldBe "local/a.jpg"
            list.first { it.id == 2 }.poster shouldBe ""               // blank conservé
            list.first { it.id == 3 }.poster shouldBe "/error.jpg"     // exception => poster inchangé
            list.first { it.id == 4 }.poster shouldBe "local/d.jpg"
        }
    }

    test("fetchMovies s’arrête et renvoie Failure dès qu’une page échoue (pas de page3)") {
        runTest(testDispatcher) {
            // Given
            val p1 = AppResult.Success(page(movie(1, "A", "/a.jpg")))
            val p2 = AppResult.Failure(
                AppError.Network.Http(code = 500, body = "boom")
            )
            val p3 = AppResult.Success(page(movie(99, "Z", "/z.jpg")))

            val tmdb = FakeTMDBKtorService(mapOf(1 to p1, 2 to p2, 3 to p3))
            val images = FakeImageRepository { "local$it" }

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = images,
                firestore = mockk(relaxed = true),
                tmdbKtorService = tmdb
            )

            val result = sut.fetchMovies("fr-FR")

            result shouldBe AppResult.Failure(AppError.Network.Http(500, "boom"))
        }
    }

    test("updateUserFavoriteMovieList ajoute l’id quand isFavorite = true (merge=true)") {
        runTest(testDispatcher) {
            val firestore = mockk<FirebaseFirestore>()
            val collection = mockk<CollectionReference>()
            val document = mockk<DocumentReference>(relaxed = true, relaxUnitFun = true)

            every { firestore.collection("users") } returns collection
            every { collection.document("uid-123") } returns document

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = FakeImageRepository { it },
                firestore = firestore,
                tmdbKtorService = FakeTMDBKtorService(emptyMap())
            )

            val result = sut.updateUserFavoriteMovieList(
                uuidUser = "uid-123",
                movieId = 42L,
                isFavorite = true
            )

            result.shouldBeInstanceOf<AppResult.Success<Unit>>()
            verify { firestore.collection("users") }
            verify { collection.document("uid-123") }
        }
    }

    test("updateUserFavoriteMovieList retire l’id quand isFavorite = false (merge=true)") {
        runTest(testDispatcher) {
            val firestore = mockk<FirebaseFirestore>()
            val collection = mockk<CollectionReference>()
            val document = mockk<DocumentReference>(relaxed = true, relaxUnitFun = true)

            every { firestore.collection("users") } returns collection
            every { collection.document("uid-123") } returns document

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = FakeImageRepository { it },
                firestore = firestore,
                tmdbKtorService = FakeTMDBKtorService(emptyMap())
            )

            val result = sut.updateUserFavoriteMovieList(
                uuidUser = "uid-123",
                movieId = 42L,
                isFavorite = false
            )

            result.shouldBeInstanceOf<AppResult.Success<Unit>>()
            verify { firestore.collection("users") }
            verify { collection.document("uid-123") }
        }
    }

    test("updateUserFavoriteMovieList propage une erreur si Firestore jette une exception") {
        runTest(testDispatcher) {
            val firestore = mockk<FirebaseFirestore>()
            val collection = mockk<CollectionReference>()

            every { firestore.collection("users") } returns collection
            every { collection.document("uid-err") } throws RuntimeException("set failed")

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = FakeImageRepository { it },
                firestore = firestore,
                tmdbKtorService = FakeTMDBKtorService(emptyMap())
            )

            val result = sut.updateUserFavoriteMovieList(
                uuidUser = "uid-err",
                movieId = 7L,
                isFavorite = true
            )

            result.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            val err = result.error
            err.shouldBeInstanceOf<AppError.Network.Unknown>()
            err.cause?.message shouldBe "set failed"
        }
    }

    test("setUserFavoriteMovies écrit la liste complète (merge=true)") {
        runTest(testDispatcher) {
            val firestore = mockk<FirebaseFirestore>()
            val collection = mockk<CollectionReference>()
            val document = mockk<DocumentReference>(relaxed = true, relaxUnitFun = true)

            every { firestore.collection("users") } returns collection
            every { collection.document("uid-xyz") } returns document

            val sut = MovieRemoteDataSourceImpl(
                imageRepository = FakeImageRepository { it },
                firestore = firestore,
                tmdbKtorService = FakeTMDBKtorService(emptyMap())
            )

            val result = sut.setUserFavoriteMovies(
                uid = "uid-xyz",
                ids = listOf(1L, 2L, 3L)
            )

            result.shouldBeInstanceOf<AppResult.Success<Unit>>()
            verify { firestore.collection("users") }
            verify { collection.document("uid-xyz") }
        }
    }
})
