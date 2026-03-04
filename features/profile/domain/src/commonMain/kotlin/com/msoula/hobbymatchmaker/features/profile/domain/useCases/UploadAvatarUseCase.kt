package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.AvatarStorageRepository

class UploadAvatarUseCase(
    private val avatarStorageRepository: AvatarStorageRepository
) {
    suspend operator fun invoke(uid: String, localPath: String): AppResult<String, AppError> =
        avatarStorageRepository.uploadAvatar(uid, localPath)
}
