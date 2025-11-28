package com.example.finsur.domain.checkout.models

data class Warehouse(
    val id: Int,
    val name: String,
    val address: Address,
    val phoneNumber: String,
    val schedule: String,
    val isActive: Boolean
)
