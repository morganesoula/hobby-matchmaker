package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class SessionRemoteDataSourceImplTest: FunSpec({
    val dispatcher = StandardTestDispatcher()

    val mockFirebaseFirestore = mockk<FirebaseFirestore>()
    val mockCollectionRef = mockk<CollectionReference>()
    val mockDocumentRef = mockk<DocumentReference>()
    val mockDocumentSnapshot = mockk<DocumentSnapshot>()

    val user = SessionUserDomainModel(
        uid = "user123",
        email = "test@example.com"
    )

    val dataSource = SessionRemoteDataSourceImpl(mockFirebaseFirestore)

    beforeTest {
        clearAllMocks()
    }

    context("createUser") {
        test("Should return Success(true) when user already exists") {
            coEvery { mockFirebaseFirestore.collection("users") } returns mockCollectionRef
            coEvery { mockCollectionRef.document("user123") } returns mockDocumentRef
            coEvery { mockDocumentRef.get() } returns mockDocumentSnapshot
            every { mockDocumentSnapshot.exists } returns true

            runTest(dispatcher) {
                val result = dataSource.createUser(user)

                result shouldBe Result.Success(true)
            }
        }

        test("Should save user when user does not exist, and return Success(true)") {
            val expectedMap = mapOf(
                "uid" to "user123",
                "email" to "test@example.com"
            )

            coEvery { mockFirebaseFirestore.collection("users") } returns mockCollectionRef
            coEvery { mockCollectionRef.document("user123") } returns mockDocumentRef
            coEvery { mockDocumentRef.get() } returns mockDocumentSnapshot
            every { mockDocumentSnapshot.exists } returns false
            coEvery { mockDocumentRef.set(expectedMap) } just Runs

            runTest(dispatcher) {
                val result = dataSource.createUser(user)

                result shouldBe Result.Success(true)
                coVerify(exactly = 1) { mockDocumentRef.set(expectedMap) }
            }
        }
    }
})
