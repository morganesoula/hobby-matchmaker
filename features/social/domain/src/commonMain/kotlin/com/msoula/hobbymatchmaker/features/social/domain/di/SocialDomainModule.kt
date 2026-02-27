package com.msoula.hobbymatchmaker.features.social.domain.di

import com.msoula.hobbymatchmaker.features.social.domain.useCases.AcceptInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CancelInvitationUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckMovieMatchUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckSocialCircleLimitUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ComputeCommonMoviesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.DeclineInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.GetSharedMovieIdsUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.GetUserAvatarUrlUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveIncomingInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSentInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSocialCircleUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RefreshIncomingInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RefreshSentInvitesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RefreshSocialCircleUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RemoveMemberUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SearchUsersByPseudoUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SendInviteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SocialUseCases
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SyncFavoriteToCircleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleSocialDomain = module {
    factoryOf(::SearchUsersByPseudoUseCase)
    factoryOf(::AcceptInviteUseCase)
    factoryOf(::CancelInvitationUseCase)
    factoryOf(::DeclineInviteUseCase)
    factoryOf(::ObserveIncomingInvitesUseCase)
    factoryOf(::RefreshIncomingInvitesUseCase)
    factoryOf(::ObserveSentInvitesUseCase)
    factoryOf(::RefreshSentInvitesUseCase)
    factoryOf(::ObserveSocialCircleUseCase)
    factoryOf(::RefreshSocialCircleUseCase)
    factoryOf(::RemoveMemberUseCase)
    factoryOf(::SendInviteUseCase)
    factoryOf(::CheckSocialCircleLimitUseCase)
    factoryOf(::SocialUseCases)
    factoryOf(::CheckMovieMatchUseCase)
    factoryOf(::GetUserAvatarUrlUseCase)
    factoryOf(::SyncFavoriteToCircleUseCase)
    factoryOf(::GetSharedMovieIdsUseCase)
    factoryOf(::ComputeCommonMoviesUseCase)
}
