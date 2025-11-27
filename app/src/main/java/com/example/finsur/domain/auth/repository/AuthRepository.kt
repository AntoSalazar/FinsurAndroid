package com.example.finsur.domain.auth.repository

import com.example.finsur.domain.auth.models.User

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
}

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        username: String,
        birthDate: String?,
        phoneNumber: String?,
        isActive: Boolean
    ): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun getUserProfile(): Result<User>
    suspend fun logout()
}
