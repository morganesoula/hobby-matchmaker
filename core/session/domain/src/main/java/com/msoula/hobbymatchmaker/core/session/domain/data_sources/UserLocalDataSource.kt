package com.msoula.hobbymatchmaker.core.session.domain.data_sources

import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel

interface UserLocalDataSource {
    suspend fun getUser(): UserDomainModel?
    suspend fun saveUser(user: UserDomainModel)
    suspend fun clearUser()
}
