package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.data.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class AuthenticationRepositoryImplTest : FunSpec({
    lateinit var remote: AuthenticationRemoteDataSource
    lateinit var repo: AuthenticationRepository

    lateinit var credential: AuthCredential

    beforeTest {
        MockKAnnotations.init(this)
        remote = mockk(relaxed = true)
        repo = AuthenticationRepositoryImpl(remoteDataSource = remote)
        credential = mockk(relaxed = true)
    }

    context("logOut") {
        test("logOut forwards to remote and returns Success(Unit)") {
            runTest {
                coEvery { remote.authenticationSignOut() } returns AppResult.Success(Unit)

                val res = repo.logOut()

                res.shouldBeInstanceOf<AppResult.Success<Unit>>()
            }
        }

        test("logOut forwards Failure from remote") {
            runTest {
                coEvery { remote.authenticationSignOut() } returns AppResult.Failure(AppError.Network.Timeout)

                val res = repo.logOut()

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Timeout
            }
        }
    }

    context("signUp") {

        test("signUp forwards to remote and returns Success(uid)") {
            runTest {
                coEvery { remote.createUserWithEmailAndPassword("a@b.com", "pwd") } returns
                    AppResult.Success("UID-1")

                val res = repo.signUp("a@b.com", "pwd")

                res.shouldBeInstanceOf<AppResult.Success<String>>()
                res.data shouldBe "UID-1"
            }
        }

        test("signUp forwards Failure from remote") {
            runTest {
                coEvery { remote.createUserWithEmailAndPassword(any(), any()) } returns
                    AppResult.Failure(AppError.Domain.Validation("Weak password"))

                val res = repo.signUp("x@y.z", "pw")

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Validation("Weak password")
            }
        }
    }

    context("signInWithEmailAndPassword") {

        test("signInWithEmailAndPassword forwards to remote and returns Success(uid)") {
            runTest {
                coEvery { remote.signInWithEmailAndPassword("a@b.com", "pwd") } returns
                    AppResult.Success("UID-2")

                val res = repo.signInWithEmailAndPassword("a@b.com", "pwd")

                res.shouldBeInstanceOf<AppResult.Success<String>>()
                res.data shouldBe "UID-2"
            }
        }

        test("signInWithEmailAndPassword forwards Failure from remote") {
            runTest {
                coEvery { remote.signInWithEmailAndPassword(any(), any()) } returns
                    AppResult.Failure(AppError.Domain.Unauthorized)

                val res = repo.signInWithEmailAndPassword("x@y.z", "bad")

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Unauthorized
            }
        }
    }

    context("resetPassword") {

        test("resetPassword forwards to remote and returns Success(Unit)") {
            runTest {
                coEvery { remote.resetPassword("u@acme.io") } returns AppResult.Success(Unit)

                val res = repo.resetPassword("u@acme.io")

                res.shouldBeInstanceOf<AppResult.Success<Unit>>()
            }
        }

        test("resetPassword forwards Failure from remote") {
            runTest {
                coEvery { remote.resetPassword(any()) } returns AppResult.Failure(AppError.Network.Unreachable)

                val res = repo.resetPassword("x@y.z")

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Unreachable
            }
        }
    }

    context("signInWithCredential") {

        test("signInWithCredential propagates Failure from remote") {
            runTest {
                coEvery { remote.signInWithCredentials(credential, ProviderType.GOOGLE) } returns
                    AppResult.Failure(AppError.Domain.Forbidden)

                val res = repo.signInWithCredential(credential, ProviderType.GOOGLE)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Forbidden
            }
        }

        test("signInWithCredential maps Success(null) -> Failure(Authentication.Unknown) via requireNonNull") {
            runTest {
                coEvery { remote.signInWithCredentials(credential, ProviderType.APPLE) } returns
                    AppResult.Success(null)

                val res = repo.signInWithCredential(credential, ProviderType.APPLE)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Authentication.Unknown
            }
        }

        test("signInWithCredential maps AuthFirebaseUser -> FirebaseUserInfoDomainModel") {
            runTest {
                val remoteUser = AuthFirebaseUser(
                    uid = "U1",
                    email = "u1@acme.io",
                    providers = listOf("google.com", "password"),
                    signInProvider = "google.com"
                )
                coEvery { remote.signInWithCredentials(credential, ProviderType.GOOGLE) } returns
                    AppResult.Success(remoteUser)

                val res = repo.signInWithCredential(credential, ProviderType.GOOGLE)

                res.shouldBeInstanceOf<AppResult.Success<FirebaseUserInfoDomainModel>>()
                res.data.uid shouldBe "U1"
                res.data.email shouldBe "u1@acme.io"
                res.data.providers!!.shouldContainExactly(listOf("google.com", "password"))
            }
        }
    }

    context("linkWithCredential") {

        test("linkInWithCredential propagates Failure from remote") {
            runTest {
                coEvery { remote.linkWithCredential(credential) } returns AppResult.Failure(AppError.Domain.Unauthorized)

                val res = repo.linkInWithCredential(credential)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Unauthorized
            }
        }

        test("linkInWithCredential maps Success(null) -> Failure(Authentication.Unknown) via requireNonNull") {
            runTest {
                coEvery { remote.linkWithCredential(credential) } returns AppResult.Success(null)

                val res = repo.linkInWithCredential(credential)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Authentication.Unknown
            }
        }

        test("linkInWithCredential maps AuthFirebaseUser -> FirebaseUserInfoDomainModel") {
            runTest {
                val remoteUser = AuthFirebaseUser(
                    uid = "U2",
                    email = null, // doit devenir DEFAULT_EMAIL via extension
                    providers = listOf("apple.com"),
                    signInProvider = "apple.com"
                )
                coEvery { remote.linkWithCredential(credential) } returns AppResult.Success(
                    remoteUser
                )

                val res = repo.linkInWithCredential(credential)

                res.shouldBeInstanceOf<AppResult.Success<FirebaseUserInfoDomainModel>>()
                res.data.uid shouldBe "U2"
                res.data.email shouldBe AuthFirebaseUser.DEFAULT_EMAIL
                res.data.providers!!.shouldContainExactly(listOf("apple.com"))
            }
        }
    }

    context("isFirstSignIn") {

        test("isFirstSignIn forwards and returns Success(true)") {
            runTest {
                coEvery { remote.isFirstSignIn("uid-x") } returns AppResult.Success(true)

                val res = repo.isFirstSignIn("uid-x")

                res.shouldBeInstanceOf<AppResult.Success<Boolean>>()
                res.data shouldBe true
            }
        }

        test("isFirstSignIn forwards Failure from remote") {
            runTest {
                coEvery { remote.isFirstSignIn(any()) } returns AppResult.Failure(AppError.Network.Timeout)

                val res = repo.isFirstSignIn("uid-y")

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Timeout
            }
        }
    }

    context("fetchFirebaseUserInfo") {

        test("fetchFirebaseUserInfo maps Success(null) -> Success(SignedOut)") {
            runTest {
                coEvery { remote.fetchFirebaseUserInfo() } returns AppResult.Success(null)

                val res = repo.fetchFirebaseUserInfo()

                res.shouldBeInstanceOf<AppResult.Success<AuthState>>()
                res.data shouldBe AuthState.SignedOut
            }
        }

        test("fetchFirebaseUserInfo maps AuthFirebaseUser -> Success(Authenticated(mapped))") {
            runTest {
                val remoteUser = AuthFirebaseUser(
                    uid = "U3",
                    email = "u3@acme.io",
                    providers = listOf("password")
                )
                coEvery { remote.fetchFirebaseUserInfo() } returns AppResult.Success(remoteUser)

                val res = repo.fetchFirebaseUserInfo()

                res.shouldBeInstanceOf<AppResult.Success<AuthState>>()
                val state = res.data
                state.shouldBeInstanceOf<AuthState.Authenticated>()
                state.user.uid shouldBe "U3"
                state.user.email shouldBe "u3@acme.io"
                state.user.providers!!.shouldContainExactly(listOf("password"))
            }
        }

        test("fetchFirebaseUserInfo propagates Failure from remote") {
            runTest {
                coEvery { remote.fetchFirebaseUserInfo() } returns AppResult.Failure(AppError.Network.Unreachable)

                val res = repo.fetchFirebaseUserInfo()

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Unreachable
            }
        }
    }
})
