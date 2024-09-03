package com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils

/* class GoogleAuthUIClient
@Inject
constructor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient
) {
    suspend fun signInWithGoogle(): IntentSender? {
        val result =
            try {
                oneTapClient.beginSignIn(buildSignInRequest()).await()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("HMM", "Error while trying to signIn with Google: ${e.message}")
                null
            }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResultModel {
        val credentials = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credentials.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResultModel(
                data =
                user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                },
                errorMessage = null,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResultModel(
                data = null,
                errorMessage = e.message,
            )
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build(),
            )
            .setAutoSelectEnabled(true)
            .build()
    }
} */
