package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface AuthenticationLocalDataSource {
    suspend fun clearUserData(): AppResult<Unit, AppError>
}
