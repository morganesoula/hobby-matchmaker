package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
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
class SignUpUseCaseTest : FunSpec({
    lateinit var repo: AuthenticationRepository
    lateinit var createUser: CreateUserUseCase
    lateinit var useCase: SignUpUseCase

    beforeTest {
        MockKAnnotations.init(this)
        repo = mockk()
        createUser = mockk()
        useCase = SignUpUseCase(
            authenticationRepository = repo,
            createUserUseCase = createUser
        )
    }

    test("repository signUp Failure -> propagates error; createUser not called") {
        runTest {
            coEvery { repo.signUp("user@acme.io", "pwd") } returns
                AppResult.Failure(AppError.Domain.Validation("Weak password"))

            val res = useCase(Parameters.DoubleStringParam("user@acme.io", "pwd"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.Validation("Weak password")
            coVerify(exactly = 0) { createUser.invoke(any()) }
        }
    }

    test("repository Success then createUser Failure -> propagates createUser error") {
        runTest {
            coEvery { repo.signUp("user@acme.io", "pwd") } returns
                AppResult.Success("U1")
            coEvery {
                createUser.invoke(
                    SessionUserDomainModel(
                        uid = "U1",
                        email = "user@acme.io"
                    )
                )
            } returns
                AppResult.Failure(AppError.Storage.WriteFailed)

            val res = useCase(Parameters.DoubleStringParam("user@acme.io", "pwd"))

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Storage.WriteFailed
            coVerify(exactly = 1) {
                createUser.invoke(SessionUserDomainModel(uid = "U1", email = "user@acme.io"))
            }
        }
    }

    test("repository Success and createUser Success -> Success(SignUpSuccess)") {
        runTest {
            coEvery { repo.signUp("user@acme.io", "pwd") } returns
                AppResult.Success("U2")
            coEvery {
                createUser.invoke(
                    SessionUserDomainModel(
                        uid = "U2",
                        email = "user@acme.io"
                    )
                )
            } returns
                AppResult.Success(Unit)

            val res = useCase(Parameters.DoubleStringParam("user@acme.io", "pwd"))

            res.shouldBeInstanceOf<AppResult.Success<SignUpSuccess>>()
            res.data shouldBe SignUpSuccess
            coVerify(exactly = 1) {
                createUser.invoke(SessionUserDomainModel(uid = "U2", email = "user@acme.io"))
            }
        }
    }

})
