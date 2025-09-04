package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel

interface SessionRemoteDataSource {
    suspend fun createUser(user: UserFireStoreModel): AppResult<Unit, AppError>
}
