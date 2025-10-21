package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.IOException

class SessionRemoteDataSourceImplTest : FunSpec({

    lateinit var firestore: FirebaseFirestore
    lateinit var collection: CollectionReference
    lateinit var document: DocumentReference
    lateinit var snapshot: DocumentSnapshot
    lateinit var dataSource: SessionRemoteDataSource

    beforeTest {
        MockKAnnotations.init(this)

        firestore = mockk()
        collection = mockk()
        document = mockk()
        snapshot = mockk()

        dataSource = SessionRemoteDataSourceImpl(firestore)
    }

    context("when user does not exist") {
        test("creates the document and returns Success(Unit)") {
            runTest {
                val user = UserFireStoreModel(uid = "u1", email = "u1@acme.io")

                every { firestore.collection("users") } returns collection
                every { collection.document(user.uid) } returns document
                coEvery { document.get() } returns snapshot
                every { snapshot.exists } returns false

                coEvery { document.set(user) } returns Unit

                val res = dataSource.createUser(user)

                res.shouldBeInstanceOf<AppResult.Success<Unit>>()
                coVerify(exactly = 1) { document.set(user) }
            }
        }
    }

    context("when user already exists") {
        test("does not write and returns Success(Unit)") {
            runTest {
                val user = UserFireStoreModel(uid = "u2", email = "u2@acme.io")

                every { firestore.collection("users") } returns collection
                every { collection.document(user.uid) } returns document
                coEvery { document.get() } returns snapshot
                every { snapshot.exists } returns true // déjà présent

                val res = dataSource.createUser(user)

                res.shouldBeInstanceOf<AppResult.Success<Unit>>()
                coVerify(exactly = 0) { document.set(user) }
            }
        }
    }

    context("when get() fails") {
        test("maps error via toFirebaseError() and returns Failure(Network.Unreachable)") {
            runTest {
                val user = UserFireStoreModel(uid = "u3", email = "u3@acme.io")

                every { firestore.collection("users") } returns collection
                every { collection.document(user.uid) } returns document
                coEvery { document.get() } throws IOException("network down")

                val res = dataSource.createUser(user)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Unreachable
            }
        }
    }

    context("when set() fails") {
        test("maps error via toFirebaseError() and returns Failure(Network.Unreachable)") {
            runTest {
                val user = UserFireStoreModel(uid = "u4", email = "u4@acme.io")

                every { firestore.collection("users") } returns collection
                every { collection.document(user.uid) } returns document
                coEvery { document.get() } returns snapshot
                every { snapshot.exists } returns false

                coEvery { document.set(user) } throws IOException("disk/io")

                val res = dataSource.createUser(user)

                res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
                res.error shouldBe AppError.Network.Unreachable
            }
        }
    }
})
