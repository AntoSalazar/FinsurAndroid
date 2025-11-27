package com.example.finsur.domain.cart.models

data class Cart(
    val id: Int,
    val userId: Int?,
    val sessionId: String?,
    val subtotal: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val shippingAmount: Double,
    val total: Double,
    val couponCode: String?,
    val items: List<CartItem> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val expiresAt: String?
)
