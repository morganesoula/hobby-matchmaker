package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class ProviderErrorHMM(override val message: String) : HMMAppError {
    data class GoogleSignInErrorHMM(val errorMessage: String) : ProviderErrorHMM(errorMessage)
    data class FacebookSignInErrorHMM(val errorMessage: String) : ProviderErrorHMM(errorMessage)
    data class AppleSignInErrorHMM(val errorMessage: String) : ProviderErrorHMM(errorMessage)
    data class ProviderLogOutErrorHMM(val errorMessage: String) : ProviderErrorHMM(errorMessage)
    data class NoProviderFound(val errorMessage: String) : ProviderErrorHMM(errorMessage)
}
