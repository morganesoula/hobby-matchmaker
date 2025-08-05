package com.msoula.hobbymatchmaker.core.common

import kotlinx.datetime.LocalDate
import kotlin.coroutines.cancellation.CancellationException

expect fun getDeviceLocale(): String
expect fun isIosPlatform(): Boolean

suspend fun <Data, ErrorType: AppError> safeCall(
    appError: (String) -> ErrorType,
    action: suspend () -> Data
): Result<Data, ErrorType> {
    return try {
        Result.Success(action())
    } catch (exception: CancellationException) {
        throw exception
    } catch (e: Exception) {
        Result.Failure(appError(e.message ?: "Unknown error"))
    }
}

fun String.extractYear(): String = runCatching {
    LocalDate.parse(this).year.toString()
}.getOrDefault("")
