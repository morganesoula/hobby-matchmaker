package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.common.Result
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
                Result.Failure(LogOutError.UnknownError("Test error"))

            val result = repository.logOut()

            result shouldBe Result.Failure(LogOutError.UnknownError("Test error"))
        }
    }

    context("signUp") {

        test("should emit Success when email and password are valid") {
            val result = repository.signUp("valid@example.com", "password123")

            result shouldBe Result.Success("fakeUid")
        }

        test("should emit Failure when email and password are empty") {
            val result = repository.signUp("", "")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordError.UserDisabled)
        }

        test("should emit Failure when email is empty") {
            val result = repository.signUp("", "pass")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordError.TooManyRequests)
        }

        test("should emit Failure when password is empty") {
            val result = repository.signUp("mail@test.com", "")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordError.InternalError)
        }

        test("should emit Failure when email equals password") {
            val result = repository.signUp("same", "same")

            result shouldBe Result.Failure(CreateUserWithEmailAndPasswordError.EmailAlreadyExists)
        }

        test("should emit Other error when email is 'unknown error'") {
            val result = repository.signUp("unknown error", "whatever")

            result shouldBe Result.Failure(
                CreateUserWithEmailAndPasswordError.Other("Weird error message")
            )
        }
    }
})
