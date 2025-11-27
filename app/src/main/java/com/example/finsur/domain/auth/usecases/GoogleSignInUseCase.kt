package com.example.finsur.domain.auth.usecases

import android.content.Context
import com.example.finsur.core.auth.GoogleAuthManager
import com.example.finsur.core.auth.GoogleSignInResult
import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.auth.repository.Result
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val googleAuthManager: GoogleAuthManager,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(activityContext: Context): Result<User> {
        return when (val result = googleAuthManager.signIn(activityContext)) {
            is GoogleSignInResult.Success -> {
                // Send ID token to backend for verification
                authRepository.loginWithGoogle(result.idToken)
            }
            is GoogleSignInResult.Error -> {
                Result.Error(
                    Exception("Google Sign-In failed"),
                    result.message
                )
            }
            is GoogleSignInResult.Cancelled -> {
                Result.Error(
                    Exception("Google Sign-In cancelled"),
                    "Inicio de sesión cancelado"
                )
            }
        }
    }
}
