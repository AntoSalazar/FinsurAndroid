package com.example.finsur.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.finsur.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Google Authentication Manager using Credential Manager API
 * Gets ID token from Google and sends it to backend for verification
 */
class GoogleAuthManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    private val credentialManager = CredentialManager.create(applicationContext)

    suspend fun signIn(activityContext: Context): GoogleSignInResult {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext,
            )

            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            GoogleSignInResult.Success(idToken)
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Error("Error al iniciar sesión con Google: ${e.message}")
        } catch (e: Exception) {
            GoogleSignInResult.Error("Error inesperado: ${e.message}")
        }
    }
}

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
    data object Cancelled : GoogleSignInResult()
}
