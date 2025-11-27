package com.example.finsur.domain.auth.usecases

import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.auth.repository.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Add validation logic here if needed
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(
                Exception("Validation failed"),
                "El correo electrónico y la contraseña son requeridos"
            )
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error(
                Exception("Invalid email"),
                "Por favor ingresa un correo electrónico válido"
            )
        }

        return authRepository.login(email, password)
    }
}
