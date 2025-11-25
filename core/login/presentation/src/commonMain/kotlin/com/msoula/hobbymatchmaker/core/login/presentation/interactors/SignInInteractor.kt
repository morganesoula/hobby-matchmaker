package com.msoula.hobbymatchmaker.core.login.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import kotlinx.coroutines.flow.Flow

class SignInInteractor(
    val authFormValidationUseCases: LoginValidateFormUseCase,
    val resetPasswordUseCase: ResetPasswordUseCase,
    val sessionManager: SessionManager,
    val authenticationManager: AuthenticationManager
) {

    fun validateCredentials(email: String, password: String): Boolean =
        authFormValidationUseCases.validateEmail(email).successful &&
            authFormValidationUseCases.validatePassword(password).successful

    suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
        resetPasswordUseCase(Parameters.StringParam(email))

    suspend fun signInEmail(email: String, password: String): AppResult<Unit, AppError> =
        authenticationManager(
            AuthenticationManager.Params.EmailPassword(email, password)
        ).mapSuccess { Unit }

    suspend fun signInSocial(
        provider: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<Unit, AppError> =
        authenticationManager(
            AuthenticationManager.Params.SocialProvider(
                provider, credentialProvider
            )
        ).mapSuccess { Unit }

    suspend fun signInAsGuest(): AppResult<Unit, AppError> =
        sessionManager.setCurrentUserProfileUuid().mapSuccess { Unit }

    fun observeDontAsk(): Flow<Boolean> =
        sessionManager.observeDontAskCheckboxValue()

    suspend fun setDontAsk(value: Boolean) =
        sessionManager.setDontAskGuestDialog(value)
}
