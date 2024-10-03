package com.msoula.hobbymatchmaker.core.common

import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

fun getDeviceLocale(): String {
    val locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

suspend fun <T> safeCall(
    appError: (String) -> AppError,
    action: suspend () -> T
): Result<T> {
    return try {
        Result.Success(action())
    } catch (exception: CancellationException) {
        throw exception
    } catch (e: Exception) {
        Result.Failure(appError(e.message ?: "Unknown error"))
    }
}


