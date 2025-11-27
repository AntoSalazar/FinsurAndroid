package com.example.finsur.data.auth.repository

import com.example.finsur.core.auth.AuthStateManager
import com.example.finsur.core.network.CookieJarImpl
import com.example.finsur.data.auth.models.LoginRequest
import com.example.finsur.data.auth.models.RegisterRequest
import com.example.finsur.data.auth.models.UserDto
import com.example.finsur.data.auth.remote.AuthApiService
import com.example.finsur.domain.auth.models.User
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.auth.repository.Result
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val cookieJar: CookieJarImpl,
    private val authStateManager: AuthStateManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                // After successful login, get user profile
                val userResult = getUserProfile()
                if (userResult is Result.Success) {
                    // Save authentication state
                    authStateManager.setAuthenticated(userResult.data.id, email)
                    android.util.Log.d("AuthRepository", "Login successful, auth state saved")
                }
                userResult
            } else {
                Result.Error(
                    Exception("Login failed"),
                    response.errorBody()?.string() ?: "Error desconocido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión. Por favor, verifica tu conexión a internet.")
        }
    }

    override suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        username: String,
        birthDate: String?,
        phoneNumber: String?,
        isActive: Boolean
    ): Result<User> {
        return try {
            val request = RegisterRequest(
                email = email,
                first_name = firstName,
                last_name = lastName,
                password = password,
                username = username,
                birth_date = birthDate,
                phone_number = phoneNumber,
                is_active = isActive
            )
            val response = authApiService.register(request)
            if (response.isSuccessful) {
                // After successful registration, get user profile
                val userResult = getUserProfile()
                if (userResult is Result.Success) {
                    // Save authentication state
                    authStateManager.setAuthenticated(userResult.data.id, email)
                    android.util.Log.d("AuthRepository", "Registration successful, auth state saved")
                }
                userResult
            } else {
                Result.Error(
                    Exception("Registration failed"),
                    response.errorBody()?.string() ?: "Error desconocido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión. Por favor, verifica tu conexión a internet.")
        }
    }

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = authApiService.getUserProfile()
            android.util.Log.d("AuthRepository", "getUserProfile response code: ${response.code()}")
            if (response.isSuccessful) {
                val userDto = response.body()
                android.util.Log.d("AuthRepository", "getUserProfile body: $userDto")
                if (userDto != null) {
                    Result.Success(userDto.toDomain())
                } else {
                    Result.Error(Exception("No user data"), "No se encontraron datos del usuario")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error al obtener perfil"
                android.util.Log.e("AuthRepository", "getUserProfile error: $errorBody")
                Result.Error(
                    Exception("Failed to get user profile"),
                    errorBody
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "getUserProfile exception", e)
            Result.Error(e, "Error de conexión: ${e.message}")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val request = com.example.finsur.data.auth.models.GoogleAuthRequest(idToken)
            val response = authApiService.loginWithGoogle(request)
            if (response.isSuccessful) {
                // After successful Google login, get user profile
                val userResult = getUserProfile()
                if (userResult is Result.Success) {
                    // Save authentication state
                    authStateManager.setAuthenticated(userResult.data.id, userResult.data.email)
                    android.util.Log.d("AuthRepository", "Google login successful, auth state saved")
                }
                userResult
            } else {
                Result.Error(
                    Exception("Google login failed"),
                    response.errorBody()?.string() ?: "Error al iniciar sesión con Google"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión. Por favor, verifica tu conexión a internet.")
        }
    }

    override suspend fun logout() {
        android.util.Log.d("AuthRepository", "Logging out - clearing cookies and auth state")
        cookieJar.clearCookies()
        authStateManager.clearAuthentication()
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = userId,
            username = username,
            email = email ?: "",
            permissions = permissions
        )
    }
}
