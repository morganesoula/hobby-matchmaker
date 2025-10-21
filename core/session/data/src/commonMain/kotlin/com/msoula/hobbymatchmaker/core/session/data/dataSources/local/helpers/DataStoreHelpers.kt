package com.msoula.hobbymatchmaker.core.session.data.dataSources.local.helpers

import androidx.datastore.core.IOException
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import kotlinx.coroutines.CancellationException

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
