package com.msoula.hobbymatchmaker.core.design.util

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.authentication_already_exists
import com.msoula.hobbymatchmaker.core.design.authentication_invalid_credentials
import com.msoula.hobbymatchmaker.core.design.authentication_unknown
import com.msoula.hobbymatchmaker.core.design.external_service_error
import com.msoula.hobbymatchmaker.core.design.forbidden
import com.msoula.hobbymatchmaker.core.design.http_error_code
import com.msoula.hobbymatchmaker.core.design.network_timeout
import com.msoula.hobbymatchmaker.core.design.network_unreachable
import com.msoula.hobbymatchmaker.core.design.not_found
import com.msoula.hobbymatchmaker.core.design.request_canceled
import com.msoula.hobbymatchmaker.core.design.serialization_error
import com.msoula.hobbymatchmaker.core.design.storage_corrupted
import com.msoula.hobbymatchmaker.core.design.storage_read_failed
import com.msoula.hobbymatchmaker.core.design.storage_write_failed
import com.msoula.hobbymatchmaker.core.design.unauthorized
import com.msoula.hobbymatchmaker.core.design.unknown_error
import com.msoula.hobbymatchmaker.core.design.validation_error
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
        AppError.Network.Timeout -> UIText.Resource(Res.string.network_timeout)
        AppError.Network.Unreachable -> UIText.Resource(Res.string.network_unreachable)
        is AppError.Network.Http -> UIText.Resource(
            Res.string.http_error_code, listOf(error.code)
        )

        AppError.Network.Canceled -> UIText.Resource(Res.string.request_canceled)
        AppError.Network.Serialization -> UIText.Resource(
            Res.string.serialization_error
        )

        is AppError.Network.Unknown -> UIText.Resource(
            Res.string.unknown_error
        )

        is AppError.Domain.Validation -> UIText.Resource(
            Res.string.validation_error, listOf(error.reason)
        )

        AppError.Domain.Unauthorized -> UIText.Resource(
            Res.string.unauthorized
        )

        AppError.Domain.Forbidden -> UIText.Resource(
            Res.string.forbidden
        )

        AppError.Domain.NotFound -> UIText.Resource(
            Res.string.not_found
        )

        is AppError.External.Service -> UIText.Resource(
            Res.string.external_service_error, listOf(error.provider)
        )

        AppError.Storage.WriteFailed -> UIText.Resource(
            Res.string.storage_write_failed
        )

        AppError.Storage.ReadFailed -> UIText.Resource(
            Res.string.storage_read_failed
        )

        AppError.Storage.Corrupted -> UIText.Resource(
            Res.string.storage_corrupted
        )

        AppError.Authentication.AlreadyExists -> UIText.Resource(
            Res.string.authentication_already_exists
        )

        AppError.Authentication.Unknown -> UIText.Resource(
            Res.string.authentication_unknown
        )

        AppError.Authentication.InvalidCredentials -> UIText.Resource(
            Res.string.authentication_invalid_credentials
        )
    }
}
