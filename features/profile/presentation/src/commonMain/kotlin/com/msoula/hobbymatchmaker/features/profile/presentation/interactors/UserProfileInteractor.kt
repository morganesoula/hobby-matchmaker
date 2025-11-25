package com.msoula.hobbymatchmaker.features.profile.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.CheckIfPseudoIsAvailable
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpsertUserProfileUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserProfileInteractor(
    /* private val observeSession: ObserveSessionStateUseCase,
    private val observeProfile: ObserveCurrentUserProfileStateUseCase,*/
    private val checkPseudo: CheckIfPseudoIsAvailable,
    private val upsertUser: UpsertUserProfileUseCase,
    private val imageManager: ImageFileManager,
    private val logOutUseCase: LogOutUseCase
) {

    suspend fun checkPseudoAvailable(pseudo: String) = checkPseudo(pseudo)

    suspend fun saveProfile(uid: String, profile: UserProfileDomainModel) =
        upsertUser(profile.copy(uid = uid))

    @OptIn(ExperimentalTime::class)
    suspend fun saveAvatar(
        uid: String,
        oldPath: String?,
        newPath: String
    ): AppResult<String, AppError> {
        val fileName = "avatar_${uid}_${Clock.System.now()}.jpg"

        val internalPath =
            imageManager.copyImageToInternalStorage(newPath, fileName)
                ?: return AppResult.Failure(AppError.Storage.WriteFailed)

        if (oldPath != null &&
            (oldPath.startsWith("/data/") ||
                oldPath.contains("/files/avatars"))
        ) {
            imageManager.deleteImageFromInternalStorage(oldPath)
        }

        return AppResult.Success(internalPath)
    }

    suspend fun logOut() = logOutUseCase()
}
