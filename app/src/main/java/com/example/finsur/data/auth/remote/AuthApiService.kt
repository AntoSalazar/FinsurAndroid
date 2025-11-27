package com.example.finsur.data.auth.remote

import com.example.finsur.data.auth.models.AuthResponse
import com.example.finsur.data.auth.models.GoogleAuthRequest
import com.example.finsur.data.auth.models.LoginRequest
import com.example.finsur.data.auth.models.RegisterRequest
import com.example.finsur.data.auth.models.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<AuthResponse>

    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserDto>

    @POST("auth/google/mobile")
    suspend fun loginWithGoogle(
        @Body googleAuthRequest: GoogleAuthRequest
    ): Response<AuthResponse>
}
