package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.AvatarStorageRepository
import dev.gitlive.firebase.storage.FirebaseStorage

class AvatarStorageRepositoryImpl(
    private val storage: FirebaseStorage,
    private val imageFileManager: ImageFileManager
) : AvatarStorageRepository {
    override suspend fun uploadAvatar(
        uid: String,
        localPath: String
    ): AppResult<String, AppError> {
        return try {
            val reference = storage.reference.child("avatars/$uid/avatar.jpg")
            val data = imageFileManager.readFileData(localPath)
                ?: return AppResult.Failure(AppError.Storage.ReadFailed)

            reference.putData(data)
            AppResult.Success(reference.getDownloadUrl())
        } catch (e: Exception) {
            Logger.e("Error in uploadingAvatar: ${e.message}")
            AppResult.Failure(AppError.External.Service("firebase-storage", e.message))
        }
    }
}
