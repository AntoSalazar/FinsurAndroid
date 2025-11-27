package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        addressId: Int,
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

        return repository.updateAddress(
            addressId, title, addressLine1, addressLine2, country,
            city, state, postalCode, landmark, phoneNumber
        )
    }
}
