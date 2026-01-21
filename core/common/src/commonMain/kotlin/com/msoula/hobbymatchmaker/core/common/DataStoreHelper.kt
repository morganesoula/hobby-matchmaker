package com.msoula.hobbymatchmaker.core.common

import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

suspend inline fun safeLocalWrite(crossinline block: suspend () -> Unit): AppResult<Unit, AppError> =
    try {
        block()
        AppResult.Success(Unit)
    } catch (_: IOException) {
        AppResult.Failure(AppError.Storage.WriteFailed)
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        AppResult.Failure(AppError.Network.Unknown(t))
    }
