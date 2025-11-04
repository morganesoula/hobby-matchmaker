package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.RemoteAuthUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.UserInfo
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.IOException

class AuthenticationRemoteDataSourceImplTest : FunSpec({
    var auth: FirebaseAuth = mockk(relaxed = true)
    var firestore: FirebaseFirestore = mockk(relaxed = true)
    var authManager: AuthManager = mockk(relaxed = true)
    lateinit var dataSource: AuthenticationRemoteDataSource

    var collection: CollectionReference = mockk(relaxed = true)
    var document: DocumentReference = mockk(relaxed = true)
    var snapshot: DocumentSnapshot = mockk(relaxed = true)

    var user: FirebaseUser = mockk(relaxed = true)
    var user2: FirebaseUser = mockk(relaxed = true)
    var authResult: AuthResult = mockk(relaxed = true)
    var credential: AuthCredential = mockk(relaxed = true)

    beforeTest {
        MockKAnnotations.init(this)

        dataSource = AuthenticationRemoteDataSourceImpl(
            auth, firestore, authManager
        )
    }

    test("authenticationSignOut forwards to AuthManager and returns its result") {
        runTest {
            coEvery { authManager.signOut() } returns AppResult.Success(Unit)

            val res = dataSource.authenticationSignOut()

            res.shouldBeInstanceOf<AppResult.Success<Unit>>()
            coVerify(exactly = 1) { authManager.signOut() }
        }
    }

    test("signInWithCredentials forwards to AuthManager.signIn(provider, credential)") {
        runTest {
            val expected = RemoteAuthUser(
                uid = "u123",
                email = "e@acme.io",
                providers = listOf("google.com")
            )
            coEvery {
                authManager.signIn(
                    ProviderType.GOOGLE,
                    credential
                )
            } returns AppResult.Success(expected)

            val res = dataSource.signInWithCredentials(credential, ProviderType.GOOGLE)

            res.shouldBeInstanceOf<AppResult.Success<RemoteAuthUser?>>()
            res.data shouldBe expected
            coVerify(exactly = 1) { authManager.signIn(ProviderType.GOOGLE, credential) }
        }
    }

    test("linkWithCredential returns Unauthorized when currentUser is null") {
        runTest {
            every { auth.currentUser } returns null

            val res = dataSource.linkWithCredential(credential)

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Domain.Unauthorized
            coVerify(exactly = 0) { user.linkWithCredential(any()) }
        }
    }

    test("linkWithCredential links on current user and maps to AuthFirebaseUser") {
        runTest {
            every { auth.currentUser } returns user
            coEvery { user.linkWithCredential(credential) } returns authResult
            every { authResult.user } returns user2
            every { user2.uid } returns "u42"
            every { user2.email } returns "x@acme.io"
            val infoGoogle = mockk<UserInfo> { every { providerId } returns "google.com" }
            val infoFirebase = mockk<UserInfo> { every { providerId } returns "firebase" } // filtré
            every { user2.providerData } returns listOf(infoFirebase, infoGoogle)

            val res = dataSource.linkWithCredential(credential)

            res.shouldBeInstanceOf<AppResult.Success<RemoteAuthUser?>>()
            val data = res.data!!
            data.uid shouldBe "u42"
            data.email shouldBe "x@acme.io"
            data.providers.shouldContainExactly(listOf("google.com")) // "firebase" filtré
            coVerify(exactly = 1) { user.linkWithCredential(credential) }
        }
    }

    test("createUserWithEmailAndPassword returns Success(uid) when user present") {
        runTest {
            coEvery { auth.createUserWithEmailAndPassword("a@b.com", "pwd") } returns authResult
            every { authResult.user } returns user
            every { user.uid } returns "uid-1"

            val res = dataSource.createUserWithEmailAndPassword("a@b.com", "pwd")

            res.shouldBeInstanceOf<AppResult.Success<String>>()
            res.data shouldBe "uid-1"
        }
    }

    test("createUserWithEmailAndPassword with missing user maps IllegalStateException to Authentication.Unknown") {
        runTest {
            coEvery { auth.createUserWithEmailAndPassword(any(), any()) } returns authResult
            every { authResult.user } returns null

            val res = dataSource.createUserWithEmailAndPassword("a@b.com", "pwd")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Authentication.Unknown
        }
    }

    test("signInWithEmailAndPassword returns Success(uid) when user present") {
        runTest {
            coEvery { auth.signInWithEmailAndPassword("a@b.com", "pwd") } returns authResult
            every { authResult.user } returns user
            every { user.uid } returns "uid-2"

            val res = dataSource.signInWithEmailAndPassword("a@b.com", "pwd")

            res.shouldBeInstanceOf<AppResult.Success<String>>()
            res.data shouldBe "uid-2"
        }
    }

    test("signInWithEmailAndPassword with missing user maps IllegalStateException to Authentication.Unknown") {
        runTest {
            coEvery { auth.signInWithEmailAndPassword(any(), any()) } returns authResult
            every { authResult.user } returns null

            val res = dataSource.signInWithEmailAndPassword("a@b.com", "pwd")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Authentication.Unknown
        }
    }

    test("resetPassword success returns Success(Unit)") {
        runTest {
            coEvery { auth.sendPasswordResetEmail("u@acme.io") } returns Unit

            val res = dataSource.resetPassword("u@acme.io")

            res.shouldBeInstanceOf<AppResult.Success<Unit>>()
        }
    }

    test("resetPassword IOException maps to Network.Unreachable") {
        runTest {
            coEvery { auth.sendPasswordResetEmail(any()) } throws IOException("net")

            val res = dataSource.resetPassword("x@y.z")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Network.Unreachable
        }
    }

    test("getUserUid returns null when no currentUser") {
        runTest {
            every { auth.currentUser } returns null

            val uid = dataSource.getUserUid()

            uid.shouldBeNull()
        }
    }

    test("getUserUid returns currentUser.uid when present") {
        runTest {
            every { auth.currentUser } returns user
            every { user.uid } returns "u-777"

            val uid = dataSource.getUserUid()

            uid shouldBe "u-777"
        }
    }

    test("isFirstSignIn returns Success(true) when uid is blank") {
        runTest {
            val res = dataSource.isFirstSignIn("")

            res.shouldBeInstanceOf<AppResult.Success<Boolean>>()
            res.data shouldBe true
        }
    }

    test("isFirstSignIn checks Firestore: exists=false => true") {
        runTest {
            every { firestore.collection("users") } returns collection
            every { collection.document("u1") } returns document
            coEvery { document.get() } returns snapshot
            every { snapshot.exists } returns false

            val res = dataSource.isFirstSignIn("u1")

            res.shouldBeInstanceOf<AppResult.Success<Boolean>>()
            res.data shouldBe true
        }
    }

    test("isFirstSignIn checks Firestore: exists=true => false") {
        runTest {
            every { firestore.collection("users") } returns collection
            every { collection.document("u2") } returns document
            coEvery { document.get() } returns snapshot
            every { snapshot.exists } returns true

            val res = dataSource.isFirstSignIn("u2")

            res.shouldBeInstanceOf<AppResult.Success<Boolean>>()
            res.data shouldBe false
        }
    }

    test("isFirstSignIn maps Firestore get() IOException to Network.Unreachable") {
        runTest {
            every { firestore.collection("users") } returns collection
            every { collection.document("u3") } returns document
            coEvery { document.get() } throws IOException("io")

            val res = dataSource.isFirstSignIn("u3")

            res.shouldBeInstanceOf<AppResult.Failure<AppError>>()
            res.error shouldBe AppError.Network.Unreachable
        }
    }

    test("fetchFirebaseUserInfo returns Success(null) when no current user") {
        runTest {
            every { auth.currentUser } returns null

            val res = dataSource.fetchFirebaseUserInfo()

            res.shouldBeInstanceOf<AppResult.Success<RemoteAuthUser?>>()
            res.data shouldBe null
        }
    }

    test("fetchFirebaseUserInfo returns mapped AuthFirebaseUser when current user present") {
        runTest {
            every { auth.currentUser } returns user
            every { user.uid } returns "U9"
            every { user.email } returns "u9@acme.io"
            val infoEmail = mockk<UserInfo> { every { providerId } returns "password" }
            val infoFirebase = mockk<UserInfo> { every { providerId } returns "firebase" }
            every { user.providerData } returns listOf(infoFirebase, infoEmail)

            val res = dataSource.fetchFirebaseUserInfo()

            res.shouldBeInstanceOf<AppResult.Success<RemoteAuthUser?>>()
            val data = res.data!!
            data.uid shouldBe "U9"
            data.email shouldBe "u9@acme.io"
            data.providers.shouldContainExactly(listOf("password"))
        }
    }
})
