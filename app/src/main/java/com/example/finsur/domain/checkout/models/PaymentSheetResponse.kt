package com.example.finsur.domain.checkout.models

data class PaymentSheetResponse(
    val customer: String,
    val customerSessionClientSecret: String,
    val orderId: Int,
    val orderNumber: String,
    val paymentIntent: String,
    val publishableKey: String
)
