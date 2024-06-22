package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.session.domain.data_sources.UserLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel

class UserRepository(private val userLocalDataSource: UserLocalDataSource) {
    suspend fun getUser(): UserDomainModel? =
        userLocalDataSource.getUser()

    suspend fun saveUser(user: UserDomainModel) =
        userLocalDataSource.saveUser(user)

    suspend fun clearUser() =
        userLocalDataSource.clearUser()
}
