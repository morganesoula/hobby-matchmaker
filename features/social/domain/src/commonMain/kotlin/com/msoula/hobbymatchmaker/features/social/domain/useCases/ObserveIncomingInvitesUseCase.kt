package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

sealed class ObserveIncomingInvitesSuccess {
    data class Success(val invites: List<SocialInviteDomainModel>) : ObserveIncomingInvitesSuccess()
    data object Empty : ObserveIncomingInvitesSuccess()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveIncomingInvitesUseCase(
    private val socialRepository: SocialRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(ownerUid: String) =
        socialRepository.observeIncomingInvites(ownerUid)
            .distinctUntilChanged()
            .mapLatest<List<SocialInviteDomainModel>, AppResult<ObserveIncomingInvitesSuccess, AppError>> { list ->
                if (list.isEmpty()) {
                    AppResult.Success(ObserveIncomingInvitesSuccess.Empty)
                } else {
                    AppResult.Success(ObserveIncomingInvitesSuccess.Success(list))
                }
            }
            .catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)
}
