package com.msoula.hobbymatchmaker.core.network

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ExternalServiceError
import com.msoula.hobbymatchmaker.core.common.NetworkError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.ServerError
import kotlinx.coroutines.CancellationException
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
suspend fun <R> execute(
    call: suspend () -> Response<R>,
    errorCallBack: ((Int) -> AppError)? = null
): Result<R> {
    return try {
        val response = call()
        val body = response.body()

        when {
            response.isSuccessful && body != null -> {
                Result.Success(body)
            }

            response.isSuccessful && (Unit as? R).toString() == Unit.toString() -> {
                Result.Success(Unit as R)
            }

            else -> {
                val error = response.code()
                Result.Failure(errorCallBack?.invoke(error) ?: parseCommonError(error))
            }
        }
    } catch (exception: CancellationException) {
        Log.e(
            "HMM",
            "Error parsing data ${exception.message}"
        )
        throw exception
    } catch (exception: Exception) {
        Log.d("HMM", "Error message is: ${exception.message}")
        Result.Failure(NetworkError())
    }
}

fun parseCommonError(error: Int): AppError {
    return when (error) {
        503 -> ServerError()
        else -> ExternalServiceError()
    }
}
