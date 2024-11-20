package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import android.content.Context
import android.util.Log
import com.facebook.AccessToken
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

class SocialMediaSignInUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val socialMediaSignInUseCases: SocialMediaSignInUseCases
) : FlowUseCase<
    Parameters.GetCredentialResponseParam, SocialMediaSignInSuccess, AppError>
    (dispatcher) {
    override fun execute(parameters: Parameters.GetCredentialResponseParam):
        Flow<Result<SocialMediaSignInSuccess, AppError>> {
        return channelFlow {
            send(Result.Loading)

            val type = determineSocialMediaType(parameters)
            val facebookToken = parameters.facebookAccessToken?.token
            val facebookEmail: String = fetchFacebookEmail(parameters)

            when (val credentials = getCredentials(facebookToken, parameters.context)) {
                is Result.Success -> when (val logging = loggingWithSocialMedia(
                    credentials = credentials.data!!,
                    type = type,
                    fetchFirebaseUserInfo = socialMediaSignInUseCases.fetchFirebaseUserInfo,
                    signInWithCredentialUseCase = socialMediaSignInUseCases.signInWithCredentialUseCase,
                    isFirstSignInUseCase = socialMediaSignInUseCases.isFirstSignInUseCase,
                    linkInWithCredentialUseCase = socialMediaSignInUseCases.linkInWithCredentialUseCase
                )) {
                    is Result.Success -> {
                        socialMediaSignInUseCases.setIsConnectedUseCase(true)

                        when (val creatingUserResult = createUser(
                            uid = logging.data?.user?.uid ?: "random",
                            email = when (type) {
                                "GOOGLE" -> logging.data?.user?.email ?: ""
                                else -> facebookEmail ?: ""
                            },
                            createUserUseCase = socialMediaSignInUseCases.createUserUseCase
                        )) {
                            is Result.Success -> send(Result.Success(SocialMediaSignInSuccess))
                            is Result.Failure -> send(Result.Failure(creatingUserResult.error))
                            else -> Unit
                        }
                    }

                    is Result.Failure -> send(Result.Failure(logging.error))
                    else -> Unit
                }

                is Result.Failure -> send(Result.Failure(credentials.error))
                is Result.Loading -> Unit
            }
        }.flowOn(dispatcher)
    }

    private fun determineSocialMediaType(parameters: Parameters.GetCredentialResponseParam): String {
        return if (parameters.facebookAccessToken != null) "FACEBOOK" else "GOOGLe"
    }

    private suspend fun fetchFacebookEmail(parameters: Parameters.GetCredentialResponseParam): String {
        val facebookToken: String? = parameters.facebookAccessToken?.token

        return facebookToken?.let {
            when (val result = fetchFacebookClient(parameters.facebookAccessToken!!)) {
                is Result.Success -> result.data
                is Result.Failure -> ""
                else -> ""
            }
        } ?: run {
            Log.d("HMM", "No facebook token found")
            ""
        }
    }

    private suspend fun fetchFacebookClient(accessToken: AccessToken): Result<String?, AppError>? {
        return when (val result = authenticationRepository.fetchFacebookClient(accessToken)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Failure -> Result.Failure(result.error)
            else -> Result.Loading
        }
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
            else -> Result.Loading
        }
    }
}

class SocialMediaSignInUseCases(
    val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    val linkInWithCredentialUseCase: LinkInWithCredentialUseCase,
    val isFirstSignInUseCase: IsFirstSignInUseCase,
    val setIsConnectedUseCase: SetIsConnectedUseCase,
    val createUserUseCase: CreateUserUseCase,
    val fetchFirebaseUserInfo: FetchFirebaseUserInfo
)

sealed class SocialMediaSignInError(override val message: String) : AppError {
    data object FetchGoogleAuthClientError : SocialMediaSignInError("FetchGoogleAuthClientError")
    data object FetchFacebookCredentialsError :
        SocialMediaSignInError("FetchFacebookCredentialsError")

    data class CreateUserError(override val message: String) : SocialMediaSignInError(message)
}

data object SocialMediaSignInSuccess
