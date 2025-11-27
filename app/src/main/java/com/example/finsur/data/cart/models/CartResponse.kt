package com.example.finsur.data.cart.models

import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val message: String? = null,
    val item: CartItemDto? = null,
    val cart: CartDto
)
