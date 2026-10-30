package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.UserDataDAO

class AuthenticationLocalDataSourceImpl(
    private val userDataDAO: UserDataDAO
) : AuthenticationLocalDataSource {
    override suspend fun clearUserData(): AppResult<Unit, AppError> =
        safeCallStorage { userDataDAO.clearAllUserData() }
}
