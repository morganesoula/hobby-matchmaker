package com.msoula.hobbymatchmaker.tests.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel

class FakeSessionRemoteDataSource : SessionRemoteDataSource {
    var createUserResult: AppResult<Unit, AppError> = AppResult.Success(Unit)
    val created = mutableListOf<UserFireStoreModel>()

    override suspend fun createUser(user: UserFireStoreModel): AppResult<Unit, AppError> {
        created += user
        return createUserResult
    }
}
