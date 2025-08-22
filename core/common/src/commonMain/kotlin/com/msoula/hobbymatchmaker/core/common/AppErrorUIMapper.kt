package com.msoula.hobbymatchmaker.core.common

import com.msoula.hobbymatchmaker.core.common.UIText.*
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
        AppError.Network.Timeout -> Resource(Res.string.network_timeout)
        AppError.Network.Unreachable -> Resource(Res.string.network_unreachable)
        is AppError.Network.Http -> Resource(
            Res.string.http_error_code, listOf(error.code)
        )

        AppError.Network.Canceled -> Resource(Res.string.request_canceled)
        AppError.Network.Serialization -> Resource(Res.string.serialization_error)
        is AppError.Network.Unknown -> Resource(Res.string.unknown_error)

        is AppError.Domain.Validation -> Resource(
            Res.string.validation_error, listOf(error.reason)
        )

        AppError.Domain.Unauthorized -> Resource(Res.string.unauthorized)
        AppError.Domain.Forbidden -> Resource(Res.string.forbidden)
        AppError.Domain.NotFound -> Resource(Res.string.not_found)

        is AppError.External.Service -> Resource(
            Res.string.external_service_error, listOf(error.provider)
        )

        AppError.Storage.WriteFailed -> Resource(Res.string.storage_write_failed)
        AppError.Storage.ReadFailed -> Resource(Res.string.storage_read_failed)
        AppError.Storage.Corrupted -> Resource(Res.string.storage_corrupted)

        AppError.Authentication.AlreadyExists -> Plain("")
        AppError.Authentication.Unknown -> Plain("")
    }
}
