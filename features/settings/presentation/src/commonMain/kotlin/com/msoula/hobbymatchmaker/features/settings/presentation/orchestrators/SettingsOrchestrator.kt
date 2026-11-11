package com.msoula.hobbymatchmaker.features.settings.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.DeleteAccountSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.DeleteAccountUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppInfo
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.settings.presentation.models.AppVersionUiModel
import kotlinx.coroutines.flow.first

class SettingsOrchestrator(
    private val logOutUseCase: LogOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val observeSessionState: ObserveSessionStateUseCase,
    private val observeCurrentUserProfileState: ObserveCurrentUserProfileStateUseCase,
    private val imageManager: ImageFileManager,
    private val appInfo: AppInfo
) {
    fun appVersion() = AppVersionUiModel(
        versionName = appInfo.versionName,
        buildNumber = appInfo.buildNumber
    )

    suspend fun logOut() = logOutUseCase()

    suspend fun deleteAccount(): AppResult<DeleteAccountSuccess, AppError> {
        // Read before the deletion: the profile is gone afterwards
        val avatarPath = currentAvatarPath()
        return deleteAccountUseCase().onSuccess { deleteLocalAvatar(avatarPath) }
    }

    private suspend fun currentAvatarPath(): String? {
        val uid = (observeSessionState().first() as? SessionState.Authenticated)?.uid ?: return null
        return observeCurrentUserProfileState(uid).first().avatarUrl
    }

    private fun deleteLocalAvatar(path: String?) {
        if (path != null && (path.startsWith("/data") || path.contains("/files/avatars"))) {
            imageManager.deleteImageFromInternalStorage(path)
        }
    }
}
