package com.msoula.hobbymatchmaker.core.design.util

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

enum class RetryPolicy { Manual, Never, WithBackoff }

data class UIErrorHint(
    val retry: RetryPolicy = RetryPolicy.Never,
    val isUserActionable: Boolean = false
)

fun <T, R> AppResult<T, AppError>.toUIState(
    errorMessageMapper: ErrorMessageMapper,
    mapper: (T) -> UiState<R>
): UiState<R> =
    when (this) {
        is AppResult.Success -> mapper(data)
        is AppResult.Failure ->
            UiState.Error(errorMessageMapper.toUIText(error), error.hint())
    }

fun AppError.hint(): UIErrorHint = when (this) {
    is AppError.Network.Timeout -> UIErrorHint(RetryPolicy.WithBackoff, false)
    is AppError.Network.Unreachable -> UIErrorHint(RetryPolicy.WithBackoff, false)
    is AppError.Network.Http -> UIErrorHint(
        retry = if (code in 500..599) RetryPolicy.WithBackoff else RetryPolicy.Never,
        isUserActionable = code in listOf(401, 403)
    )

    is AppError.Network.Canceled -> UIErrorHint(RetryPolicy.Never, false)
    is AppError.Network.Serialization -> UIErrorHint(RetryPolicy.Never, false)
    is AppError.Network.Unknown -> UIErrorHint(RetryPolicy.Never, false)

    is AppError.Domain.Validation -> UIErrorHint(RetryPolicy.Never, true)
    is AppError.Domain.Unauthorized -> UIErrorHint(RetryPolicy.Never, true)
    is AppError.Domain.Forbidden -> UIErrorHint(RetryPolicy.Never, true)
    is AppError.Domain.NotFound -> UIErrorHint(RetryPolicy.Never, false)

    is AppError.External.Service -> UIErrorHint(RetryPolicy.WithBackoff, true)

    is AppError.Storage.ReadFailed -> UIErrorHint(RetryPolicy.Never, false)
    is AppError.Storage.WriteFailed -> UIErrorHint(RetryPolicy.WithBackoff, true)
    is AppError.Storage.Corrupted -> UIErrorHint(RetryPolicy.Never, false)

    // The user has to wait or accept the situation: retrying cannot change the outcome
    is AppError.Social -> UIErrorHint(RetryPolicy.Never, true)

    // Every authentication and profile error needs the user to act and is never retried
    is AppError.Authentication,
    is AppError.Profile -> UIErrorHint(RetryPolicy.Never, true)
}

inline fun <Event> AppError.route(
    onConnectivity: () -> Event,
    onUserActionRequired: () -> Event,
    onOther: () -> Event
): Event = with(hint()) {
    when {
        retry != RetryPolicy.Never && !isUserActionable -> onConnectivity()
        isUserActionable -> onUserActionRequired()
        else -> onOther()
    }
}
