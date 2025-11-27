package com.example.finsur.data.cart.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartDto(
    val id: Int,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("session_id")
    val sessionId: String? = null,
    val subtotal: Double,
    @SerialName("tax_amount")
    val taxAmount: Double,
    @SerialName("discount_amount")
    val discountAmount: Double,
    @SerialName("shipping_amount")
    val shippingAmount: Double,
    val total: Double,
    @SerialName("coupon_code")
    val couponCode: String? = null,
    val items: List<CartItemDto>? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("expires_at")
    val expiresAt: String? = null
)
