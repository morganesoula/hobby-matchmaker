package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

sealed class ObserveSentInvitesSuccess {
    data class Success(val invites: List<SocialInviteDomainModel>) : ObserveSentInvitesSuccess()
    data object Empty : ObserveSentInvitesSuccess()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSentInvitesUseCase(
    private val socialRepository: SocialRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(ownerUid: String): Flow<AppResult<ObserveSentInvitesSuccess, AppError>> =
        socialRepository.observeSentInvites(ownerUid)
            .distinctUntilChanged()
            .mapLatest<List<SocialInviteDomainModel>, AppResult<ObserveSentInvitesSuccess, AppError>> { list ->
                if (list.isEmpty()) {
                    AppResult.Success(ObserveSentInvitesSuccess.Empty)
                } else {
                    AppResult.Success(ObserveSentInvitesSuccess.Success(list))
                }
            }
            .catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)
}
