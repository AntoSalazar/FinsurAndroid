package com.example.finsur.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.Result
import com.example.finsur.domain.auth.usecases.GetUserProfileUseCase
import com.example.finsur.domain.auth.usecases.LoginUseCase
import com.example.finsur.domain.auth.usecases.LogoutUseCase
import com.example.finsur.domain.auth.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val googleSignInUseCase: com.example.finsur.domain.auth.usecases.GoogleSignInUseCase
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()

    private val _registerFormState = MutableStateFlow(RegisterFormState())
    val registerFormState: StateFlow<RegisterFormState> = _registerFormState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> {
                    _currentUser.value = result.data
                    _authUiState.value = AuthUiState.Success(result.data)
                }
                is Result.Error -> {
                    _currentUser.value = null
                    _authUiState.value = AuthUiState.Initial
                }
            }
        }
    }

    fun login() {
        val formState = _loginFormState.value
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val result = loginUseCase(formState.email, formState.password)) {
                is Result.Success -> {
                    _currentUser.value = result.data
                    _authUiState.value = AuthUiState.Success(result.data)
                    clearLoginForm()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(
                        result.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    fun register() {
        val formState = _registerFormState.value

        // Validate confirm password
        if (formState.password != formState.confirmPassword) {
            _registerFormState.value = formState.copy(
                confirmPasswordError = "Las contraseñas no coinciden"
            )
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val result = registerUseCase(
                email = formState.email,
                fullName = formState.fullName,
                password = formState.password
            )) {
                is Result.Success -> {
                    _currentUser.value = result.data
                    _authUiState.value = AuthUiState.Success(result.data)
                    clearRegisterForm()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(
                        result.message ?: "Error al registrarse"
                    )
                }
            }
        }
    }

    fun loginWithGoogle(activityContext: android.content.Context) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val result = googleSignInUseCase(activityContext)) {
                is Result.Success -> {
                    _currentUser.value = result.data
                    _authUiState.value = AuthUiState.Success(result.data)
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(
                        result.message ?: "Error al iniciar sesión con Google"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _currentUser.value = null
            _authUiState.value = AuthUiState.Initial
        }
    }

    // Login form updates
    fun updateLoginEmail(email: String) {
        _loginFormState.value = _loginFormState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun updateLoginPassword(password: String) {
        _loginFormState.value = _loginFormState.value.copy(
            password = password,
            passwordError = null
        )
    }

    private fun clearLoginForm() {
        _loginFormState.value = LoginFormState()
    }

    // Register form updates
    fun updateRegisterEmail(email: String) {
        _registerFormState.value = _registerFormState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun updateRegisterFullName(fullName: String) {
        _registerFormState.value = _registerFormState.value.copy(
            fullName = fullName,
            fullNameError = null
        )
    }

    fun updateRegisterPassword(password: String) {
        _registerFormState.value = _registerFormState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerFormState.value = _registerFormState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    private fun clearRegisterForm() {
        _registerFormState.value = RegisterFormState()
    }

    fun resetAuthState() {
        _authUiState.value = AuthUiState.Initial
    }
}
