package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import com.msoula.hobbymatchmaker.features.profile.domain.validators.PseudoValidator
import kotlinx.coroutines.flow.first

class ReservePseudoUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(uid: String, pseudo: String, name: String): AppResult<Unit, AppError> {
        val trimmedPseudo = pseudo.trim()
        val trimmedName = name.trim()
        val errors = PseudoValidator.validate(trimmedPseudo)
        if (errors.isNotEmpty()) return AppResult.Failure(AppError.Domain.Validation(errors.toString()))

        return userProfileRepository.reservePseudo(uid, trimmedPseudo, trimmedName).flatMap {
            val localProfile = userProfileRepository.observeCurrentUserProfile(uid).first()
            userProfileRepository.upsertUserProfile(
                UserProfileDomainModel(
                    uid = uid,
                    name = trimmedName,
                    pseudo = trimmedPseudo,
                    avatarUrl = localProfile?.avatarUrl,
                    bio = localProfile?.bio,
                    interests = localProfile?.interests ?: emptyList(),
                    likedMoviesCount = localProfile?.likedMoviesCount ?: 0
                )
            )
        }
    }
}
