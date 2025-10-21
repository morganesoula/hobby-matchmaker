package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordUseCaseTest : FunSpec({

    lateinit var repo: AuthenticationRepository
    lateinit var useCase: ResetPasswordUseCase

    beforeTest {
        MockKAnnotations.init(this)
        repo = mockk()
        useCase = ResetPasswordUseCase(authenticationRepository = repo)
    }

    test("blank email -> Failure(Validation('Email is required')) and repo not called") {
        runTest {
            val res = useCase(Parameters.StringParam("   "))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.Validation("Email is required")
            coVerify(exactly = 0) { repo.resetPassword(any()) }
            confirmVerified(repo)
        }
    }

    test("trims email then forwards to repository -> Success(Unit)") {
        runTest {
            coEvery { repo.resetPassword("user@acme.io") } returns AppResult.Success(Unit)

            val res = useCase(Parameters.StringParam("  user@acme.io  "))

            res.shouldBeInstanceOf<AppResult.Success<Unit>>()
            coVerify(exactly = 1) { repo.resetPassword("user@acme.io") }
        }
    }

    test("repo Failure(Unauthorized) -> maps to Success(Unit)") {
        runTest {
            coEvery { repo.resetPassword("u@acme.io") } returns AppResult.Failure(AppError.Domain.Unauthorized)

            val res = useCase(Parameters.StringParam("u@acme.io"))

            res.shouldBeInstanceOf<AppResult.Success<Unit>>()
            coVerify(exactly = 1) { repo.resetPassword("u@acme.io") }
        }
    }

    test("repo Failure(Timeout) -> propagates Failure(Timeout)") {
        runTest {
            coEvery { repo.resetPassword("u@acme.io") } returns AppResult.Failure(AppError.Network.Timeout)

            val res = useCase(Parameters.StringParam("u@acme.io"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Network.Timeout
            coVerify(exactly = 1) { repo.resetPassword("u@acme.io") }
        }
    }

    test("repo Failure(Validation) -> propagates same Failure") {
        runTest {
            val err = AppError.Domain.Validation("Invalid email")
            coEvery { repo.resetPassword("bad") } returns AppResult.Failure(err)

            val res = useCase(Parameters.StringParam("bad"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe err
            coVerify(exactly = 1) { repo.resetPassword("bad") }
        }
    }
})
