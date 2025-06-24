package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.session.data.dataSources.fakes.FakePreferencesDataStore
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSourceImpl.Companion.IS_CONNECTED_KEY
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.IOException

class SessionLocalDataSourceImplTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("setIsConnected") {
        test("Called with true, should update datastore with true") {
            val fakeDataStore = FakePreferencesDataStore()
            val dataSource = SessionLocalDataSourceImpl(fakeDataStore)

            runTest(dispatcher) {
                dataSource.setIsConnected(true)
                fakeDataStore.data.first()[IS_CONNECTED_KEY] shouldBe true
            }
        }

        test("Called with false, should update datastore with false") {
            val fakeDataStore = FakePreferencesDataStore()
            val dataSource = SessionLocalDataSourceImpl(fakeDataStore)

            runTest(dispatcher) {
                dataSource.setIsConnected(false)
                fakeDataStore.data.first()[IS_CONNECTED_KEY] shouldBe false
            }
        }
    }

    context("observeIsConnected") {
        test("Should emit true when IS_CONNECTED_KEY is true") {
            val prefs = preferencesOf(IS_CONNECTED_KEY to true)
            val fakeDataStore = FakePreferencesDataStore(prefs)
            val dataSource = SessionLocalDataSourceImpl(fakeDataStore)

            runTest(dispatcher) {
                dataSource.observeIsConnected().test {
                    awaitItem().shouldBeTrue()
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("Should emit false when IS_CONNECTED_KEY is false") {
            val prefs = preferencesOf(IS_CONNECTED_KEY to false)
            val fakeDataStore = FakePreferencesDataStore(prefs)
            val dataSource = SessionLocalDataSourceImpl(fakeDataStore)

            runTest(dispatcher) {
                dataSource.observeIsConnected().test {
                    awaitItem().shouldBeFalse()
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("Should emit false on IOException") {
            val throwingDataStore = object : DataStore<Preferences> {
                override val data: Flow<Preferences> = flow {
                    throw IOException("Simulated IO exception")
                }

                override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
                    error("Not needed")
                }
            }

            val dataSource = SessionLocalDataSourceImpl(throwingDataStore)

            runTest {
                dataSource.observeIsConnected().test {
                    awaitItem().shouldBeFalse()
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("Should rethrow non-IOException") {
            val throwingDataStore = object : DataStore<Preferences> {
                override val data: Flow<Preferences> = flow {
                    throw IllegalStateException("Boom!")
                }

                override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
                    error("Not needed")
                }
            }

            val dataSource = SessionLocalDataSourceImpl(throwingDataStore)

            runTest {
                shouldThrow<IllegalStateException> {
                    dataSource.observeIsConnected().first()
                }.message shouldBe "Boom!"
            }
        }
    }
})
