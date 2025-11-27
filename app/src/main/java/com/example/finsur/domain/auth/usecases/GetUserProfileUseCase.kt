package com.example.finsur.domain.auth.usecases

import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.auth.repository.Result
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getUserProfile()
    }
}
