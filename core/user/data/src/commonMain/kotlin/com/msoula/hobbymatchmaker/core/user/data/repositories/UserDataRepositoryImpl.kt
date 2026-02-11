package com.msoula.hobbymatchmaker.core.user.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.user.data.dataSources.local.UserLocalDataSource
import com.msoula.hobbymatchmaker.core.user.data.dataSources.remote.UserRemoteDataSource
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.core.user.domain.repositories.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDataRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    dispatcherProvider: DispatcherProvider
) : UserDataRepository {

    private val customScope = dispatcherProvider.createScope()

    private val cache = MutableStateFlow<Map<String, UserSummaryDomainModel>>(emptyMap())
    private val activeObservations = mutableSetOf<String>()

    override fun observeUser(uid: String): Flow<UserSummaryDomainModel?> =
        observeUsers(listOf(uid)).map { it[uid] }

    override fun observeUsers(uids: List<String>): Flow<Map<String, UserSummaryDomainModel>> {
        if (uids.isEmpty()) return flowOf(emptyMap())

        val newUids = uids.filter { it !in activeObservations }
        newUids.forEach { uid ->
            activeObservations.add(uid)
            startRemoteObservation(uid)
        }

        return combine(
            cache,
            userLocalDataSource.observeUsers(uids)
        ) { memory, local ->
            val result = mutableMapOf<String, UserSummaryDomainModel>()
            uids.forEach { uid ->
                memory[uid]?.let { result[uid] = it }
                    ?: local.firstOrNull { it?.uid == uid }?.let { result[uid] = it }
            }

            result
        }
    }

    override suspend fun getUser(uid: String): AppResult<UserSummaryDomainModel, AppError> {
        cache.value[uid]?.let { return AppResult.Success(it) }

        userLocalDataSource.getUser(uid)?.let { user ->
            updateCache(user)
            return AppResult.Success(user)
        }

        return userRemoteDataSource.getUser(uid)
            .flatMap { user ->
                user
                    ?.also {
                        updateCache(it)
                        userLocalDataSource.upsertUser(it)
                    }
                    ?.let { AppResult.Success(it) }
                    ?: AppResult.Failure(AppError.Domain.NotFound)
            }
    }

    override suspend fun getUsers(uids: List<String>): Map<String, UserSummaryDomainModel> {
        if (uids.isEmpty()) return emptyMap()

        val result = mutableMapOf<String, UserSummaryDomainModel>()
        val missing = mutableListOf<String>()

        uids.forEach { uid ->
            cache.value[uid]?.let { result[uid] = it } ?: missing.add(uid)
        }

        if (missing.isEmpty()) return result

        val stillMissing = mutableListOf<String>()
        val localUsers = userLocalDataSource.getUsers(missing)

        missing.forEach { uid ->
            localUsers.firstOrNull { it?.uid == uid }?.let { user ->
                result[uid] = user
                updateCache(user)
            } ?: stillMissing.add(uid)
        }

        if (stillMissing.isEmpty()) return result

        stillMissing.chunked(30).forEach { chunk ->
            val remoteUsers = userRemoteDataSource.getUsers(chunk)
            remoteUsers.forEach { (uid, user) ->
                result[uid] = user
                updateCache(user)
            }

            customScope.launch {
                userLocalDataSource.upsertUsers(remoteUsers.values.toList())
            }
        }

        return result
    }

    override suspend fun prefetchUsers(uids: List<String>) {
        customScope.launch {
            getUsers(uids)
        }
    }

    override suspend fun invalidateCache(uid: String) {
        cache.update { it - uid }
        userLocalDataSource.deleteUser(uid)
    }

    private fun startRemoteObservation(uid: String) {
        customScope.launch {
            userRemoteDataSource.observeUser(uid).collect { user ->
                user?.let {
                    updateCache(it)
                    userLocalDataSource.upsertUser(it)
                }
            }
        }
    }

    private fun updateCache(user: UserSummaryDomainModel) {
        cache.update { current -> current + (user.uid to user) }
    }
}
