package com.msoula.hobbymatchmaker.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.presentation.models.LogOutState
import com.msoula.hobbymatchmaker.presentation.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AppViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val mockLogOutUseCase = mockk<LogOutUseCase>()
    private val mockObserveIsConnectedUseCase = mockk<ObserveIsConnectedUseCase>()
    private val mockStringResourcesProvider = mockk<StringResourcesProvider>()

    @Test
    fun `when logging fails, error is sent`() = runTest(testDispatcher) {
        val appViewModel = AppViewModel(
            mockLogOutUseCase,
            mockObserveIsConnectedUseCase,
            mockStringResourcesProvider
        )

        every { mockLogOutUseCase(any()) } returns flow {
            emit(
                Result.Failure(
                    LogOutError.UnknownError(
                        ""
                    )
                )
            )
        }

        every { mockStringResourcesProvider.getString(2131820965) } returns "Unknown error"

        appViewModel.logOut()

        appViewModel.logOutState.test {
            assert(awaitItem() == LogOutState.Idle)
            assert(awaitItem() == LogOutState.Error("Unknown error"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when logging out, success is sent`() = runTest(testDispatcher) {
        val appViewModel = AppViewModel(
            mockLogOutUseCase,
            mockObserveIsConnectedUseCase,
            mockStringResourcesProvider
        )

        every { mockLogOutUseCase(any()) } returns flow {
            emit(
                Result.Success(LogOutSuccess)
            )
        }

        appViewModel.logOut()

        appViewModel.logOutState.test {
            assert(awaitItem() == LogOutState.Idle)
            assert(awaitItem() == LogOutState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when user is connected, authentication state is connected`() = runTest(testDispatcher) {
        val appViewModel = AppViewModel(
            mockLogOutUseCase,
            mockObserveIsConnectedUseCase,
            mockStringResourcesProvider
        )

        every { mockObserveIsConnectedUseCase() } returns flow {
            emit(true)
        }

        appViewModel.authenticationState.test {
            assert(awaitItem() == AuthUiStateModel.CheckingState)
            assert(awaitItem() == AuthUiStateModel.IsConnected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when user is not connected, authentication state is not connected`() = runTest(testDispatcher) {
        val appViewModel = AppViewModel(
            mockLogOutUseCase,
            mockObserveIsConnectedUseCase,
            mockStringResourcesProvider
        )

        every { mockObserveIsConnectedUseCase() } returns flow {
            emit(false)
        }

        appViewModel.authenticationState.test {
            assert(awaitItem() == AuthUiStateModel.CheckingState)
            assert(awaitItem() == AuthUiStateModel.NotConnected)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
