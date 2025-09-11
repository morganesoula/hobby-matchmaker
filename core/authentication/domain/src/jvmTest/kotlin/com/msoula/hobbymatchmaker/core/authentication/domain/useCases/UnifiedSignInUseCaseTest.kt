package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import dev.gitlive.firebase.auth.AuthCredential
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
class UnifiedSignInUseCaseTest : FunSpec({
    lateinit var signInUseCase: SignInUseCase
    lateinit var signInWithCredentialUseCase: SignInWithCredentialUseCase
    lateinit var setIsConnected: SetIsConnectedUseCase
    lateinit var useCase: UnifiedSignInUseCase

    lateinit var credential: AuthCredential

    beforeTest {
        MockKAnnotations.init(this)
        signInUseCase = mockk()
        signInWithCredentialUseCase = mockk()
        setIsConnected = mockk()
        credential = mockk(relaxed = true)

        useCase = UnifiedSignInUseCase(
            signInUseCase = signInUseCase,
            signInWithCredentialUseCase = signInWithCredentialUseCase,
            setIsConnectedUseCase = setIsConnected
        )
    }

    context("email and password") {
        test("EmailPassword: signInUseCase Failure -> propagates; setIsConnected not called") {
            runTest {
                coEvery { signInUseCase(Parameters.DoubleStringParam("u@acme.io", "bad")) } returns
                    AppResult.Failure(AppError.Domain.Unauthorized)

                val res = useCase(
                    UnifiedSignInUseCase.Params.EmailPassword("u@acme.io", "bad")
                )

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Unauthorized
                coVerify(exactly = 0) { setIsConnected(true) }
                coVerify(exactly = 0) { signInWithCredentialUseCase.invoke(any(), any()) }
            }
        }

        test("EmailPassword: signInUseCase Success then setIsConnected Failure -> propagates set error") {
            runTest {
                coEvery { signInUseCase(Parameters.DoubleStringParam("u@acme.io", "pwd")) } returns
                    AppResult.Success(SignInSuccess)
                coEvery { setIsConnected(true) } returns
                    AppResult.Failure(AppError.Storage.WriteFailed)

                val res = useCase(
                    UnifiedSignInUseCase.Params.EmailPassword("u@acme.io", "pwd")
                )

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Storage.WriteFailed
                coVerify(exactly = 1) { setIsConnected(true) }
                coVerify(exactly = 0) { signInWithCredentialUseCase.invoke(any(), any()) }
            }
        }

        test("EmailPassword: full success -> Success(SignInSuccess)") {
            runTest {
                coEvery { signInUseCase(Parameters.DoubleStringParam("u@acme.io", "pwd")) } returns
                    AppResult.Success(SignInSuccess)
                coEvery { setIsConnected(true) } returns AppResult.Success(Unit)

                val res = useCase(
                    UnifiedSignInUseCase.Params.EmailPassword("u@acme.io", "pwd")
                )

                res.shouldBeInstanceOf<AppResult.Success<SignInSuccess>>()
                res.data shouldBe SignInSuccess
                coVerify(exactly = 1) { setIsConnected(true) }
                coVerify(exactly = 0) { signInWithCredentialUseCase.invoke(any(), any()) }
            }
        }
    }

    context("socialMedia") {

        test("SocialMedia: signInWithCredentialUseCase Failure -> propagates; setIsConnected not called") {
            runTest {
                coEvery {
                    signInWithCredentialUseCase.invoke(
                        credential,
                        ProviderType.GOOGLE
                    )
                } returns
                    AppResult.Failure(AppError.Domain.Forbidden)

                val res = useCase(
                    UnifiedSignInUseCase.Params.SocialMedia(credential, ProviderType.GOOGLE)
                )

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Domain.Forbidden
                coVerify(exactly = 0) { setIsConnected(true) }
                coVerify(exactly = 0) { signInUseCase.invoke(any()) }
            }
        }

        test("SocialMedia: success then setIsConnected Failure -> propagates set error") {
            runTest {
                val fbUser = FirebaseUserInfoDomainModel(
                    uid = "U_FB",
                    email = "fb@acme.io",
                    providers = listOf("facebook.com")
                )

                coEvery {
                    signInWithCredentialUseCase.invoke(
                        credential,
                        ProviderType.APPLE
                    )
                } returns AppResult.Success(fbUser)

                coEvery { setIsConnected(true) } returns
                    AppResult.Failure(AppError.Network.Timeout)

                val res = useCase(
                    UnifiedSignInUseCase.Params.SocialMedia(credential, ProviderType.APPLE)
                )

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Timeout
                coVerify(exactly = 1) { setIsConnected(true) }
                coVerify(exactly = 0) { signInUseCase.invoke(any()) }
            }
        }

        test("SocialMedia: full success -> Success(SignInSuccess)") {
            runTest {
                val fbUser2 = FirebaseUserInfoDomainModel(
                    uid = "U_FB_2",
                    email = "fb2@acme.io",
                    providers = listOf("facebook.com")
                )

                coEvery {
                    signInWithCredentialUseCase.invoke(
                        credential,
                        ProviderType.FACEBOOK
                    )
                } returns AppResult.Success(fbUser2)
                coEvery { setIsConnected(true) } returns AppResult.Success(Unit)

                val res = useCase(
                    UnifiedSignInUseCase.Params.SocialMedia(credential, ProviderType.FACEBOOK)
                )

                res.shouldBeInstanceOf<AppResult.Success<SignInSuccess>>()
                res.data shouldBe SignInSuccess
                coVerify(exactly = 1) { setIsConnected(true) }
                coVerify(exactly = 0) { signInUseCase.invoke(any()) }
            }
        }
    }
})
