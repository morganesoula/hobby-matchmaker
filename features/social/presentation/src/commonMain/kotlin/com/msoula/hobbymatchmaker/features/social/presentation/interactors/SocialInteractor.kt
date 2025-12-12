package com.msoula.hobbymatchmaker.features.social.presentation.interactors

import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.useCases.AcceptInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CancelInvitationUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.DeclineInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveIncomingInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSentInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SearchUsersByPseudoUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SendInviteUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SocialInteractor(
    private val searchUsersByPseudoUseCase: SearchUsersByPseudoUseCase,
    private val sendInviteUseCase: SendInviteUseCase,
    private val observeSentInvitesUseCase: ObserveSentInvitesUseCase,
    private val cancelInvitationUseCase: CancelInvitationUseCase,
    private val observeIncomingInvitesUseCase: ObserveIncomingInvitesUseCase,
    private val declineInviteUseCase: DeclineInviteUseCase,
    private val acceptInviteUseCase: AcceptInviteUseCase
) {
    suspend fun searchUsers(pseudo: String, ownerUid: String?) =
        searchUsersByPseudoUseCase(pseudo, ownerUid)

    @OptIn(ExperimentalTime::class)
    suspend fun sendInvite(fromUid: String, toPseudo: String, name: String?) =
        sendInviteUseCase(
            SocialInviteDomainModel(
                inviteId = "",
                fromUid = fromUid,
                fromPseudo = "",
                toPseudo = toPseudo,
                name = name,
                status = InviteStatus.PENDING,
                createdAt = Clock.System.now(),
                updatedAt = null
            )
        )

    fun observeSentInvites(ownerUid: String) = observeSentInvitesUseCase(ownerUid)
    fun observeIncomingInvites(ownerUid: String) = observeIncomingInvitesUseCase(ownerUid)

    suspend fun cancelInvite(inviteId: String) = cancelInvitationUseCase(inviteId)
    suspend fun declineInvite(inviteId: String) = declineInviteUseCase(inviteId)
    suspend fun acceptInvite(
        inviteId: String,
        ownerUid: String,
        guestUid: String
    ) = acceptInviteUseCase(
        inviteId,
        ownerUid,
        guestUid
    )
}
