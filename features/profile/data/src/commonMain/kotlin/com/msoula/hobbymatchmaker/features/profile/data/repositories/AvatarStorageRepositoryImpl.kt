package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.AvatarStorageRepository
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlin.coroutines.cancellation.CancellationException

class AvatarStorageRepositoryImpl(
    private val storage: FirebaseStorage,
    private val imageFileManager: ImageFileManager,
    private val connectivityChecker: NetworkConnectivityChecker
) : AvatarStorageRepository {

    override suspend fun uploadAvatar(
        uid: String,
        localPath: String
    ): AppResult<String, AppError> {
        if (!connectivityChecker.hasActiveConnection()) {
            return AppResult.Failure(AppError.Network.Unreachable)
        }

        return try {
            val reference = storage.reference.child("avatars/$uid/avatar.jpg")
            val data = imageFileManager.readFileData(localPath)
                ?: return AppResult.Failure(AppError.Storage.ReadFailed)

            reference.putData(data)
            AppResult.Success(reference.getDownloadUrl())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e("Error in uploadingAvatar: ${e.message}")
            AppResult.Failure(e.toAvatarUploadError())
        }
    }

    // Firebase Storage reports rule rejections as a plain exception message
    private fun Exception.toAvatarUploadError(): AppError {
        val message = message.orEmpty()
        return when {
            PERMISSION_DENIED.containsMatchIn(message) -> AppError.Domain.Forbidden
            else -> AppError.External.Service("firebase-storage", message)
        }
    }

    private companion object {
        val PERMISSION_DENIED = Regex(
            "\\b403\\b|permission denied|not authorized|unauthorized",
            RegexOption.IGNORE_CASE
        )
    }
}
