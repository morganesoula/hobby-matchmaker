package com.msoula.hobbymatchmaker.features.social.presentation.interactors

import com.msoula.hobbymatchmaker.features.social.domain.useCases.SearchUsersByPseudoUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SendInviteUseCase

class SocialInteractor(
    private val searchUsersByPseudoUseCase: SearchUsersByPseudoUseCase,
    private val sendInviteUseCase: SendInviteUseCase
) {
    suspend fun searchUsers(pseudo: String, ownerUid: String?) =
        searchUsersByPseudoUseCase(pseudo, ownerUid)

    suspend fun sendInvite(fromUid: String, toUid: String) =
        sendInviteUseCase(fromUid, toUid)
}
