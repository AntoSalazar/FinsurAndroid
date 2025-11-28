package com.example.finsur.data.checkout.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentSheetResponseDto(
    @SerialName("customer")
    val customer: String,
    @SerialName("customerSessionClientSecret")
    val customerSessionClientSecret: String,
    @SerialName("orderId")
    val orderId: Int,
    @SerialName("orderNumber")
    val orderNumber: String,
    @SerialName("paymentIntent")
    val paymentIntent: String,
    @SerialName("publishableKey")
    val publishableKey: String
)
