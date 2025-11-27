package com.example.finsur.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val userId: Int,
    val permissions: List<String>
)
