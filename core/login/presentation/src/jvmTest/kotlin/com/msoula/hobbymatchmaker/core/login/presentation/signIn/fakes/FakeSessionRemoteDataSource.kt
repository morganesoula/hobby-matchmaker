package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel

class FakeSessionRemoteDataSource(
    private val createUserResult: Result<Boolean, SessionErrors.CreateUserError> =
        Result.Success(true)
) : SessionRemoteDataSource {
    override suspend fun createUser(user: SessionUserDomainModel):
        Result<Boolean, SessionErrors.CreateUserError> = createUserResult
}
