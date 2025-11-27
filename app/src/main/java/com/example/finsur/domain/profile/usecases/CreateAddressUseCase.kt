package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class CreateAddressUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        title: String,
        addressLine1: String,
        addressLine2: String?,
        country: String,
        city: String,
        state: String,
        postalCode: String,
        landmark: String?,
        phoneNumber: String
    ): Result<Address> {
        // Validation
        if (title.isBlank()) {
            return Result.Error(Exception("Title required"), "El título es requerido")
        }
        if (addressLine1.isBlank()) {
            return Result.Error(Exception("Address required"), "La dirección es requerida")
        }
        if (city.isBlank()) {
            return Result.Error(Exception("City required"), "La ciudad es requerida")
        }
        if (state.isBlank()) {
            return Result.Error(Exception("State required"), "El estado es requerido")
        }
        if (postalCode.isBlank()) {
            return Result.Error(Exception("Postal code required"), "El código postal es requerido")
        }
        if (phoneNumber.isBlank()) {
            return Result.Error(Exception("Phone required"), "El teléfono es requerido")
        }

        return repository.createAddress(
            title, addressLine1, addressLine2, country,
            city, state, postalCode, landmark, phoneNumber
        )
    }
}
