package com.msoula.hobbymatchmaker.core.common

import org.jetbrains.compose.resources.StringResource

sealed interface UIText {
    data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UIText
    data class Plain(val value: String) : UIText
}

interface ErrorMessageMapper {
    fun toUIText(error: AppError): UIText
}

object DefaultErrorMessageMapper : ErrorMessageMapper {
    override fun toUIText(error: AppError): UIText = when (error) {
        is AppError.Network.Timeout -> UIText.Resource(Res.string.network_timeout)
        is AppError.Network.Unreachable -> UIText.Resource(Res.string.network_unreachable)
        is AppError.Network.Http -> UIText.Resource(
            Res.string.http_error_code, listOf(error.code)
        )

        is AppError.Network.Canceled -> UIText.Resource(Res.string.request_canceled)
        is AppError.Network.Serialization -> UIText.Resource(Res.string.serialization_error)
        is AppError.Network.Unknown -> UIText.Resource(Res.string.unknown_error)

        is AppError.Domain.Validation -> UIText.Resource(
            Res.string.validation_error, listOf(error.reason)
        )

        is AppError.Domain.Unauthorized -> UIText.Resource(Res.string.unauthorized)
        is AppError.Domain.Forbidden -> UIText.Resource(Res.string.forbidden)
        is AppError.Domain.NotFound -> UIText.Resource(Res.string.not_found)

        is AppError.External.Service -> UIText.Resource(
            Res.string.external_service_error, listOf(error.provider)
        )

        is AppError.Storage.WriteFailed -> UIText.Plain("")
        is AppError.Storage.ReadFailed -> UIText.Plain("")
        is AppError.Storage.Corrupted -> UIText.Plain("")
    }
}
