package com.example.finsur.domain.checkout.models

data class Address(
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val postalCode: String
)
