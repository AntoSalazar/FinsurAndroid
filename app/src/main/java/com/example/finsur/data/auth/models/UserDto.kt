package com.example.finsur.data.auth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")
    val userId: Int,
    val username: String?,
    val email: String? = null,
    val permissions: List<String> = emptyList()
)
