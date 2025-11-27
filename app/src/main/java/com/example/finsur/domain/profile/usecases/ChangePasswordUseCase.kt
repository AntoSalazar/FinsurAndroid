package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        // Validation
        if (currentPassword.isBlank()) {
            return Result.Error(
                Exception("Current password required"),
                "La contraseña actual es requerida"
            )
        }
        if (newPassword.length < 8) {
            return Result.Error(
                Exception("Password too short"),
                "La nueva contraseña debe tener al menos 8 caracteres"
            )
        }

        return repository.changePassword(userId, currentPassword, newPassword)
    }
}
