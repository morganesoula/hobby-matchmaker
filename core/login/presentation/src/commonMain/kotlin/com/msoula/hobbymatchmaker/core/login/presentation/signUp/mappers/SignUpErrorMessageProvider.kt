package com.msoula.hobbymatchmaker.core.login.presentation.signUp.mappers

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.HMMAppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.connection_issue
import com.msoula.hobbymatchmaker.core.login.presentation.email_already_exists_error
import com.msoula.hobbymatchmaker.core.login.presentation.internal_error
import com.msoula.hobbymatchmaker.core.login.presentation.too_many_requests_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_disabled_error
import org.jetbrains.compose.resources.getString

class SignUpErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: HMMAppError): String {
        Logger.e("Into SignUpEMP with error: $error")
        return when (error) {
            is SignUpErrors.EmailAlreadyExists ->
                getString(Res.string.email_already_exists_error)

            is SignUpErrors.UserDisabled ->
                getString(Res.string.user_disabled_error)

            is SignUpErrors.TooManyRequests ->
                getString(Res.string.too_many_requests_error)

            is SignUpErrors.InternalErrorHMM ->
                getString(Res.string.internal_error)

            is SignUpErrors.Connection ->
                getString(Res.string.connection_issue)

            else -> ""
        }
    }
}
