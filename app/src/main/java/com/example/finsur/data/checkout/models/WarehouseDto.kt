package com.example.finsur.data.checkout.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: AddressDto,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("schedule")
    val schedule: String,
    @SerialName("is_active")
    val isActive: Boolean
)
