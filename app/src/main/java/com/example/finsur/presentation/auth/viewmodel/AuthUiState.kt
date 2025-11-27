package com.example.finsur.presentation.auth.viewmodel

import com.example.finsur.domain.auth.models.User

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

data class RegisterFormState(
    val email: String = "",
    val fullName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val fullNameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)
