package com.msoula.hobbymatchmaker.core.common

import kotlin.coroutines.cancellation.CancellationException

expect fun getDeviceLocale(): String

suspend fun <Data> safeCall(
    appError: (String) -> AppError,
    action: suspend () -> Data
): Result<Data, Nothing> {
    return try {
        Result.Success(action())
    } catch (exception: CancellationException) {
        throw exception
    } catch (e: Exception) {
        Result.Failure(appError(e.message ?: "Unknown error"))
    }
}
