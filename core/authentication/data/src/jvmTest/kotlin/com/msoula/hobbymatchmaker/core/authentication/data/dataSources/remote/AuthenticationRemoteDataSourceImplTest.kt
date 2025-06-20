package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.fakes.FakeAuthManager
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class AuthenticationRemoteDataSourceImplTest : FunSpec({
    val dispatcher = StandardTestDispatcher()
    val auth = mockk<FirebaseAuth>(relaxed = true)
    val firestore = mockk<FirebaseFirestore>()
    val authManager = FakeAuthManager()
    val dataSource = AuthenticationRemoteDataSourceImpl(auth, firestore, authManager)

    context("signOut") {
        test("authenticationSignOut should return success") {
            runTest(dispatcher) {
                authManager.signOutResult = Result.Success(true)
                val result = dataSource.authenticationSignOut()
                result shouldBe Result.Success(true)
            }
        }

        test("authenticationSignOut should handle exception") {
            runTest(dispatcher) {
                authManager.shouldThrow = true
                val result = dataSource.authenticationSignOut()
                result shouldBe Result.Failure(LogOutError.UnknownError("Some unexpected exception"))
            }
        }
    }

    context("signIn") {
        test("signInWithCredentials should return success") {
            runTest(dispatcher) {
                val result = dataSource.signInWithCredentials(mockk(), ProviderType.GOOGLE)
                result shouldBe Result.Success(
                    FirebaseUserInfoDomainModel(
                        "uid",
                        "email@fake.com",
                        listOf("google.com")
                    )
                )
            }
        }
    }

    context("linkWithCredential") {
        test("linkWithCredential should return success") {
            runTest(dispatcher) {
                val credential = mockk<AuthCredential>()
                val user = mockk<FirebaseUser> {
                    every { uid } returns "123"
                    every { email } returns "user@example.com"
                    every { providerData } returns emptyList()
                }

                val authResult = mockk<AuthResult>()
                every { authResult.user } returns user

                val currentUser = mockk<FirebaseUser> {
                    coEvery { linkWithCredential(credential) } returns authResult
                }

                every { auth.currentUser } returns currentUser

                val result = dataSource.linkWithCredential(credential)
                result shouldBe Result.Success(
                    FirebaseUserInfoDomainModel(
                        "123",
                        "user@example.com",
                        emptyList()
                    )
                )
            }
        }

        test("linkWithCredential should return failure when user is null") {
            runTest(dispatcher) {
                val credential = mockk<AuthCredential>()
                val authResult = mockk<AuthResult> {
                    every { user } returns null
                }
                coEvery { auth.currentUser?.linkWithCredential(credential) } returns authResult

                val result = dataSource.linkWithCredential(credential)
                result shouldBe Result.Failure(SocialMediaError.LinkWithCredentialsError)
            }
        }

        test("linkWithCredential should return failure on exception") {
            runTest(dispatcher) {
                val credential = mockk<AuthCredential>()
                coEvery { auth.currentUser?.linkWithCredential(credential) } throws Exception("fail")

                val result = dataSource.linkWithCredential(credential)
                result shouldBe Result.Failure(SocialMediaError.LinkWithCredentialsError)
            }
        }
    }

    context("createUserWithEmailAndPassword") {
        test("createUserWithEmailAndPassword should return success") {
            runTest(dispatcher) {
                val mockAuthResult = mockk<AuthResult>()
                val mockFirebaseUser = mockk<FirebaseUser>()

                coEvery {
                    auth.createUserWithEmailAndPassword(
                        "test@example.com",
                        "password"
                    )
                } returns mockAuthResult

                every { auth.currentUser } returns mockFirebaseUser
                every { mockFirebaseUser.uid } returns "uid123"

                val result =
                    dataSource.createUserWithEmailAndPassword("test@example.com", "password")
                result shouldBe Result.Success("uid123")
            }
        }
    }


    context("signInWithEmailAndPassword") {
        test("signInWithEmailAndPassword should return success") {
            runTest(dispatcher) {
                val user = mockk<FirebaseUser>()
                every { user.uid } returns "uid456"

                val resultAuth = mockk<AuthResult>()
                every { resultAuth.user } returns user

                coEvery {
                    auth.signInWithEmailAndPassword(
                        "test@example.com",
                        "password"
                    )
                } returns resultAuth

                val result = dataSource.signInWithEmailAndPassword("test@example.com", "password")
                result shouldBe Result.Success("uid456")
            }
        }

        test("signInWithEmailAndPassword should throw if user is null") {
            runTest(dispatcher) {
                val resultAuth = mockk<AuthResult> {
                    every { user } returns null
                }
                coEvery {
                    auth.signInWithEmailAndPassword(
                        "test@example.com",
                        "password"
                    )
                } returns resultAuth

                shouldThrow<Error> {
                    dataSource.signInWithEmailAndPassword("test@example.com", "password")
                }
            }
        }
    }

    context("resetPassword") {
        test("resetPassword should return success") {
            runTest(dispatcher) {
                coEvery { auth.sendPasswordResetEmail("test@example.com") } returns Unit

                val result = dataSource.resetPassword("test@example.com")
                result shouldBe Result.Success(true)
            }
        }
    }

    context("getUserUid") {
        test("getUserUid should return uid") {
            runTest(dispatcher) {
                every { auth.currentUser?.uid } returns "uid789"
                val result = dataSource.getUserUid()
                result shouldBe "uid789"
            }
        }
    }

    context("isFirstSignIn") {
        test("isFirstSignIn should return true when uid is empty") {
            runTest(dispatcher) {
                val result = dataSource.isFirstSignIn("")
                result shouldBe true
            }
        }
    }

    context("fetchFirebaseUserInfo") {
        test("fetchFirebaseUserInfo should return domain model") {
            runTest(dispatcher) {
                val user = mockk<FirebaseUser> {
                    every { uid } returns "123"
                    every { email } returns "email@fake.com"
                    every { providerData } returns emptyList()
                }
                every { auth.currentUser } returns user

                val result = dataSource.fetchFirebaseUserInfo()
                result shouldBe FirebaseUserInfoDomainModel("123", "email@fake.com", emptyList())
            }
        }
    }
})
