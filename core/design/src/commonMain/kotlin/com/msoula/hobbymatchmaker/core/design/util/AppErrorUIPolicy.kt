package com.msoula.hobbymatchmaker.core.design.util

import com.msoula.hobbymatchmaker.core.common.AppError

enum class RetryPolicy { Never, WithBackoff }

data class UIErrorHint(
    val retry: RetryPolicy = RetryPolicy.Never,
    val isUserActionable: Boolean = false
)

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

    AppError.Authentication.AlreadyExists -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Authentication.Unknown -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Authentication.InvalidCredentials -> UIErrorHint(RetryPolicy.Never, true)
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
