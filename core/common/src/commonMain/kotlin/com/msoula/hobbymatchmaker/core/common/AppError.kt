package com.msoula.hobbymatchmaker.core.common

sealed interface AppError {
    sealed interface Network : AppError {
        data object Timeout : Network
        data object Unreachable : Network
        data object Canceled : Network
        data object Serialization : Network
        data class Http(val code: Int, val body: String? = null) : Network
        data class Unknown(val causeMessage: String? = null) : Network
    }

    sealed interface Domain : AppError {
        data object Unauthorized : Domain
        data object Forbidden : Domain
        data object NotFound : Domain
        data class Validation(val reason: String) : Domain
    }

    sealed interface Storage : AppError {
        data object WriteFailed : Storage
        data object ReadFailed : Storage
        data object Corrupted : Storage
    }

    sealed interface External : AppError {
        data class Service(val provider: String, val message: String? = null) : External
    }
}
