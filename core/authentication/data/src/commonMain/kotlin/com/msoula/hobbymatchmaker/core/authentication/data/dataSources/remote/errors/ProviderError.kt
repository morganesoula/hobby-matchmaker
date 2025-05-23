package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class ProviderError(override val message: String) : AppError {
    data class GoogleSignInError(val errorMessage: String) : ProviderError(errorMessage)
    data class FacebookSignInError(val errorMessage: String) : ProviderError(errorMessage)
    data class AppleSignInError(val errorMessage: String) : ProviderError(errorMessage)
    data class ProviderLogOutError(val errorMessage: String) : ProviderError(errorMessage)
    data class NoProviderFound(val errorMessage: String): ProviderError(errorMessage)
}
