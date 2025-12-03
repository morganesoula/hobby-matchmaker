package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class SearchUsersByPseudoUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(pseudo: String, ownerUid: String? = null) =
        socialRepository.searchUsersByPseudo(pseudo, ownerUid)
}
