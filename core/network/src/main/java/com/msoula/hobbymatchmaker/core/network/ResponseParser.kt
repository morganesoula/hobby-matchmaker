package com.msoula.hobbymatchmaker.core.network

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ExternalServiceError
import com.msoula.hobbymatchmaker.core.common.NetworkError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.ServerError
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import retrofit2.Response

suspend fun <Data> execute(
    call: suspend () -> Response<Data>,
    errorCallBack: ((Int) -> AppError)? = null
): Result<Data, Nothing> {
    return try {
        val response = call()
        val body = response.body()

        when {
            response.isSuccessful && body != null -> {
                Result.Success(body)
            }

            else -> {
                val error = response.code()
                Result.Failure(errorCallBack?.invoke(error) ?: parseCommonError(error))
            }
        }
    } catch (exception: Exception) {
        currentCoroutineContext().ensureActive()
        Log.e("HMM", "Error while executing call: ${exception.message}", exception)
        Result.Failure(NetworkError())
    }
}

fun parseCommonError(error: Int): AppError {
    return when (error) {
        503 -> ServerError()
        else -> ExternalServiceError()
    }
}
