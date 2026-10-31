package com.msoula.hobbymatchmaker.core.login.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LinkInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import kotlinx.coroutines.flow.Flow

class SignInOrchestrator(
    private val authFormValidationUseCases: LoginValidateFormUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val linkInWithCredentialUseCase: LinkInWithCredentialUseCase,
    private val sessionManager: SessionManager,
    private val authenticationManager: AuthenticationManager
) {

    fun validateCredentials(email: String, password: String): Boolean =
        authFormValidationUseCases.validateEmail(email).successful &&
            authFormValidationUseCases.validatePassword(password).successful

    fun validateEmail(email: String): Boolean =
        authFormValidationUseCases.validateEmail(email).successful

    suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
        resetPasswordUseCase(Parameters.StringParam(email))

    suspend fun signInEmail(email: String, password: String): AppResult<Boolean, AppError> =
        authenticationManager(
            AuthenticationManager.Params.EmailPassword(email, password)
        ).mapSuccess { it.needsPseudo }

    suspend fun signInSocial(
        provider: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<Boolean, AppError> =
        authenticationManager(
            AuthenticationManager.Params.SocialProvider(
                provider, credentialProvider
            )
        ).mapSuccess { it.needsPseudo }

    suspend fun linkCredential(
        provider: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<Unit, AppError> =
        linkInWithCredentialUseCase(provider, credentialProvider).mapSuccess { }

    suspend fun signInAsGuest(): AppResult<Boolean, AppError> =
        sessionManager.setCurrentUserProfileUuid().mapSuccess { false }

    fun observeDontAsk(): Flow<Boolean> =
        sessionManager.observeDontAskCheckboxValue()

    suspend fun setDontAsk(value: Boolean) =
        sessionManager.setDontAskGuestDialog(value)
}
