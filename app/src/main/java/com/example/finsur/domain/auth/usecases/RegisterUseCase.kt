package com.example.finsur.domain.auth.usecases

import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.auth.repository.Result
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        fullName: String,
        password: String,
        isActive: Boolean = true
    ): Result<User> {
        // Validation
        if (email.isBlank() || fullName.isBlank() || password.isBlank()) {
            return Result.Error(
                Exception("Validation failed"),
                "Todos los campos obligatorios deben ser completados"
            )
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error(
                Exception("Invalid email"),
                "Por favor ingresa un correo electrónico válido"
            )
        }

        if (password.length < 6) {
            return Result.Error(
                Exception("Password too short"),
                "La contraseña debe tener al menos 6 caracteres"
            )
        }

        // Split fullName into firstName and lastName
        val nameParts = fullName.trim().split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        // Generate username from email (part before @)
        val username = email.substringBefore("@")

        return authRepository.register(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            username = username,
            birthDate = null,
            phoneNumber = null,
            isActive = isActive
        )
    }
}
