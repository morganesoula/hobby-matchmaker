package com.msoula.hobbymatchmaker.core.authentication.domain.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSessionRepository : SessionRepository {

    val isConnectedFlow = MutableStateFlow(false)
    private var shouldCreateUserSucceed: Boolean = true

    override suspend fun setIsConnected(isConnected: Boolean) {
        isConnectedFlow.value = isConnected
    }

    override fun observeIsConnected(): Flow<Boolean> = isConnectedFlow.asStateFlow()

    override suspend fun createUser(user: SessionUserDomainModel): Result<Boolean, SessionErrors.CreateUserErrorHMM> {
        return if (shouldCreateUserSucceed) {
            Result.Success(true)
        } else {
            Result.Failure(SessionErrors.CreateUserErrorHMM.SaveErrorHMM("Failed to create user"))
        }
    }
}
