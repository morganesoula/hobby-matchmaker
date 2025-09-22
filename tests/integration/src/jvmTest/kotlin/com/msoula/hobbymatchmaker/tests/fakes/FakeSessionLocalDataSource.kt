package com.msoula.hobbymatchmaker.tests.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionLocalDataSource : SessionLocalDataSource {
    private val isConnected = MutableStateFlow(false)
    private val shouldShowGuestDialog = MutableStateFlow(true)

    override suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError> {
        this.isConnected.value = isConnected
        return AppResult.Success(Unit)
    }

    override fun observeIsConnected(): Flow<Boolean> = isConnected

    override suspend fun setShouldShowGuestDialog(shouldShow: Boolean): AppResult<Unit, AppError> {
        shouldShowGuestDialog.value = shouldShow
        return AppResult.Success(Unit)
    }

    override fun observeShouldShowGuestDialog(): Flow<Boolean> = shouldShowGuestDialog
}
