package com.msoula.hobbymatchmaker.core.session.data.dataSources.local.helpers

import androidx.datastore.core.IOException
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import kotlinx.coroutines.CancellationException

suspend inline fun safeLocalWrite(crossinline block: suspend () -> Unit): R<Unit, AppError> =
    try {
        block()
        R.Success(Unit)
    } catch (_: IOException) {
        R.Failure(AppError.Storage.WriteFailed)
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(AppError.Network.Unknown(t))
    }
