package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignInUseCaseTest : FunSpec({
    lateinit var repo: AuthenticationRepository
    lateinit var setIsConnected: SetIsConnectedUseCase
    lateinit var useCase: SignInUseCase

    beforeTest {
        MockKAnnotations.init(this)
        repo = mockk()
        setIsConnected = mockk()
        useCase = SignInUseCase(
            authenticationRepository = repo,
            setIsConnectedUseCase = setIsConnected
        )
    }

    test("repository Failure -> propagates error; setIsConnected not called") {
        runTest {
            coEvery {
                repo.signInWithEmailAndPassword(
                    "u@acme.io",
                    "bad"
                )
            } returns AppResult.Failure(AppError.Domain.Unauthorized)

            val res = useCase(Parameters.DoubleStringParam("u@acme.io", "bad"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.Unauthorized
            coVerify(exactly = 0) { setIsConnected(true) }
        }
    }

    test("repository Success then setIsConnected Failure -> propagates set error") {
        runTest {
            coEvery {
                repo.signInWithEmailAndPassword("u@acme.io", "pwd")
            } returns
                AppResult.Success("UID-1")

            coEvery {
                setIsConnected(true)
            } returns
                AppResult.Failure(AppError.Storage.WriteFailed)

            val res = useCase(Parameters.DoubleStringParam("u@acme.io", "pwd"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Storage.WriteFailed
            coVerify(exactly = 1) { setIsConnected(true) }
        }
    }

    test("repository Success and setIsConnected Success -> Success(SignInSuccess)") {
        runTest {
            coEvery {
                repo.signInWithEmailAndPassword("u@acme.io", "pwd")
            } returns AppResult.Success("UID-2")

            coEvery { setIsConnected(true) } returns AppResult.Success(Unit)

            val res = useCase(Parameters.DoubleStringParam("u@acme.io", "pwd"))

            res.shouldBeInstanceOf<AppResult.Success<SignInSuccess>>()
            res.data shouldBe SignInSuccess
            coVerify(exactly = 1) { setIsConnected(true) }
        }
    }
})
