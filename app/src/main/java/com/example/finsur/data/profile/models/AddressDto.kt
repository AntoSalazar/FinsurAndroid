package com.example.finsur.data.profile.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    val title: String,
    @SerialName("address_line_1")
    val addressLine1: String,
    @SerialName("address_line_2")
    val addressLine2: String? = null,
    val country: String,
    val city: String,
    val state: String,
    @SerialName("postal_code")
    val postalCode: String,
    val landmark: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("is_default")
    val isDefault: Boolean
)

@Serializable
data class CreateAddressRequest(
    val title: String,
    @SerialName("address_line_1")
    val addressLine1: String,
    @SerialName("address_line_2")
    val addressLine2: String?,
    val country: String,
    val city: String,
    val state: String,
    @SerialName("postal_code")
    val postalCode: String,
    val landmark: String?,
    @SerialName("phone_number")
    val phoneNumber: String
)

@Serializable
data class UpdateAddressRequest(
    val title: String,
    @SerialName("address_line_1")
    val addressLine1: String,
    @SerialName("address_line_2")
    val addressLine2: String?,
    val country: String,
    val city: String,
    val state: String,
    @SerialName("postal_code")
    val postalCode: String,
    val landmark: String?,
    @SerialName("phone_number")
    val phoneNumber: String
)
