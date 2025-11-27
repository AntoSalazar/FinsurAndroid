package com.example.finsur.data.cart.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: Int,
    @SerialName("product_id")
    val productId: Int,
    @SerialName("sku_id")
    val skuId: Int,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: String,
    val product: CartProductDto
)

@Serializable
data class CartProductDto(
    val id: Int,
    val name: String,
    val cover: String? = null,
    val slug: String
)
