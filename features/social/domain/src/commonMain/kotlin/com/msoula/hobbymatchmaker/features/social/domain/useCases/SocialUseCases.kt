package com.msoula.hobbymatchmaker.features.social.domain.useCases

data class SocialUseCases(
    val searchUsersByPseudoUseCase: SearchUsersByPseudoUseCase,
    val observeSentInvitesUseCase: ObserveSentInvitesUseCase,
    val refreshIncomingInvitesUseCase: RefreshIncomingInvitesUseCase,
    val refreshSentInvitesUseCase: RefreshSentInvitesUseCase,
    val cancelInvitationUseCase: CancelInvitationUseCase,
    val observeIncomingInvitesUseCase: ObserveIncomingInvitesUseCase,
    val declineInviteUseCase: DeclineInviteUseCase,
    val acceptInviteUseCase: AcceptInviteUseCase,
    val sendInvitesUseCase: SendInviteUseCase,
    val checkSocialCircleLimitUseCase: CheckSocialCircleLimitUseCase
)
