package com.example.finsur.data.checkout.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentSheetRequestDto(
    @SerialName("shipping_method")
    val shippingMethod: String,
    @SerialName("shipping_address_id")
    val shippingAddressId: Int? = null,
    @SerialName("warehouse_id")
    val warehouseId: Int? = null,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("coupon_code")
    val couponCode: String? = null
)
