package com.msoula.hobbymatchmaker.core.session.domain.data_sources

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel

interface SessionRemoteDataSource {
    suspend fun createUser(user: SessionUserDomainModel): Result<Boolean>
}
