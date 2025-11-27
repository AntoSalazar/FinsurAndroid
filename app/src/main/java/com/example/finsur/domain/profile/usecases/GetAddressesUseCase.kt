package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<Address>> {
        return repository.getAddresses()
    }
}
