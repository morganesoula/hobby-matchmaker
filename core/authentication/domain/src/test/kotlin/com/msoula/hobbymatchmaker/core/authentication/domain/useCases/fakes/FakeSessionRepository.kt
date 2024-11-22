package com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSessionRepository : SessionRepository {

    private var localIsConnected: Boolean = true

    override suspend fun setIsConnected(isConnected: Boolean) {
        localIsConnected = isConnected
    }

    override fun observeIsConnected(): Flow<Boolean> {
        return flow {
            emit(localIsConnected)
        }
    }

    override suspend fun createUser(user: SessionUserDomainModel): Result<Boolean, SessionErrors.CreateUserError> {
        TODO("Not yet implemented")
    }
}
