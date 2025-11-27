package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(addressId: Int): Result<Unit> {
        return repository.deleteAddress(addressId)
    }
}
