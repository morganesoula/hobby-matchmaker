package com.msoula.hobbymatchmaker.core.common

import java.util.Locale

fun getDeviceLocale(): String {
    val locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

suspend fun <T> safeFirebaseCall(
    appError: (String) -> AppError,
    action: suspend () -> T
): Result<T> {
    return try {
        Result.Success(action())
    } catch (e: Exception) {
        Result.Failure(appError(e.message ?: "Unknown error"))
    }
}


