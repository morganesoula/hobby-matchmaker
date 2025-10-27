package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

sealed class ObserveCurrentUserProfileSuccess {
    data class Success(val userProfile: UserProfileDomainModel) : ObserveCurrentUserProfileSuccess()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCurrentUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository,
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<AppResult<ObserveCurrentUserProfileSuccess, AppError>> =
        userProfileRepository.observeCurrentUserProfile()
            .distinctUntilChanged()
            .mapLatest { data ->
                AppResult.Success(ObserveCurrentUserProfileSuccess.Success(data))
            }
            //.catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)
}
