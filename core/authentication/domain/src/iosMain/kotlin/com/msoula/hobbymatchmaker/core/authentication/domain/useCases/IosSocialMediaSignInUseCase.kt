package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.IosAuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class IosSocialMediaSignInUseCase(
    dispatcher: CoroutineDispatcher,
    private val authenticationRepository: IosAuthenticationRepository
) :
    FlowUseCase<Parameters.StringParam, SocialMediaSignInSuccess, SocialMediaSignInError>(dispatcher) {

    override fun execute(parameters: Parameters.StringParam): Flow<Result<SocialMediaSignInSuccess, SocialMediaSignInError>> {
        return channelFlow {
            send(Result.Loading)

            when (val result = getCredentials(parameters.value)) {
                is Result.Success -> connectWithCredentials(result.data)
                is Result.Failure -> send(Result.Failure(result.error))
                else -> Unit
            }
        }
    }

    private suspend fun getCredentials(provider: String): Result<AuthCredential?, SocialMediaSignInError> {
        return when (provider) {
            "GOOGLE" -> getGoogleCredentials()
            "APPLE" -> getAppleCredentials()
            else -> Result.Failure(SocialMediaSignInError.UnknownCredentialProviderError)
        }
    }

    private suspend fun getGoogleCredentials(): Result<AuthCredential?, SocialMediaSignInError> {
        return when (val result = authenticationRepository.getGoogleCredentials()) {
            is Result.Success -> Result.Success(result.data.first)
            else -> {
                Result.Failure(SocialMediaSignInError.EmptyGoogleCredentialError)
            }
        }
    }

    private suspend fun getAppleCredentials(): Result<AuthCredential?, SocialMediaSignInError> {
        return when (val result = authenticationRepository.getAppleCredentials()) {
            is Result.Success -> Result.Success(result.data.first)
            else -> {
                Result.Failure(SocialMediaSignInError.FetchGoogleAuthClientError)
            }
        }
    }

    private suspend fun connectWithCredentials(credential: AuthCredential?): Result<Boolean, SocialMediaSignInError> {
        return if (credential != null) {
            when (authenticationRepository.connectWithCredentials(credential)) {
                is Result.Success -> Result.Success(true)
                else -> Result.Failure(SocialMediaSignInError.ConnectWithCredentialsError)
            }
        } else Result.Failure(SocialMediaSignInError.EmptyGoogleCredentialError)
    }
}

sealed class SocialMediaSignInError(override val message: String) : AppError {
    data object FetchGoogleAuthClientError : SocialMediaSignInError("FetchGoogleAuthClientError")
    data object AppleSignInError : SocialMediaSignInError("AppleSignInError")
    data object ConnectWithCredentialsError : SocialMediaSignInError("SocialMediaSignInError")
    data object EmptyGoogleCredentialError : SocialMediaSignInError("EmptyGoogleCredentialError")
    data object UnknownCredentialProviderError :
        SocialMediaSignInError("UnknownCredentialProviderError")

    data class Error(override val message: String) : SocialMediaSignInError(message)
}

data object SocialMediaSignInSuccess


