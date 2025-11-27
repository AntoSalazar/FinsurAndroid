package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.UserProfile
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdatePersonalInfoUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): Result<UserProfile> {
        // Validation
        if (firstName.isBlank()) {
            return Result.Error(Exception("Nombre requerido"), "El nombre es requerido")
        }
        if (lastName.isBlank()) {
            return Result.Error(Exception("Apellido requerido"), "El apellido es requerido")
        }

        return repository.updatePersonalInfo(userId, firstName, lastName, phoneNumber)
    }
}
