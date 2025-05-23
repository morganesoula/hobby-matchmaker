package com.msoula.hobbymatchmaker.core.network

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

expect class Utility {
    companion object {
        fun getPlatformTMDBKey(): String
    }
}

suspend inline fun <T, E : AppError> safeKtorCall(
    crossinline block: suspend () -> Result<T, E>,
    crossinline errorMapper: (Throwable) -> E
): Result<T, E> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Logger.e("Network error: ${e.message}")
        Result.Failure(errorMapper(e))
    } catch (e: Exception) {
        Logger.e("Unexpected error: ${e.message}", e)
        Result.Failure(errorMapper(e))
    }
}
