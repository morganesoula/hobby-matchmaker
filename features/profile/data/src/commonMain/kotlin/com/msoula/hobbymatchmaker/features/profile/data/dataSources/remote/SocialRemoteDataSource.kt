package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface SocialRemoteDataSource {
    suspend fun searchUsersByPseudo(pseudo: String, currentUserUid: String? = null): AppResult<List<String>, AppError>
}
