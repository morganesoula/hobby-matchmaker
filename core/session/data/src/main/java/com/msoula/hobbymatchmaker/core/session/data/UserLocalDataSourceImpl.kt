package com.msoula.hobbymatchmaker.core.session.data

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import com.msoula.hobbymatchmaker.core.session.data.models.mappers.toUserDataStore
import com.msoula.hobbymatchmaker.core.session.data.models.mappers.toUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.UserLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel
import kotlinx.coroutines.flow.firstOrNull

class UserLocalDataSourceImpl(private val dataStore: AuthenticationDataStore) :
    UserLocalDataSource {

    override suspend fun getUser(): UserDomainModel? =
        dataStore.user.firstOrNull()?.toUserDomainModel()

    override suspend fun saveUser(user: UserDomainModel) {
        dataStore.saveUser(user.toUserDataStore())
    }

    override suspend fun clearUser() {
        dataStore.clearUser()
    }
}
