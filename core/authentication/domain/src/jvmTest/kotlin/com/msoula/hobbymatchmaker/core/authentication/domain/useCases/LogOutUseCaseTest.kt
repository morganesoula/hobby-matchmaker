package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class LogOutUseCaseTest : FunSpec({
    lateinit var repo: AuthenticationRepository
    lateinit var setIsConnected: SetIsConnectedUseCase
    lateinit var observeIsConnected: ObserveIsConnectedUseCase
    lateinit var useCase: LogOutUseCase

    beforeTest {
        MockKAnnotations.init(this)
        repo = mockk()
        setIsConnected = mockk()
        observeIsConnected = mockk()
        useCase = LogOutUseCase(
            authenticationRepository = repo,
            setIsConnectedUseCase = setIsConnected,
            observeIsConnectedUseCase = observeIsConnected
        )
    }

    test("logout Failure -> propagates error and does not call setIsConnected") {
        runTest {
            coEvery { repo.logOut() } returns AppResult.Failure(AppError.Network.Timeout)

            val res = useCase()

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Network.Timeout
            coVerify(exactly = 1) { repo.logOut() }
            coVerify(exactly = 0) { setIsConnected.invoke(any()) }
            verify(exactly = 0) { observeIsConnected() }
        }
    }

    test("logout Success then setIsConnected Failure -> propagates set failure") {
        runTest {
            coEvery { repo.logOut() } returns AppResult.Success(Unit)
            coEvery { setIsConnected(false) } returns AppResult.Failure(AppError.Storage.WriteFailed)

            val res = useCase()

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Storage.WriteFailed
            coVerify(exactly = 1) { repo.logOut() }
            coVerify(exactly = 1) { setIsConnected(false) }
            verify(exactly = 0) { observeIsConnected() }
        }
    }

    test("logout Success, setIsConnected Success, observe emits false immediately -> Success(LogOutSuccess)") {
        runTest {
            coEvery { repo.logOut() } returns AppResult.Success(Unit)
            coEvery { setIsConnected(false) } returns AppResult.Success(Unit)
            every { observeIsConnected() } returns flowOf(false)

            val res = useCase()

            res.shouldBeInstanceOf<AppResult.Success<LogOutSuccess>>()
            res.data shouldBe LogOutSuccess
            coVerify(exactly = 1) { repo.logOut() }
            coVerify(exactly = 1) { setIsConnected(false) }
            verify(exactly = 1) { observeIsConnected() }
        }
    }

    test("logout Success, setIsConnected Success, observe emits true then false -> Success when becomes disconnected") {
        runTest {
            coEvery { repo.logOut() } returns AppResult.Success(Unit)
            coEvery { setIsConnected(false) } returns AppResult.Success(Unit)
            every { observeIsConnected() } returns flow {
                emit(true)   // encore connecté
                emit(false)  // devient déconnecté -> la condition first { !connected } passe ici
            }

            val res = useCase()

            res.shouldBeInstanceOf<AppResult.Success<LogOutSuccess>>()
            res.data shouldBe LogOutSuccess
            coVerify(exactly = 1) { repo.logOut() }
            coVerify(exactly = 1) { setIsConnected(false) }
            verify(exactly = 1) { observeIsConnected() }
        }
    }
})
