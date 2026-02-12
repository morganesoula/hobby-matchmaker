package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.CreateDefaultUserProfileUseCase

data object SignUpSuccess
class SignUpUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createDefaultUserProfileUseCase: CreateDefaultUserProfileUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    suspend operator fun invoke(p: Parameters.DoubleStringParam): AppResult<SignUpSuccess, AppError> =
        authenticationRepository.signUp(p.firstValue, p.secondValue)
            .flatMapSuspend { uid ->
                createUserUseCase(SessionUserDomainModel(uid, p.firstValue))
                    .flatMapSuspend {
                        createDefaultUserProfileUseCase(uid = uid, name = "")
                    }
                    .flatMapSuspend {
                        setIsConnectedUseCase(true).mapSuccess {
                            SignUpSuccess
                        }
                    }
            }
            .mapSuccess { SignUpSuccess }
}
