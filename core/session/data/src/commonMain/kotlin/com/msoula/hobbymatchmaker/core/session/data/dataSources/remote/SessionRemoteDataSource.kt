package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel

interface SessionRemoteDataSource {
    suspend fun createUser(user: UserFireStoreModel): R<Unit, AppError>
}
