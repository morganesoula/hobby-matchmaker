package com.msoula.hobbymatchmaker.core.network

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall

interface NetworkConnectivityChecker {
    fun hasActiveConnection(): Boolean
}

suspend inline fun <Entry> safeNetworkCall(
    connectivityChecker: NetworkConnectivityChecker,
    crossinline block: suspend () -> Entry
): AppResult<Entry, AppError> {
    if (!connectivityChecker.hasActiveConnection()) {
        return AppResult.Failure(AppError.Network.Unreachable)
    }

    return safeFirebaseCall { block() }
}
