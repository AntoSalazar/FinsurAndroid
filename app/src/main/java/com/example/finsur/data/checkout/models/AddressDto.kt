package com.example.finsur.data.checkout.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    @SerialName("address_line_1")
    val addressLine1: String,
    @SerialName("address_line_2")
    val addressLine2: String?,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("postal_code")
    val postalCode: String
)
