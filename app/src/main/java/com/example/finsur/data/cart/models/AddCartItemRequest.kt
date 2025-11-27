package com.example.finsur.data.cart.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCartItemRequest(
    @SerialName("product_id")
    val productId: Int,
    @SerialName("sku_id")
    val skuId: Int,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: String
)
