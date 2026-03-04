package com.msoula.hobbymatchmaker.features.profile.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface AvatarStorageRepository {
    suspend fun uploadAvatar(uid: String, localPath: String): AppResult<String, AppError>
}
