package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.UserProfile
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getUserProfile()
    }
}
