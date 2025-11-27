package com.example.finsur.data.cart.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCartItemRequest(
    val quantity: Int
)
