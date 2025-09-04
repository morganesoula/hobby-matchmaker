package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRemoteDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AuthenticationRepositoryTest: FunSpec({

    val fakeRemoteDataSource = FakeAuthenticationRemoteDataSource()
    val repository = AuthenticationRepositoryImpl(fakeRemoteDataSource)

    context("logOut") {

        test("should emit Success when remoteDataSource returns Success") {
            fakeRemoteDataSource.authenticationSignOutResult = Result.Success(true)

            val result = repository.logOut()

            result shouldBe Result.Success(true)
        }

        test("should emit Failure when remoteDataSource returns Failure") {
            fakeRemoteDataSource.authenticationSignOutResult =
                Result.Failure(LogOutErrorHMM.UnknownErrorHMM("Test error"))

            val result = repository.logOut()

            result shouldBe Result.Failure(LogOutErrorHMM.UnknownErrorHMM("Test error"))
        }
    }

    context("signUp") {

        test("should emit Success when email and password are valid") {
            val result = repository.signUp("valid@example.com", "password123")

            result shouldBe Result.Success("fakeUid")
        }

        test("should emit Failure when email and password are empty") {
            val result = repository.signUp("", "")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.UserDisabled)
        }

        test("should emit Failure when email is empty") {
            val result = repository.signUp("", "pass")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.TooManyRequests)
        }

        test("should emit Failure when password is empty") {
            val result = repository.signUp("mail@test.com", "")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.InternalErrorHMM)
        }

        test("should emit Failure when email equals password") {
            val result = repository.signUp("same", "same")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists)
        }

        test("should emit Other error when email is 'unknown error'") {
            val result = repository.signUp("unknown error", "whatever")

            result shouldBe Result.Failure(
                CreateUserWithEmailAndPasswordErrorHMM.Other("Weird error message")
            )
        }
    }
})
