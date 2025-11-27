package com.example.finsur.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(
    val idToken: String
)
