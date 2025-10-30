package com.msoula.hobbymatchmaker.core.network

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.toGenericAppError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException

fun Throwable.toKtorAppError(): AppError = when (this) {
    is NoTransformationFoundException -> AppError.Network.Serialization
    is HttpRequestTimeoutException -> AppError.Network.Timeout
    is ResponseException -> {
        val code = response.status.value
        AppError.Network.Http(code, message)
    }
    else -> toGenericAppError()
}
