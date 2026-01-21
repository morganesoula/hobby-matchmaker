package com.msoula.hobbymatchmaker.features.social.domain.di

import com.msoula.hobbymatchmaker.features.social.domain.useCases.AcceptInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CancelInvitationUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.DeclineInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveIncomingInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSentInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSocialCircleUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RemoveMemberUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SearchUsersByPseudoUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SendInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SocialUseCases
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleSocialDomain = module {
    factoryOf(::SearchUsersByPseudoUseCase)
    factoryOf(::AcceptInviteUseCase)
    factoryOf(::CancelInvitationUseCase)
    factoryOf(::DeclineInviteUseCase)
    factoryOf(::ObserveIncomingInvitesUseCase)
    factoryOf(::ObserveSentInvitesUseCase)
    factoryOf(::ObserveSocialCircleUseCase)
    factoryOf(::RemoveMemberUseCase)
    factoryOf(::SendInviteUseCase)
    factoryOf(::SocialUseCases)
}
