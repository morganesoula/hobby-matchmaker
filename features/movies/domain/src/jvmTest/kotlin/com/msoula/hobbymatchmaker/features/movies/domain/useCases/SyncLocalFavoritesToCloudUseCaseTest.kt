package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.fakes.FakeMovieRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class SyncLocalFavoritesToCloudUseCaseTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("Auth failure") {
        test("early return -> MovieRepository not called") {
            runTest(dispatcher) {
                val movieRepository = FakeMovieRepository()

                val authenticationRepository = mockk<AuthenticationRepository>()

                coEvery { authenticationRepository.fetchFirebaseUserInfo() } returns
                    AppResult.Failure(AppError.Authentication.Unknown)

                val result = SyncLocalFavoritesToCloudUseCase(
                    movieRepository,
                    authenticationRepository,
                    dispatcher
                )

                result()

                movieRepository.getFavoriteLocalMovieIdsCall shouldBe 0
                movieRepository.syncUserFavoritesRemoteCalls shouldBe 0
            }
        }

        test("Signed out -> early return -> MovieRepository not called") {
            runTest(dispatcher) {
                val movieRepository = FakeMovieRepository().apply {
                    favoriteIdsResult = AppResult.Failure(AppError.Storage.ReadFailed)
                }

                val authenticationRepository = mockk<AuthenticationRepository>()

                coEvery { authenticationRepository.fetchFirebaseUserInfo() } returns
                    AppResult.Success(
                        AuthState.Authenticated(
                            FirebaseUserInfoDomainModel(
                                uid = "u1", email = null, providers = null
                            )
                        )
                    )

                val result = SyncLocalFavoritesToCloudUseCase(
                    movieRepository,
                    authenticationRepository,
                    dispatcher
                )

                result()

                movieRepository.getFavoriteLocalMovieIdsCall shouldBe 1
                movieRepository.syncUserFavoritesRemoteCalls shouldBe 0
            }
        }
    }

    context("Auth Success") {
        test("favorites Success not empty -> push remote with uid & ids") {
            runTest(dispatcher) {
                val movieRepo = FakeMovieRepository().apply {
                    favoriteIdsResult = AppResult.Success(listOf(1L, 2L, 3L))
                }

                val authRepo = mockk<AuthenticationRepository>()

                coEvery { authRepo.fetchFirebaseUserInfo() } returns AppResult.Success(
                    AuthState.Authenticated(
                        FirebaseUserInfoDomainModel(
                            uid = "u42",
                            email = null,
                            providers = null
                        )
                    )
                )

                val result = SyncLocalFavoritesToCloudUseCase(
                    movieRepo,
                    authRepo,
                    dispatcher
                )

                result()

                movieRepo.getFavoriteLocalMovieIdsCall shouldBe 1
                movieRepo.syncUserFavoritesRemoteCalls shouldBe 1
                movieRepo.lastSyncUid shouldBe "u42"
                movieRepo.lastSyncIds!!.shouldContainExactly(1L, 2L, 3L)
            }
        }

        test("favorites Success empty -> push remote with empty list") {
            runTest(dispatcher) {
                val movieRepo = FakeMovieRepository().apply {
                    favoriteIdsResult = AppResult.Success(emptyList())
                }
                val authRepo = mockk<AuthenticationRepository>()
                coEvery { authRepo.fetchFirebaseUserInfo() } returns AppResult.Success(
                    AuthState.Authenticated(
                        FirebaseUserInfoDomainModel(
                            uid = "u1",
                            email = null,
                            providers = null
                        )
                    )
                )

                val result = SyncLocalFavoritesToCloudUseCase(
                    movieRepo,
                    authRepo,
                    dispatcher
                )

                result()

                movieRepo.getFavoriteLocalMovieIdsCall shouldBe 1
                movieRepo.syncUserFavoritesRemoteCalls shouldBe 1
                movieRepo.lastSyncUid shouldBe "u1"
                movieRepo.lastSyncIds shouldBe emptyList()
            }
        }

        test("syncUserFavoritesRemote fails -> no exception, only log") {
            runTest(dispatcher) {
                val movieRepo = FakeMovieRepository().apply {
                    favoriteIdsResult = AppResult.Success(listOf(7L))
                    syncRemoteResult = AppResult.Failure(AppError.Network.Http(503, "unavailable"))
                }
                val authRepo = mockk<AuthenticationRepository>()
                coEvery { authRepo.fetchFirebaseUserInfo() } returns AppResult.Success(
                    AuthState.Authenticated(
                        FirebaseUserInfoDomainModel(
                            uid = "u1",
                            email = null,
                            providers = null
                        )
                    )
                )

                val result = SyncLocalFavoritesToCloudUseCase(
                    movieRepo,
                    authRepo,
                    dispatcher
                )

                result()

                movieRepo.getFavoriteLocalMovieIdsCall shouldBe 1
                movieRepo.syncUserFavoritesRemoteCalls shouldBe 1
                movieRepo.lastSyncUid shouldBe "u1"
                movieRepo.lastSyncIds shouldBe listOf(7L)
            }
        }
    }
})
