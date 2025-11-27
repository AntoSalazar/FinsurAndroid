package com.example.finsur.domain.auth.models

data class User(
    val id: Int,
    val username: String?,
    val email: String,
    val permissions: List<String>
)
