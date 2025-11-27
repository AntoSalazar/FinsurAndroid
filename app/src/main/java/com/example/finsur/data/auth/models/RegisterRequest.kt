package com.example.finsur.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val username: String,
    val birth_date: String? = null,
    val phone_number: String? = null,
    val is_active: Boolean = true
)
