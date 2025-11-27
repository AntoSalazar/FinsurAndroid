package com.example.finsur.domain.profile.models

data class Address(
    val id: Int,
    val userId: Int,
    val title: String,
    val addressLine1: String,
    val addressLine2: String?,
    val country: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val landmark: String?,
    val phoneNumber: String,
    val isDefault: Boolean
)
