package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.repositories.SocialRepository

class SearchUsersByPseudoUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(pseudo: String) = socialRepository.searchUsersByPseudo(pseudo)
}
