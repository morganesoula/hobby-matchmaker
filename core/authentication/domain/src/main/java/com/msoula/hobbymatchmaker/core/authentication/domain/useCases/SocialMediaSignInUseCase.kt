package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import android.content.Context
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SocialMediaSignInBisUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val linkInWithCredentialUseCase: LinkInWithCredentialUseCase,
    private val isFirstSignInUseCase: IsFirstSignInUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo
) : FlowUseCase<
    Parameters.GetCredentialResponseParam, SocialMediaSignInSuccess, AppError>
    (dispatcher) {
    override fun execute(parameters: Parameters.GetCredentialResponseParam):
        Flow<Result<SocialMediaSignInSuccess, AppError>> {
        return channelFlow {
            send(Result.Loading)

            val type = if (parameters.facebookAccessToken != null) "FACEBOOK" else "GOOGLE"
            val facebookToken = parameters.facebookAccessToken?.token
            val facebookEmail: String? =
                if (facebookToken != null) {
                    when (val result =
                        authenticationRepository.fetchFacebookClient(parameters.facebookAccessToken!!)) {
                        is Result.Success -> result.data
                        is Result.Failure -> {
                            send(Result.Failure(result.error))
                            ""
                        }

                        is Result.BusinessRuleError -> {
                            send(Result.BusinessRuleError(result.error))
                            ""
                        }

                        else -> ""
                    }
                } else {
                    Log.d("HMM", "No facebook token found")
                    ""
                }

            when (val credentials = getCredentials(facebookToken, parameters.context)) {
                is Result.Success -> when (val logging = loggingWithSocialMedia(
                    credentials = credentials.data!!,
                    type = type,
                    fetchFirebaseUserInfo = fetchFirebaseUserInfo,
                    signInWithCredentialUseCase = signInWithCredentialUseCase,
                    isFirstSignInUseCase = isFirstSignInUseCase,
                    linkInWithCredentialUseCase = linkInWithCredentialUseCase
                )) {
                    is Result.Success -> {
                        setIsConnectedUseCase(true)

                        when (val creatingUserResult = createUser(
                            uid = logging.data?.user?.uid ?: "random",
                            email = when (type) {
                                "GOOGLE" -> logging.data?.user?.email ?: ""
                                else -> facebookEmail ?: ""
                            },
                            createUserUseCase = createUserUseCase
                        )) {
                            is Result.Success -> send(Result.Success(SocialMediaSignInSuccess))
                            is Result.Failure -> send(Result.Failure(creatingUserResult.error))
                            is Result.BusinessRuleError -> send(
                                Result.BusinessRuleError(
                                    creatingUserResult.error
                                )
                            )

                            else -> Unit
                        }
                    }

                    is Result.Failure -> send(Result.Failure(logging.error))
                    is Result.BusinessRuleError -> send(Result.BusinessRuleError(logging.error))
                    else -> Unit
                }

                is Result.Failure -> send(Result.Failure(credentials.error))
                is Result.BusinessRuleError -> send(Result.BusinessRuleError(credentials.error))
                is Result.Loading -> Unit
            }
        }.flowOn(dispatcher)
    }

    private suspend fun getCredentials(
        facebookToken: String?,
        context: Context
    ): Result<AuthCredential?, SocialMediaSignInError> {
        return if (facebookToken != null) {
            when (val result =
                authenticationRepository.fetchFacebookCredentials(facebookToken)) {
                is Result.Success -> Result.Success(result.data)
                else -> {
                    Result.Failure(SocialMediaSignInError.FetchFacebookCredentialsError)
                }
            }
        } else {
            when (val result =
                authenticationRepository.connectWithGoogle(context)) {
                is Result.Success -> Result.Success(result.data.first)
                else -> {
                    Result.Failure(SocialMediaSignInError.FetchGoogleAuthClientError)
                }
            }
        }
    }

    private suspend fun loggingWithSocialMedia(
        credentials: AuthCredential,
        type: String,
        fetchFirebaseUserInfo: FetchFirebaseUserInfo,
        isFirstSignInUseCase: IsFirstSignInUseCase,
        signInWithCredentialUseCase: SignInWithCredentialUseCase,
        linkInWithCredentialUseCase: LinkInWithCredentialUseCase
    ): Result<AuthResult?, SocialMediaError> {
        val firebaseUserInfo = fetchFirebaseUserInfo()
        val isFirstTime = isFirstSignInUseCase(firebaseUserInfo?.uid ?: "")

        val providerId = when (type) {
            "GOOGLE" -> "google.com"
            "FACEBOOK" -> "facebook.com"
            else -> ""
        }

        return if (isFirstTime || firebaseUserInfo?.providers?.contains(providerId) == true) {
            signInWithCredentialUseCase(credentials)
        } else {
            linkInWithCredentialUseCase(credentials)
        }
    }

    private suspend fun createUser(
        uid: String,
        email: String,
        createUserUseCase: CreateUserUseCase
    ): Result<Boolean, SocialMediaSignInError.CreateUserError> {
        return when (val result =
            createUserUseCase(
                SessionUserDomainModel(
                    uid = uid,
                    email = email
                )
            )) {
            is Result.Success -> Result.Success(true)
            is Result.Failure -> Result.Failure(result.error)
            is Result.BusinessRuleError -> Result.Failure(result.error)
            else -> Result.Loading
        }
    }
}

sealed class SocialMediaSignInError(override val message: String) : AppError {
    data object FetchGoogleAuthClientError : SocialMediaSignInError("FetchGoogleAuthClientError")
    data object FetchFacebookCredentialsError :
        SocialMediaSignInError("FetchFacebookCredentialsError")

    data class CreateUserError(override val message: String) : SocialMediaSignInError(message)
}

data object SocialMediaSignInSuccess
